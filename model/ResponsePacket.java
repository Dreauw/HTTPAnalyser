package model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

public class ResponsePacket {
	private PcapPacket packet;
	private long id;
	
	private ResponsePacket previous;
	private ResponsePacket next;
	
	
	public ResponsePacket(PcapPacket packet, Tcp tcp) {
		this.packet = packet;
		this.id = tcp.seq();
	}


	public PcapPacket getPacket() {
		return packet;
	}

	public long getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return packet.toString();
	}


	public ResponsePacket getPrevious() {
		return previous;
	}


	public void setPrevious(ResponsePacket previous) {
		this.previous = previous;
	}


	public ResponsePacket getNext() {
		return next;
	}


	public void setNext(ResponsePacket next) {
		this.next = next;
	}
}
