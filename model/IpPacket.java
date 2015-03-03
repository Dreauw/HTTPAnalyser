package model;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;

public class IpPacket {
	private PcapPacket packet;
	private int id;
	
	
	public IpPacket(PcapPacket packet, Ip4 ip4) {
		this.packet = packet;
		this.id = ip4.id();
	}


	public PcapPacket getPacket() {
		return packet;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return packet.toString();
	}
}
