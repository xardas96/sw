package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class DrawPanel extends JPanel {
	private static final long serialVersionUID = -950718375757439961L;
	private Image image;
	public DrawPanel(Image image){
		setBackground(Color.WHITE);
		this.image = image;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(image, 0, 0, null);
		setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
	}
}
