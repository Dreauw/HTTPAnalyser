package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jnetpcap.packet.PcapPacket;

import model.HTTPMessage;
import model.IpPacket;
import model.Model;
import model.ModelMessage;

public class PacketList extends JScrollPane implements Observer {
	private static final long serialVersionUID = 1L;
	
	private JList list;
	private JPopupMenu popupMenu;
	private Model model;
	private JMenuItem showHTTPReq, showHTTPRes, saveResponse, downloadURL;
	
	public PacketList(Model model) {
		super();
		
		this.model = model;
		model.addObserver(this);
		
		generatePopupMenu();
		
		list = new JList(model.getHTTPPackets());
		list.setLayoutOrientation(JList.VERTICAL);
		
		list.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {onMouseClick(e);}
			@Override
			public void mousePressed(MouseEvent e) {onMouseClick(e);}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		setViewportView(list);
	}
	
	/**
	 * Generate the popup menu (opened on right click)
	 */
	private void generatePopupMenu() {
		popupMenu = new JPopupMenu();
		popupMenu.add(showHTTPReq = new JMenuItem("Show request packet..."));
		popupMenu.add(showHTTPRes = new JMenuItem("Show response packet(s)..."));
		popupMenu.addSeparator();
		popupMenu.add(saveResponse = new JMenuItem("Save HTTP Response Content..."));
		popupMenu.add(downloadURL = new JMenuItem("Download the requested URL..."));
		
		// Listeners
		showHTTPReq.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showText(getSelectedHTTPMessage().getRequest().toString());
			}
		});
		
		showHTTPRes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedHTTPMessage().sortResponses();
				String p = "";
				for (IpPacket packet : getSelectedHTTPMessage().getResponses())
					p += packet.toString();

				showText(p);
			}
		});
	}
	
	private void showText(String text) {
		JTextArea textArea = new JTextArea(text);
		JScrollPane scrollPane = new JScrollPane(textArea);  
		textArea.setLineWrap(true);  
		textArea.setWrapStyleWord(true); 
		scrollPane.setPreferredSize( new Dimension( 500, 500 ) );
		JOptionPane.showMessageDialog(this, scrollPane, "Dialog", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Return the selected HTTPMessage
	 * @return An instance of HTTPMessage
	 */
	public HTTPMessage getSelectedHTTPMessage() {
		return (HTTPMessage) list.getSelectedValue();
	}
	
	/**
	 * Called when the mouse is pressed/released over an item in the list
	 */
	private void onMouseClick(MouseEvent e) {
		int index = list.locationToIndex(e.getPoint());
		if (index >= 0 && e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger()) {
			list.setSelectedIndex(index);
			
			// If there's no response, disable items that use them
			if (getSelectedHTTPMessage().getResponses().size() <= 0) {
				showHTTPRes.setEnabled(false);
				saveResponse.setEnabled(false);
			}
			
			// Show the popup
			popupMenu.show(list, e.getX(), e.getY());
		}
	}

	@Override
	public void update(Observable observable, Object obj) {
		ModelMessage msg = (ModelMessage) obj;
		
		if (msg.getType() == ModelMessage.TYPE.PACKET_UPDATED) {
			repaint();
		} else if (msg.getType() == ModelMessage.TYPE.PACKET_CLEARED) {
			//model.getHTTPPackets().clear();
		}
	}

}
