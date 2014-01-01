package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class DrawPanel extends JPanel implements WebcamImageRenderer {
	private static final long serialVersionUID = -950718375757439961L;
	private Image image;

	public DrawPanel(Image image) {
		setBackground(Color.WHITE);
		this.image = image;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	@Override
	public void setImage(Image image) {
		this.image = image;
	}
}