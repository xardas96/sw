package ar.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

import ar.utils.Vector2d;

public abstract class ImageOperations {
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
}
