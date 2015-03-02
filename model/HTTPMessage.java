package model;

import java.util.ArrayList;

import org.jnetpcap.packet.PcapPacket;

public class HTTPMessage {
	private String url;
	private PcapPacket request;
	private ArrayList<PcapPacket> responses;
	
	public HTTPMessage(String url, PcapPacket request) {
		this.url = url;
		this.request = request;
		this.responses = new ArrayList<PcapPacket>();
	}
	
	public void addResponse(PcapPacket packet) {
		responses.add(packet);
	}
	
	@Override
	public String toString() {
		return " (" + responses.size() + ") " + url;
	}

}
