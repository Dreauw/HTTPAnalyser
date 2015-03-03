package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import model.HTTPMessage;
import model.Model;
import model.ModelMessage;

public class PacketList extends JScrollPane implements Observer {
	private static final long serialVersionUID = 1L;
	
	private JList list;
	private JPopupMenu popupMenu;
	private JMenuItem showHTTPReq, showHTTPRes, saveResponse, downloadURL;
	
	public PacketList(Model model) {
		super();
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
		popupMenu.add(showHTTPReq = new JMenuItem("Show HTTP Request..."));
		popupMenu.add(showHTTPRes = new JMenuItem("Show HTTP Response..."));
		popupMenu.addSeparator();
		popupMenu.add(saveResponse = new JMenuItem("Save HTTP Response Content..."));
		popupMenu.add(downloadURL = new JMenuItem("Download the requested URL..."));
		
		// Listeners
		showHTTPReq.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(getSelectedHTTPMessage().getRequest());
			}
		});
		
		showHTTPRes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(getSelectedHTTPMessage().getResponses().get(0));
			}
		});
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
