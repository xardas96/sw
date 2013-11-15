package gui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -8026416994513756565L;
	
	private DrawPanel panel;
	
	public MainFrame(Image image, String title){
		panel = new DrawPanel(image);
		JScrollPane pane= new JScrollPane(panel);
		//add(panel);
		add(pane);
		setSize(new Dimension(700, 700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(title);
	}
}
