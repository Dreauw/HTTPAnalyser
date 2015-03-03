package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.DefaultListModel;

import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

public class Model extends Observable {
	private NetworkDevice networkDevice;
	private boolean capturing;
	private DefaultListModel packets;
	
	public Model() {
		capturing = false;
		packets = new DefaultListModel();
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
		packets.addElement(httpMsg);
		
		this.setChanged();
		this.notifyObservers(new ModelMessage(ModelMessage.TYPE.PACKET_ADDED, httpMsg));
	}
	
	/**
	 * Update the packets
	 */
	public void updatePackets() {
		this.setChanged();
		this.notifyObservers(new ModelMessage(ModelMessage.TYPE.PACKET_UPDATED));
	}
	
	/**
	 * Clear all the captured packets
	 */
	public void clearPackets() {
		packets.clear();
		
		this.setChanged();
		this.notifyObservers(new ModelMessage(ModelMessage.TYPE.PACKET_CLEARED));
	}
	
	/**
	 * Return a DefaultListModel with all the captured HTTPMessage
	 */
	public DefaultListModel getHTTPPackets() {
		return packets;
	}
	
	
	/**
	 * Write the content of an HTTP response inside of a file
	 * @param file The file to write the content
	 * @param message The HTTP message to save
	 */
	public void saveHTTPResponseContent(File file, HTTPMessage message) {
		// First, we sort all the responses in the right order (based on the IP4 header's id)
		message.sortResponses();
		
		Http http = new Http();
		Tcp tcp = new Tcp();
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			
			// Loop trought all the responses
			int size =  message.getResponses().size();
			for (int i = 0; i < size; ++i) {
				PcapPacket packet = message.getResponses().get(i).getPacket();
				// Write the payload of the fragment (if there's one)
				if (packet.hasHeader(http)) {
					// Remove the http header
					out.write(http.getPayload());
				} else if (packet.hasHeader(tcp)) {
					// No http dg, so the payload is in the tcp dg
					out.write(tcp.getPayload());
				};
			}
		} catch (Exception e) {
			this.setChanged();
			this.notifyObservers(new ModelMessage(ModelMessage.TYPE.ERROR, e.getMessage()));
		} finally {
			try {
				// Always close the file
				if (out != null) out.close();
			} catch (IOException e) {
				this.setChanged();
				this.notifyObservers(new ModelMessage(ModelMessage.TYPE.ERROR, e.getMessage()));
			}
		}
	}
}
