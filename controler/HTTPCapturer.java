package controler;

import java.util.HashMap;
import java.util.List;

import javax.swing.SwingWorker;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.AbstractMessageHeader.MessageType;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

import model.HTTPMessage;
import model.Model;

public class HTTPCapturer extends SwingWorker<Void, HTTPMessage> implements PcapPacketHandler<String> {
	private Model model;
	private Tcp tcp;
	private Http http;
	private Pcap pcap;
	private HashMap<Integer, HTTPMessage> requests;
	
	public HTTPCapturer(Model model) {
		this.model = model;
		this.tcp = new Tcp();  
        this.http = new Http();
        this.requests = new HashMap<Integer, HTTPMessage>();
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
        PcapBpfProgram filter = new PcapBpfProgram();
        
        pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, buff);
        
        if (pcap == null)
			throw new Exception("Unable to start the http capture (" + buff + ")");
        
        // Process only tcp packets on port 80 (HTTP)
        if (pcap.compile(filter, "tcp port 80", 1, 0) != Pcap.OK)
        	throw new Exception("Unable to compile the filter (" + pcap.getErr() + ")");
        
        if (pcap.setFilter(filter) != Pcap.OK)
        	throw new Exception("Unable to set the filter (" + pcap.getErr() + ")");
        
        model.setInCapture(true);
        
        execute();
	}
	
	/**
	 * Stop the capture
	 */
	public void stop() {
		if (pcap != null)
			pcap.breakloop();
		model.setInCapture(false);
	}


	@Override
	public void nextPacket(PcapPacket packet, String arg1) {
        if (packet.hasHeader(tcp)) {
        	HTTPMessage associatedRequest = requests.get(tcp.destination());
        	if (associatedRequest != null) {
        		if (packet.hasHeader(http) && http.getMessageType() == MessageType.RESPONSE) {
        			// Add the response to HTTPMessage
        		}
        	} else if (packet.hasHeader(http) && http.getMessageType() == MessageType.REQUEST) {
        		String host = http.fieldValue(Http.Request.Host);
        		String url = http.fieldValue(Http.Request.RequestUrl);
        		if (url == null) url = "/";
        		
        		HTTPMessage httpMsg = new HTTPMessage(host + url, packet);
        		requests.put(tcp.source(), httpMsg);
        		
        		publish(httpMsg);
        	}
        }
	}

	@Override
	protected Void doInBackground() throws Exception {
		pcap.loop(Pcap.LOOP_INFINITE, HTTPCapturer.this, "");
        pcap.close();
        model.setInCapture(false);
        pcap = null;
		return null;
	}
	
	@Override
	protected void process(List<HTTPMessage> list) {
		for (HTTPMessage httpMsg : list)
			model.addPacket(httpMsg);
	}
}
