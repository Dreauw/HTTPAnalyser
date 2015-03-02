package view;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
	
	public MainWindow() {
		super("HTTPAnalyser");
		
		model = new Model();
		model.addObserver(this);
		
		System.out.println(model.getDevicesName());
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	@Override
	public void update(Observable observable, Object obj) {
		ModelMessage msg = (ModelMessage) obj;
		
		// Show error message
		if (msg.getType() == ModelMessage.TYPE.ERROR) {
			JOptionPane.showMessageDialog(this, msg.getData(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	public static void main(String[] args) {
		new MainWindow();
	}
}
