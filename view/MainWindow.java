package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controler.HTTPCapturer;

import model.Model;
import model.ModelMessage;

/**
 * MainWindow
 */
public class MainWindow extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 600;
	
	private Model model;
	
	private HTTPCapturer httpCapturer;
	private DownloadManager downloadManager;
	private JButton btnCapture, btnClear;
	private JComboBox comboBoxNetworkDevice;
	
	public MainWindow() {
		super("HTTPAnalyser");
		
		model = new Model();
		model.addObserver(this);
		
		downloadManager = new DownloadManager(model);
		
		httpCapturer = new HTTPCapturer(model);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		addCaptureButtons(panel);
		panel.add(new PacketList(model, httpCapturer), BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	/**
	 * Add the button to control (start/stop) the capture of http packets
	 * @param panel Panel to add the buttons on
	 */
	private void addCaptureButtons(JPanel panel) {
		comboBoxNetworkDevice = new JComboBox(model.getDevicesName().toArray());
		btnCapture = new JButton("Start");
		btnClear = new JButton("Clear");
		
		comboBoxNetworkDevice.setPreferredSize(new Dimension(200, 26));
		
		comboBoxNetworkDevice.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.selectNetworkDevice(comboBoxNetworkDevice.getSelectedIndex());
				if (model.isInCapture()) {
					JOptionPane.showMessageDialog(MainWindow.this, "You need to re-start the capture to use this device");
				}
			}
		});
		
		btnCapture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (model.isInCapture()) {
						httpCapturer.stop();
						httpCapturer = new HTTPCapturer(model);
					} else {
						httpCapturer.start(model.getSelectedDevice());
					}
				} catch (Exception e) {
					showErrorMessage(e.getMessage());
				}
			}
		});
		
		btnClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				httpCapturer.clear();
			}
		});
		
		JPanel subPanel = new JPanel();
		subPanel.add(comboBoxNetworkDevice);
		subPanel.add(btnCapture);
		subPanel.add(btnClear);
		panel.add(subPanel, BorderLayout.NORTH);
	}

	@Override
	public void update(Observable observable, Object obj) {
		ModelMessage msg = (ModelMessage) obj;
		// Show error message
		if (msg.getType() == ModelMessage.TYPE.ERROR) {
			showErrorMessage((String) msg.getData());
		} else if (msg.getType() == ModelMessage.TYPE.CAPTURE_STATE_CHANGED) {
			// Update the state of the button to control the capture
			btnCapture.setText(model.isInCapture() ? "Stop" : "Start");
		} else if (msg.getType() == ModelMessage.TYPE.ADD_DOWNLOAD) {
			downloadManager.setVisible(true);
		}
	}
	
	private void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	public static void main(String[] args) {
		new MainWindow();
	}
}
