package model;

import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class NetworkDevice {
	private static List<PcapIf> devices;
	private int selectedDevice;
	
	public NetworkDevice() throws Exception {
		findAllDevices();
		selectedDevice = 0;
	}
	
	private void findAllDevices() throws Exception {
		if (devices != null) return;
		
		devices = new ArrayList<PcapIf>();
		StringBuilder buff = new StringBuilder();

        if (Pcap.findAllDevs(devices, buff) == Pcap.NOT_OK || devices.isEmpty()) {  
        	throw new Exception("Unable to find the network devices (" + buff + ")");  
        }
	}
	
	public List<String> getDevicesName() {
		List<String> names = new ArrayList<String>();
		
		for (PcapIf device : devices) 
			names.add(device.getDescription());
				
		return names;
	}
	
	public void setSelectedDevice(int idx) {
		selectedDevice = idx;
	}
	
	public PcapIf getSelectedDevice() {
		return devices.get(selectedDevice);
	}
	
	public int getSelectedIndex() {
		return selectedDevice;
	}
}
