package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
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
	private JButton btnCapture;
	
	public MainWindow() {
		super("HTTPAnalyser");
		
		model = new Model();
		model.addObserver(this);
		
		System.out.println(model.getDevicesName());
		
		httpCapturer = new HTTPCapturer(model);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		addCaptureButton(panel);
		panel.add(new PacketList(model), BorderLayout.CENTER);
		
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
	private void addCaptureButton(JPanel panel) {
		btnCapture = new JButton("Start");
		
		btnCapture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (model.isInCapture()) {
						httpCapturer.stop();
					} else {
						httpCapturer.start(model.getSelectedDevice());
					}
				} catch (Exception e) {
					showErrorMessage(e.getMessage());
				}
			}
		});
		
		panel.add(btnCapture, BorderLayout.NORTH);
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
		}
	}
	
	private void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	public static void main(String[] args) {
		new MainWindow();
	}
}
