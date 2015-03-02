package view;

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
	
	public MainWindow() {
		super("HTTPAnalyser");
		
		model = new Model();
		model.addObserver(this);
		
		System.out.println(model.getDevicesName());
		
		httpCapturer = new HTTPCapturer(model);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		addCaptureButtons(panel);
		
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	/**
	 * Add two buttons to control (start/stop) the capture of http packets
	 * @param panel Panel to add the buttons on
	 */
	private void addCaptureButtons(JPanel panel) {
		JButton btnStartCapture = new JButton("Start capture");
		JButton btnStopCapture = new JButton("Stop capture");
		
		btnStartCapture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					httpCapturer.start(model.getSelectedDevice());
				} catch (Exception e) {
					showErrorMessage(e.getMessage());
				}
			}
		});
		
		btnStopCapture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				httpCapturer.stop();
			}
		});
		
		panel.add(btnStartCapture);
		panel.add(btnStopCapture);
	}

	@Override
	public void update(Observable observable, Object obj) {
		ModelMessage msg = (ModelMessage) obj;
		
		// Show error message
		if (msg.getType() == ModelMessage.TYPE.ERROR) {
			showErrorMessage((String) msg.getData());
		}
	}
	
	private void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	public static void main(String[] args) {
		new MainWindow();
	}
}
