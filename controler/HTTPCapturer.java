package controler;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.AbstractMessageHeader.MessageType;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

import model.Model;

public class HTTPCapturer implements PcapPacketHandler<String> {
	private Model model;
	private Tcp tcp;
	private Http http;
	private Pcap pcap;
	
	public HTTPCapturer(Model model) {
		this.model = model;
		this.tcp = new Tcp();  
        this.http = new Http();
	}
	
	/**
	 * Start the capture of HTTP packets
	 * @param device The network device to listen
	 * @throws Exception If there's an error with the opening of the capture
	 */
	public void start(PcapIf device) throws Exception {
		int snaplen = 64 * 1024;
        int flags = Pcap.MODE_PROMISCUOUS;
        int timeout = 10 * 1000;
        StringBuilder buff = new StringBuilder();
        
        pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, buff);
        
        if (pcap == null)
			throw new Exception("Unable to start the http capture (" + buff + ")");
        
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				pcap.loop(Pcap.LOOP_INFINITE, HTTPCapturer.this, "");
		        pcap.close();  
		        pcap = null;
			}
		}).start();
        
	}
	
	/**
	 * Stop the capture
	 */
	public void stop() {
		if (pcap != null)
			pcap.breakloop();
	}


	@Override
	public void nextPacket(PcapPacket packet, String arg1) {
        if (packet.hasHeader(tcp) && packet.hasHeader(http)) {
        	if (http.getMessageType() == MessageType.REQUEST)
        		System.out.println(http.fieldValue(Http.Request.RequestUrl));
        }
		
	}
}
