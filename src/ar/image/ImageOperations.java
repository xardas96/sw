package ar.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

import ar.utils.Vector2d;

public abstract class ImageOperations {
	public static final int RED_SHIFT = 16;
	public static final int GREEN_SHIFT = 8;
	public static final int BLUE_SHIFT = 0;
	
	public static double calculateBlackness(Vector2d point, BufferedImage image){
		double blackness = 0;
		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				Color c = new Color(image.getRGB((int)point.getX()+i, (int) point.getY()+j));
				double b = c.getRed()+c.getBlue()+c.getRed();
				blackness += b /=3;
			}
		}
		return blackness/9;
	}
	
	public static Vector2d calculateSobel(BufferedImage image, int x, int y) {
		int shift = BLUE_SHIFT;
		int gx = getRGBComposite(image, x - 1, y - 1, shift);
		gx += 2 * getRGBComposite(image, x, y - 1, shift);
		gx += getRGBComposite(image, x + 1, y - 1, shift);
		gx -= getRGBComposite(image, x - 1, y + 1, shift);
		gx -= 2 * getRGBComposite(image, x, y + 1, shift);
		gx -= getRGBComposite(image, x + 1, y + 1, shift);

		int gy = getRGBComposite(image, x - 1, y - 1, shift);
		gy += 2 * getRGBComposite(image, x - 1, y, shift);
		gy += getRGBComposite(image, x - 1, y + 1, shift);
		gy -= getRGBComposite(image, x + 1, y - 1, shift);
		gy -= 2 * getRGBComposite(image, x + 1, y, shift);
		gy -= getRGBComposite(image, x + 1, y + 1, shift);
		Vector2d vec = new Vector2d(gx, gy);
		vec.normalize();
		return vec;
	}
	
	public static int[] getRGBComposites(BufferedImage image, int x, int y) {
		int[] composites = new int[3];
		int rgb = image.getRGB(x, y);
		composites[0] = (rgb >> RED_SHIFT) & 0x0ff; // red
		composites[1] = (rgb >> GREEN_SHIFT) & 0x0ff; // green
		composites[2] = (rgb) & 0x0ff; // blue
		return composites;
	}

	public static int getRGBComposite(BufferedImage image, int x, int y, int color) {
		return (image.getRGB(x, y) >> color) & 0x0ff;
	}
}
