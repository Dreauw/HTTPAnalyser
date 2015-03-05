package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import model.Model;
import model.ModelMessage;

public class DownloadManager extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 200;
	
	private Model model;
	private JPanel panel;
	private JScrollPane scrollPane;
	
	class Element {
		private JProgressBar progressBar;
		private TitledBorder border;
		
		public Element(JProgressBar bar, TitledBorder border) {
			this.progressBar = bar;
			this.border = border;
		}
		
		public JProgressBar getProgressBar() {
			return progressBar;
		}
		
		public TitledBorder getBorder() {
			return border;
		}
	}
	
	private HashMap<String, Element> elements;
	
	public DownloadManager(Model model) {
		super("Downloads");
		
		this.model = model;
		model.addObserver(this);
		
		elements = new HashMap<String, Element>();
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		this.setContentPane(scrollPane = new JScrollPane(panel));
		
		scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	
	public void addDownload(String url) {
		JPanel subPanel = new JPanel(new BorderLayout());
		
		TitledBorder border = BorderFactory.createTitledBorder(url);
		subPanel.setBorder(border);
		subPanel.setPreferredSize(new Dimension(1, 40));
		subPanel.setMaximumSize(new Dimension(9999, 40));
		subPanel.setMinimumSize(new Dimension(1, 40));
		
		JProgressBar bar = new JProgressBar();
		elements.put(url, new Element(bar, border));
		
		subPanel.add(bar, BorderLayout.CENTER);
		
		panel.add(subPanel);
		
		panel.revalidate();
		
		validate();
		repaint();
	}


	@Override
	public void update(Observable o, Object obj) {
		ModelMessage msg = (ModelMessage) obj;
		
		if (msg.getType() == ModelMessage.TYPE.ADD_DOWNLOAD) {
			addDownload((String)msg.getData());
		} else if (msg.getType() == ModelMessage.TYPE.UPDATE_DOWNLOAD) {
			Object[] datas = (Object[]) msg.getData();
			Element element = elements.get(datas[0]);
			element.getBorder().setTitle(datas[0] + " (" + datas[1] + ")");
			element.getProgressBar().setMaximum(1000);
			element.getProgressBar().setValue((Integer)datas[2]);
			repaint();
		}
	}
	
	

}