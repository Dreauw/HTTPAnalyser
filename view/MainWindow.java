package view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Model;

/**
 * MainWindow
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 600;
	
	private Model model;
	
	public MainWindow() {
		super("HTTPAnalyser");
		
		model = new Model();
		
		System.out.println(model.getDevicesName());
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		new MainWindow();
	}
}
