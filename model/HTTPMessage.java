package model;

import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.packet.PcapPacket;

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
	
	public List<PcapPacket> getResponses() {
		return responses;
	}
	
	@Override
	public String toString() {
		return " (" + responses.size() + ") " + url;
	}

}
