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
	}
	
	@Override
	public String toString() {
		return url;
	}

}
