package ar.image;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class DerivativeGaussianKernel {
	private static final int[] FILTER_VECTOR = {-3,-5,0,5,3};
	
	public static int[] applyEdgeKernel(int[][] array) {
		int[] values = new int[3];
		for (int i = 0; i < values.length; i++) {
			values[i] = FILTER_VECTOR[0] * array[0][i];
			values[i] += FILTER_VECTOR[1] * array[1][i];
			values[i] += FILTER_VECTOR[3] * array[3][i];
			values[i] += FILTER_VECTOR[4] * array[4][i];
			values[i] = Math.abs(values[i]);
		}
		return values;
	}

	public static int applyEdgeKernelX(BufferedImage image, int x, int y) {
		int shift = ImageOperations.BLUE_SHIFT;
		int value = FILTER_VECTOR[0] * ImageOperations.getRGBComposite(image, x, y - 2, shift );
		value += FILTER_VECTOR[1] * ImageOperations.getRGBComposite(image, x, y - 1, shift );
		value += FILTER_VECTOR[3] * ImageOperations.getRGBComposite(image, x, y + 1, shift );
		value += FILTER_VECTOR[4] * ImageOperations.getRGBComposite(image, x, y + 2, shift );
		return Math.abs(value);
	}

	public static int applyEdgeKernelY(BufferedImage image, int x, int y) {
		int shift = ImageOperations.BLUE_SHIFT;
		int value = FILTER_VECTOR[0] * ImageOperations.getRGBComposite(image, x - 2, y, shift );
		value += FILTER_VECTOR[1] * ImageOperations.getRGBComposite(image, x - 1, y, shift );
		value += FILTER_VECTOR[3] * ImageOperations.getRGBComposite(image, x + 1, y,shift );
		value += FILTER_VECTOR[4] * ImageOperations.getRGBComposite(image, x + 2, y, shift );
		return Math.abs(value);
	}
	
	public static int applyEdgeKernelX(BufferedImage image, int x, int y, int shift) {
		int value = FILTER_VECTOR[0] * ImageOperations.getRGBComposite(image, x, y - 2, shift);
		value += FILTER_VECTOR[1] * ImageOperations.getRGBComposite(image, x, y - 1, shift);
		value += FILTER_VECTOR[3] * ImageOperations.getRGBComposite(image, x, y + 1, shift);
		value += FILTER_VECTOR[4] * ImageOperations.getRGBComposite(image, x, y + 2, shift);
		return Math.abs(value);
	}
	
	public static int applyEdgeKernelY(BufferedImage image, int x, int y, int shift) {
		int value = FILTER_VECTOR[0] * ImageOperations.getRGBComposite(image, x - 2, y, shift);
		value += FILTER_VECTOR[1] * ImageOperations.getRGBComposite(image, x - 1, y, shift);
		value += FILTER_VECTOR[3] * ImageOperations.getRGBComposite(image, x + 1, y, shift);
		value += FILTER_VECTOR[4] * ImageOperations.getRGBComposite(image, x + 2, y, shift);
		return Math.abs(value);
	}
	
	public static Image applyEdgeKernelX(BufferedImage image){
		BufferedImage result =new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		List<int[]> pixelList = new ArrayList<int[]>();
		List<Point> points = new ArrayList<Point>();
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 2; y < image.getHeight()-3; y++){
				pixelList.add(new int[]{applyEdgeKernelX(image, x, y, ImageOperations.RED_SHIFT), applyEdgeKernelX(image, x, y, ImageOperations.GREEN_SHIFT), applyEdgeKernelX(image, x, y, ImageOperations.BLUE_SHIFT)});
				points.add(new Point(x, y));
			}
		}
		double max = Double.MIN_VALUE;
		for(int[] tab :pixelList) {
			if(tab[0] > max) {
				max = tab[0];
			}
			if(tab[1] > max) {
				max = tab[1];
			}
			if(tab[2] > max) {
				max = tab[2];
			}
		}
		for(int i = 0; i < pixelList.size(); i++){
			int[] tab = pixelList.get(i);
			result.setRGB((int) points.get(i).getX(), (int) points.get(i).getY(), new Color((int) (tab[0]/max*255), (int) (tab[1]/max*255),(int) (tab[2]/max*255)).getRGB());
		}
		return result;
	}
	
	public static Image applyEdgeKernelY(BufferedImage image){
		BufferedImage result =new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		List<int[]> pixelList = new ArrayList<int[]>();
		List<Point> points = new ArrayList<Point>();
		for(int x = 2; x < image.getWidth()-3; x++){
			for(int y = 0; y < image.getHeight(); y++){
				pixelList.add(new int[]{applyEdgeKernelY(image, x, y, ImageOperations.RED_SHIFT), applyEdgeKernelY(image, x, y, ImageOperations.GREEN_SHIFT), applyEdgeKernelY(image, x, y,ImageOperations.BLUE_SHIFT)});
				points.add(new Point(x, y));
			}
		}
		double max = Double.MIN_VALUE;
		for(int[] tab :pixelList) {
			if(tab[0] > max) {
				max = tab[0];
			}
			if(tab[1] > max) {
				max = tab[1];
			}
			if(tab[2] > max) {
				max = tab[2];
			}
		}
		for(int i = 0; i < pixelList.size(); i++){
			int[] tab = pixelList.get(i);
			result.setRGB((int) points.get(i).getX(), (int) points.get(i).getY(), new Color((int) (tab[0]/max*255), (int) (tab[1]/max*255),(int) (tab[2]/max*255)).getRGB());
		}
		return result;
	}
}
