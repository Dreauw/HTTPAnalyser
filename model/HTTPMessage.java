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
	private List<IpPacket> responses;
	
	public HTTPMessage(String url, PcapPacket request) {
		this.url = url;
		this.request = request;
		this.responses = new ArrayList<IpPacket>();
	}
	
	public PcapPacket getRequest() {
		return request;
	}
	
	public void addResponse(IpPacket packet) {
		responses.add(packet);
	}
	
	/**
	 * Sort the fragmented packet representing the response in the right order
	 */
	public void sortResponses() {
		Collections.sort(responses, new Comparator<IpPacket>() {

			@Override
			public int compare(IpPacket p1, IpPacket p2) {
				return p1.getId() - p2.getId();
			}
			
		});
	}
	
	public List<IpPacket> getResponses() {
		return responses;
	}
	
	@Override
	public String toString() {
		return " (" + responses.size() + ") " + url;
	}

}
