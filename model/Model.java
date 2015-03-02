package model;

import java.util.List;
import java.util.Observable;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class Model extends Observable {
	private NetworkDevice networkDevice;
	private Pcap pcap;
	
	public Model() {
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
		return networkDevice.getSelectedDevice();
	}
}
