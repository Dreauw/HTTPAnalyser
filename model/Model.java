package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Http;

public class Model extends Observable {
	private NetworkDevice networkDevice;
	private boolean capturing;
	private List<HTTPMessage> packets;
	
	public Model() {
		capturing = false;
		packets = new ArrayList<HTTPMessage>();
	}
	
	private void createNetworkDevice() {
		if (networkDevice != null) return;
		try {
			networkDevice = new NetworkDevice();
		} catch (Exception e) {
			this.setChanged();
			this.notifyObservers(new ModelMessage(ModelMessage.TYPE.ERROR, e.getMessage()));
		}
		networkDevice.setSelectedDevice(2);
	}
	
	/**
	 * Get the name of all the network devices available
	 * @return A list with the names
	 */
	public List<String> getDevicesName() {
		createNetworkDevice();
		return networkDevice.getDevicesName();
	}
	
	/**
	 * Get the selected device
	 * @return An PcapIf object representing the network device
	 */
	public PcapIf getSelectedDevice() {
		createNetworkDevice();
		return networkDevice.getSelectedDevice();
	}
	
	/**
	 * Define the state of the capture of HTTP packets
	 */
	public void setInCapture(boolean inCapture) {
		capturing = inCapture;
		this.setChanged();
		this.notifyObservers(new ModelMessage(ModelMessage.TYPE.CAPTURE_STATE_CHANGED));
	}
	
	/**
	 * Test if the capture of HTTP packets is in progress
	 */
	public boolean isInCapture() {
		return capturing;
	}
	
	/**
	 * Add an http packet to the list
	 */
	public void addPacket(HTTPMessage httpMsg) {
		packets.add(httpMsg);
		
		this.setChanged();
		this.notifyObservers(new ModelMessage(ModelMessage.TYPE.PACKET_ADDED, httpMsg.toString()));
	}
}
