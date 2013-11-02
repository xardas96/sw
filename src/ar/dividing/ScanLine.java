package ar.dividing;

import java.util.ArrayList;
import java.util.List;

public class ScanLine {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	private int orientation;
	private List<Pixel> pixels;
	
	public ScanLine(int orientation, List<Pixel> pixels) {
		this.orientation = orientation;
		this.pixels = pixels;
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	public int[] getPixelXY(int index) {
		int[] coords = new int[2];
		Pixel pixel = pixels.get(index);
		coords[0] = pixel.getX();
		coords[1] = pixel.getY();
		return coords;
	}
	
	public List<Pixel> getPixels() {
		return pixels;
	}
	
	public List<int[]> getPixelsColorComposites() {
		List<int[]> composites = new ArrayList<>();
		int[] reds = new int[pixels.size()];
		int[] blues = new int[pixels.size()];
		int[] greens = new int[pixels.size()];
		for(int i = 0; i<pixels.size(); i++) {
			Pixel pixel = pixels.get(i);
			reds[i] = pixel.getR();
			greens[i] = pixel.getG();
			blues[i] = pixel.getB();
		}
		composites.add(reds);
		composites.add(greens);
		composites.add(blues);
		return composites;
	}
}