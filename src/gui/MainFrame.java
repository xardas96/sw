package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -8026416994513756565L;
	private DrawPanel panel;
	private JLabel fpsLabel;
	private int fpsCounter;

	public MainFrame(Image image, String title) {
		panel = new DrawPanel(image);
		fpsLabel = new JLabel();
		JScrollPane pane = new JScrollPane(panel);
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		add(fpsLabel, BorderLayout.SOUTH);
		setSize(new Dimension(700, 700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(title);
	}

	public void setImage(Image image) {
		fpsCounter++;
		panel.setImage(image);
		panel.repaint();
	}

	public void setFPS() {
		fpsLabel.setText(fpsCounter + " FPS");
		fpsCounter = 0;
	}
}