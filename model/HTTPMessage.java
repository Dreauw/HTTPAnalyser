package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jnetpcap.packet.PcapPacket;

public class HTTPMessage {
	private String url;
	private PcapPacket request;
	private ResponsePacket responseHead;
	private ResponsePacket responseTail;
	private int nbResponses;
	
	public HTTPMessage(String url, PcapPacket request) {
		this.url = url;
		this.request = request;
		this.nbResponses = 0;
	}
	
	public PcapPacket getRequest() {
		return request;
	}

	public void addResponse(ResponsePacket packet) {
		if (responseTail == null) {
			responseTail = responseHead = packet;
		} else {
			ResponsePacket predecessor = responseTail;
			// Insert the response in the right order (based on the id)
			while (predecessor != null && predecessor.getId() > packet.getId()) {
				predecessor = predecessor.getPrevious();
			}
			
			// Check that the packet is not already stored
			if (predecessor != null && predecessor.getId() == packet.getId()) {
				// The packet has been re-sent probably to fix an error, so we just replace the old one
				if (predecessor.getPrevious() != null) predecessor.getPrevious().setNext(packet);
				if (predecessor.getNext() != null) predecessor.getNext().setPrevious(packet);
				packet.setPrevious(predecessor.getPrevious());
				packet.setNext(predecessor.getNext());
				return;
			}
			
			if (predecessor == null) {
				packet.setNext(responseHead);
				responseHead.setPrevious(packet);
				responseHead = packet;
			} else {
				if (predecessor.getNext() == null) responseTail = packet;
				
				packet.setNext(predecessor.getNext());
				if (packet.getNext() != null) packet.getNext().setPrevious(packet);
				packet.setPrevious(predecessor);
				predecessor.setNext(packet);
			}
		}
		++nbResponses;
	}
	
	public ResponsePacket getResponse() {
		return responseHead;
	}
	
	public String getURL() {
		return url;
	}
	
	@Override
	public String toString() {
		return " (" + nbResponses + ") " + url;
	}
	
	/**
	 * Return the resource name from the URL
	 */
	public String getResourceName() {
		String name = url;
		int s = name.lastIndexOf('/');
		
		if (s > 0) name = name.substring(s+1);
		
		s = name.indexOf('?');
		if (s > 0) name = name.substring(0, s);
		
		return name;
	}

}
