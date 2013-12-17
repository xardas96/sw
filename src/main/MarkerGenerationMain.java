package main;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ar.code.MarkerGenerator;


public class MarkerGenerationMain {
	public static void main(String[] args) throws IOException{
		MarkerGenerator markergen = new MarkerGenerator();
		int code = 4;
		BufferedImage image = markergen.makeMarker(code);
		ImageIO.write(image, "png", new File("marker"+code+".png"));
		
	}
}
