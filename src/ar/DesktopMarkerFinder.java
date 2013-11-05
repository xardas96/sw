package ar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class DesktopMarkerFinder implements MarkerFinder {
	private Random random;

	public DesktopMarkerFinder() {
		random = new Random(); // ¿eby by³o szybiej, jako pole w klasie tworzone raz
	}

	@Override
	public List<Marker> readImage(InputStream is) throws IOException {
		BufferedImage image = ImageIO.read(is);
		return null;// findMarkers(image);
	}

	public Image drawEdgels(InputStream is) throws IOException {
		BufferedImage image = ImageIO.read(is);
		long startTime = System.currentTimeMillis();
		List<Edgel> edgels = findMarkers(image);
		long stopTime = System.currentTimeMillis();
		System.out.println("Czas znajdowania Edgelsów: " + (stopTime - startTime));
		Graphics2D g = image.createGraphics();
		System.out.println(edgels.size());
		for (Edgel edgel : edgels) {
			// image.setRGB(edgel.getX(), edgel.getY(), new
			// Color(0,0,255).getRGB());
			g.setColor(Color.BLUE);
			g.fillRect(edgel.getX() - 2, edgel.getY() - 2, 4, 4);
			
		}
		return image;
	}

	public Image drawLineSegments(InputStream is) throws IOException {
		BufferedImage image = ImageIO.read(is);
		long startTime = System.currentTimeMillis();
		List<LineSegment> edgels = findMarkers2(image);
		long stopTime = System.currentTimeMillis();
		System.out.println("czas line segmentów: " + (stopTime - startTime));
		Graphics2D g = image.createGraphics();
		System.out.println(edgels.size());
		// for(LineSegment segment : edgels) {
		// g.setColor(Color.YELLOW);
		// g.drawLine(segment.getStart().getX(), segment.getStart().getY(),
		// segment.getEnd().getX(), segment.getEnd().getY());
		// }
		for (LineSegment segment : edgels) {
			// image.setRGB(edgel.getX(), edgel.getY(), new
			// Color(0,0,255).getRGB());
			g.setColor(Color.YELLOW);
			g.setStroke(new BasicStroke(2.0f));
			g.drawLine(segment.getStart().getX(), segment.getStart().getY(), segment.getEnd().getX(), segment.getEnd().getY());
		}
		return image;
	}

	// private List<Marker> findMarkers(BufferedImage image) {
	private List<Edgel> findMarkers(BufferedImage image) {
		// private List<LineSegment> findMarkers(BufferedImage image){
		List<Marker> foundMarkers = new ArrayList<Marker>();
		List<Edgel> edgels = new ArrayList<Edgel>();
		int height = image.getHeight();
		int width = image.getWidth();
		for (int y = 2; y < height - 3; y += REGION_DIMENSION) {
			for (int x = 2; x < width - 3; x += REGION_DIMENSION) {
				int left = Math.min(REGION_DIMENSION, width - x - 3);
				int top = Math.min(REGION_DIMENSION, height - y - 3);
				List<Edgel> regionEdgels = findEdgels(image, x, y, left, top);
				edgels.addAll(regionEdgels);
			}
		}
		System.out.println(edgels.size());
		return edgels;
	}

	private List<LineSegment> findMarkers2(BufferedImage image) {
		List<Marker> foundMarkers = new ArrayList<Marker>();
		List<Edgel> edgels = new ArrayList<Edgel>();
		List<LineSegment> segments = new ArrayList<LineSegment>();
		int height = image.getHeight();
		int width = image.getWidth();
		for (int y = 2; y < height - 3; y += REGION_DIMENSION) {
			for (int x = 2; x < width - 3; x += REGION_DIMENSION) {
				int left = Math.min(REGION_DIMENSION, width - x - 3);
				int top = Math.min(REGION_DIMENSION, height - y - 3);
				List<Edgel> regionEdgels = findEdgels(image, x, y, left, top);
				edgels.addAll(regionEdgels);
				List<LineSegment> lineSegments;
				if (regionEdgels.size() > EDGELS_ONLINE) {
					lineSegments = findLineSegments(regionEdgels);
					segments.addAll(lineSegments);
					for(LineSegment s : lineSegments) {
						System.out.println(s.getSupportingEdgels() + " start: " + s.getStart() + " end: " + s.getEnd());
					}
				}
				// TODO the rest ;)
			}
		}
		System.out.println(edgels.size());
		return segments;
	}

	// TODO tu zaczyna siê "dobry" kod :)

	private List<LineSegment> findLineSegments(List<Edgel> edgelsInRegion) {
		List<LineSegment> foundSegments = new ArrayList<LineSegment>();
		int ransacIterations = 25;
		int maxIterations = 100;
		LineSegment lineSegmentInRun;
		do {
			lineSegmentInRun = new LineSegment();
			for (int i = 0; i < ransacIterations; i++) {
				Edgel r1;
				Edgel r2;
				int iteration = 0, ir1, ir2;
				do {
					ir1 = random.nextInt(edgelsInRegion.size());
					ir2 = random.nextInt(edgelsInRegion.size());
					r1 = edgelsInRegion.get(ir1);
					r2 = edgelsInRegion.get(ir2);
					iteration++;
				} while ((ir1 == ir2 || !r1.isOrientationCompatible(r2)) && iteration < maxIterations);
				if (iteration < maxIterations) {
					LineSegment lineSegment = new LineSegment();
					lineSegment.setStart(r1);
					lineSegment.setEnd(r2);
					lineSegment.setDirection(r2.getDirection());
					for(Edgel edgelInRegion : edgelsInRegion) {
						if(lineSegment.isInLine(edgelInRegion)) {
							lineSegment.addSupportingEdgel(edgelInRegion);
						}
					}
					if (lineSegment.getSupportingEdgelsSize() > lineSegmentInRun.getSupportingEdgelsSize()) {
						lineSegmentInRun = lineSegment;
					}
				}
			}
			if (lineSegmentInRun.getSupportingEdgelsSize() >= EDGELS_ONLINE) {
				double u1 = 0;
				double u2 = 50000;
				Vector2d direction = lineSegmentInRun.getStart().getDirection().subtract(lineSegmentInRun.getEnd().getDirection());
				Vector2d orientation = new Vector2d(-lineSegmentInRun.getStart().getY(), lineSegmentInRun.getStart().getX());
				if (Math.abs(direction.getX()) <= Math.abs(direction.getY())) {
					for (Edgel edgel : lineSegmentInRun.getSupportingEdgels()) {
						if (edgel.getY() > u1) {
							u1 = edgel.getY();
							lineSegmentInRun.setStart(edgel);
						}
						if (edgel.getY() < u2) {
							u2 = edgel.getY();
							lineSegmentInRun.setEnd(edgel);
						}
					}
				} else {
					for (Edgel edgel : lineSegmentInRun.getSupportingEdgels()) {
						if (edgel.getX() > u1) {
							u1 = edgel.getX();
							lineSegmentInRun.setStart(edgel);
						}
						if (edgel.getX() < u2) {
							u2 = edgel.getX();
							lineSegmentInRun.setEnd(edgel);
						}

					}
				}
				double dot = Vector2d.dot(lineSegmentInRun.getEnd().getPosition().subtract(lineSegmentInRun.getStart().getPosition()), orientation);
				if (dot < 0.0f) {
					lineSegmentInRun.swapEndpoints();
				}
				Vector2d newDirection = lineSegmentInRun.getEnd().getPosition().subtract(lineSegmentInRun.getStart().getPosition());
				newDirection.normalize(); 
				lineSegmentInRun.setDirection(newDirection);
				foundSegments.add(lineSegmentInRun);
				edgelsInRegion.removeAll(lineSegmentInRun.getSupportingEdgels());
			}
		} while (lineSegmentInRun.getSupportingEdgelsSize() >= EDGELS_ONLINE && edgelsInRegion.size() >= EDGELS_ONLINE);
		return foundSegments;
	}

	private List<Edgel> findEdgels(BufferedImage image, int left, int top, int width, int height) {
		List<Edgel> foundEdgels = new ArrayList<Edgel>();
		for (int y = 0; y < height; y += SCAN_LINE_DIMENSION) {
			int[][] colorArray = prepareColorArrayForX(image, left, top + y);
			int edgeValue, prevEdgeValue = 0, prevEdgeValue2 = 0;
			for (int x = 0; x < width; x++, leftShiftArray(colorArray)) { // Shiftowanie szybsze od wyci¹gania wszystkich kolorów co iteracje?
				colorArray[colorArray.length - 1] = getRGBComposites(image, left + x + 2, top + y);
				int[] edgeValues = applyEdgeKernel(colorArray);
				if (edgeValues[0] > TRESHOLD && edgeValues[1] > TRESHOLD && edgeValues[2] > TRESHOLD)
					edgeValue = edgeValues[0];
				else
					edgeValue = 0;
				if (prevEdgeValue > 0 && prevEdgeValue > prevEdgeValue2 && prevEdgeValue > edgeValue) {
					Edgel edgel = new Edgel(left + x - 1, top + y);
					edgel.setDirection(calculateSobel(image, edgel.getX(), edgel.getY()));
					foundEdgels.add(edgel);
				}
				prevEdgeValue2 = prevEdgeValue;
				prevEdgeValue = edgeValue;
			}
		}
		// robimy to samo dla vertical, mozna by jakos to zrobic funkcja czy cos, ale w tym momencie nie wiem jak, wiec copy paste :p

		for (int x = 0; x < width; x += SCAN_LINE_DIMENSION) {
			int[][] colorArray = prepareColorArrayForY(image, left + x, top);
			int edgeValue, prevEdgeValue = 0, prevEdgeValue2 = 0;
			for (int y = 0; y < height; y++, leftShiftArray(colorArray)) { // Shiftowanie szybsze od wyci¹gania wszystkich kolorów co iteracje?
				colorArray[colorArray.length - 1] = getRGBComposites(image, left + x, top + y + 2);
				int[] edgeValues = applyEdgeKernel(colorArray);
				if (edgeValues[0] > TRESHOLD && edgeValues[1] > TRESHOLD && edgeValues[2] > TRESHOLD)
					edgeValue = edgeValues[0];
				else
					edgeValue = 0;
				if (prevEdgeValue > 0 && prevEdgeValue > prevEdgeValue2 && prevEdgeValue > edgeValue) {
					Edgel edgel = new Edgel(left + x - 1, top + y);
					edgel.setDirection(calculateSobel(image, edgel.getX(), edgel.getY()));
					foundEdgels.add(edgel);
				}
				prevEdgeValue2 = prevEdgeValue;
				prevEdgeValue = edgeValue;
			}
		}

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

	private int getRGBComposite(BufferedImage image, int x, int y, int color) {
		return (image.getRGB(x, y) >> color) & 0x0ff;
	}

	private int[][] prepareColorArrayForX(BufferedImage image, int x, int y) {
		int[][] colorArray = new int[5][3]; // magicNumbers
		for (int i = 0; i < colorArray.length - 1; i++) {
			colorArray[i] = getRGBComposites(image, x - 2 + 1 * i, y);
		}
		return colorArray;
	}

	private int[][] prepareColorArrayForY(BufferedImage image, int x, int y) {
		int[][] colorArray = new int[5][3]; // magicNumbers
		for (int i = 0; i < colorArray.length - 1; i++) {
			colorArray[i] = getRGBComposites(image, x, y - 2 + 1 * i);
		}
		return colorArray;
	}

	private void leftShiftArray(int[][] array) {
		// bez forowania dla celów szybkoœciowych? (raczej ma³y boost)
		for (int i = 0; i < array.length - 1; i++)
			array[i] = array[i + 1];
	}

	private int[] applyEdgeKernel(int[][] array) {
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

	private Vector2d calculateSobel(BufferedImage image, int x, int y) {
		int gx = getRGBComposite(image, x - 1, y - 1, RED_SHIFT);
		gx += 2 * getRGBComposite(image, x, y - 1, RED_SHIFT);
		gx += getRGBComposite(image, x + 1, y - 1, RED_SHIFT);
		gx -= getRGBComposite(image, x - 1, y + 1, RED_SHIFT);
		gx -= 2 * getRGBComposite(image, x, y + 1, RED_SHIFT);
		gx -= getRGBComposite(image, x + 1, y + 1, RED_SHIFT);

		int gy = getRGBComposite(image, x - 1, y - 1, RED_SHIFT);
		gy += 2 * getRGBComposite(image, x - 1, y, RED_SHIFT);
		gy += getRGBComposite(image, x - 1, y + 1, RED_SHIFT);
		gy -= getRGBComposite(image, x + 1, y - 1, RED_SHIFT);
		gy -= getRGBComposite(image, x + 1, y, RED_SHIFT);
		gy -= getRGBComposite(image, x + 1, y + 1, RED_SHIFT);
		Vector2d vec = new Vector2d(gx, gy);
		vec.normalize();
		return vec;
	}
}