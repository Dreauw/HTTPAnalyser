package view;

import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import model.Model;
import model.ModelMessage;

public class PacketList extends JScrollPane implements Observer {
	private static final long serialVersionUID = 1L;
	
	private JList list;
	private DefaultListModel listModel;
	
	public PacketList(Model model) {
		super();
		model.addObserver(this);
		
		listModel = new DefaultListModel();
		
		list = new JList(listModel);
		list.setLayoutOrientation(JList.VERTICAL);
		
		setViewportView(list);
	}

	@Override
	public void update(Observable observable, Object obj) {
		ModelMessage msg = (ModelMessage) obj;
		
		if (msg.getType() == ModelMessage.TYPE.PACKET_ADDED) {
			listModel.addElement(msg.getData());
		} else if (msg.getType() == ModelMessage.TYPE.PACKET_CLEARED) {
			listModel.clear();
		}
	}

}
