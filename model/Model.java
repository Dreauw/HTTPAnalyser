package model;

import java.util.List;
import java.util.Observable;

public class Model extends Observable {
	private NetworkDevice networkDevice;
	
	public Model() {
		try {
			networkDevice = new NetworkDevice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the name of all the network devices available
	 * @return A list with the names
	 */
	public List<String> getDevicesName() {
		return networkDevice.getDevicesName();
	}
}
