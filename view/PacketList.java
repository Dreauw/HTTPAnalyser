package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jnetpcap.packet.PcapPacket;

import controler.HTTPCapturer;

import model.HTTPMessage;
import model.ResponsePacket;
import model.Model;
import model.ModelMessage;

public class PacketList extends JScrollPane implements Observer {
	private static final long serialVersionUID = 1L;
	
	private JList list;
	private JPopupMenu popupMenu;
	private Model model;
	private HTTPCapturer httpCapturer;
	private JMenuItem copyURL, removePacket, showHTTPReq, showHTTPRes, saveResponse, downloadURL;
	
	public PacketList(Model model, HTTPCapturer httpCapturer) {
		super();
		
		this.model = model;
		model.addObserver(this);
		
		this.httpCapturer = httpCapturer;
		
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
		popupMenu.add(copyURL = new JMenuItem("Copy URL"));
		popupMenu.add(removePacket = new JMenuItem("Remove"));
		popupMenu.addSeparator();
		popupMenu.add(showHTTPReq = new JMenuItem("Show request packet..."));
		popupMenu.add(showHTTPRes = new JMenuItem("Show response packet(s)..."));
		popupMenu.addSeparator();
		popupMenu.add(saveResponse = new JMenuItem("Save HTTP Response Content..."));
		popupMenu.add(downloadURL = new JMenuItem("Download the requested URL..."));
		
		// Listeners
		copyURL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(getSelectedHTTPMessage().getURL());
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
			}
		});
		
		removePacket.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				httpCapturer.removePacket(getSelectedHTTPMessage());
			}
		});
		
		showHTTPReq.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showText(getSelectedHTTPMessage().getRequest().toString());
			}
		});
		
		showHTTPRes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String p = "";
				ResponsePacket response = getSelectedHTTPMessage().getResponse();
				
				while (response != null) {
					PcapPacket packet = response.getPacket();
					p += packet.toString();
					response = response.getNext();
				}
				
				showText(p);
			}
		});
		
		saveResponse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSelectedFile(new File(getSelectedHTTPMessage().getResourceName()));
				
				if (fileChooser.showSaveDialog(PacketList.this) == JFileChooser.APPROVE_OPTION) {
					model.saveHTTPResponseContent(fileChooser.getSelectedFile(), getSelectedHTTPMessage());
				}
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
			showHTTPRes.setEnabled(getSelectedHTTPMessage().getResponse() != null);
			saveResponse.setEnabled(getSelectedHTTPMessage().getResponse() != null);
				
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
