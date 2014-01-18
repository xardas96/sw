package tests.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 9208876611179792319L;
	private BufferedImage image;
	private String imagePath;
	private List<Point> points;

	public void setImage(BufferedImage image) {
		this.image = image;
		points = new ArrayList<>();
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void addPoint(Point point) {
		points.add(point);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
		if (points != null) {
			g.setColor(Color.RED);
			for (Point p : points) {
				g.fillRect(p.x - 3, p.y - 3, 7, 7);
			}
		}
	}
}