package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.network.Ip4;

public class HTTPMessage {
	private String url;
	private PcapPacket request;
	private List<PcapPacket> responses;
	
	public HTTPMessage(String url, PcapPacket request) {
		this.url = url;
		this.request = request;
		this.responses = new ArrayList<PcapPacket>();
	}
	
	public PcapPacket getRequest() {
		return request;
	}
	
	public void addResponse(PcapPacket packet) {
		responses.add(packet);
	}
	
	/**
	 * Sort the fragmented packet representing the response in the right order
	 */
	public void sortResponses() {
		Collections.sort(responses, new Comparator<PcapPacket>() {

			@Override
			public int compare(PcapPacket p1, PcapPacket p2) {
				Ip4 ip1 = new Ip4(), ip2 = new Ip4();
				if (p1.hasHeader(ip1) && p2.hasHeader(ip2)) {
					return ip1.id() - ip2.id();
				}
				return 0;
			}
			
		});
	}
	
	public List<PcapPacket> getResponses() {
		return responses;
	}
	
	@Override
	public String toString() {
		return " (" + responses.size() + ") " + url;
	}

}
