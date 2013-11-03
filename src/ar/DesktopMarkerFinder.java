package ar;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class DesktopMarkerFinder implements MarkerFinder {

	@Override
	public List<Marker> readImage(InputStream is) throws IOException {
		BufferedImage image = ImageIO.read(is);
		return findMarkers(image);
	}

	private List<Marker> findMarkers(BufferedImage image) {
		List<Marker> foundMarkers = new ArrayList<Marker>();
		int height = image.getHeight();
		int width = image.getWidth();
		for (int y = 0; y < height; y += REGION_DIMENSION) {
			for (int x = 0; x < width; x += REGION_DIMENSION) {
				int left = Math.min(REGION_DIMENSION, width - x);
				int top = Math.min(REGION_DIMENSION, height - y);
				List<Edgel> edgels = findEdgels(image, x, y, left, top);
				//TODO the rest ;)
			}
		}
		return foundMarkers;
	}

	private List<Edgel> findEdgels(BufferedImage image, int left, int top, int width, int height) {
		List<Edgel> foundEdgels = new ArrayList<Edgel>();
		// TODO find edgels
		return foundEdgels;
	}

	private int[] getRGBComposites(BufferedImage image, int x, int y) {
		int[] composites = new int[3];
		int rgb = image.getRGB(x, y);
		composites[0] = (rgb >> 16) & 0x0ff; // red
		composites[1] = (rgb >> 8) & 0x0ff; // green
		composites[2] = (rgb) & 0x0ff; // blue
		return composites;
	}

}