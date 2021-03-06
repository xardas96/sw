package ar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import ar.image.DerivativeGaussianKernel;
import ar.image.ImageOperations;
import ar.marker.Marker;
import ar.utils.Vector2d;

public class DesktopMarkerFinder implements MarkerFinder {
	private Random random;
	private int debugMode = DEBUG_OFF;
	public static final int DEBUG_SHOW_STAGE1 = 1;
	public static final int DEBUG_SHOW_STAGE2 = 2;
	public static final int DEBUG_SHOW_STAGE3 = 3;
	public static final int DEBUG_SHOW_STAGE4 = 4;
	public static final int DEBUG_SHOW_STAGE5 = 5;
	public static final int DEBUG_SHOW_STAGE6 = 6;
	public static final int DEBUG_OFF = 0;

	public DesktopMarkerFinder() {
		random = new Random();
	}
	
	public DesktopMarkerFinder(int debugMode){
		this();
		this.debugMode = debugMode;
	}

	@Override
	public List<Marker> readImage(InputStream is) throws IOException {
		BufferedImage image = ImageIO.read(is);
		return findMarkers(image);
	}

	public List<Marker> findMarkers(BufferedImage image) {
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
					mergeLineSegments(image, lineSegments);
					segments.addAll(lineSegments);
				}
			}
		}
		mergeLineSegments(image, segments);
		extendLines(image, segments);
		segments = findLinesWithCorners(image, segments);
		List<Marker> markers = new ArrayList<Marker>();
		if (!segments.isEmpty()) {
			do {
				LineSegment chainSegment = segments.remove(0);
				List<LineSegment> chain = new ArrayList<LineSegment>();
				int length = 1;
				findChainOfLines(chainSegment, true, segments, chain, length);
				chain.add(chainSegment);
				if (chain.size() < 4) {
					findChainOfLines(chainSegment, false, segments, chain, chain.size());
				}
				if (chain.size() > 2) {
					Marker marker = new Marker();
					marker.setChain(chain);;
					marker.reconstructCorners();
					markers.add(marker);
				}
			} while (!segments.isEmpty());
		}
		return markers;
	}
	
	public List<Marker> findMarkers(BufferedImage image, BufferedImage debugImage){
		List<Edgel> edgels = new ArrayList<Edgel>();
		List<LineSegment> segments = new ArrayList<LineSegment>();
		List<LineSegment> debugSegments = null;
		if(debugMode==DEBUG_SHOW_STAGE2)
			debugSegments = new ArrayList<LineSegment>();
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
					if(debugMode==DEBUG_SHOW_STAGE2)
						debugSegments.addAll(lineSegments); //TODO kopiowanie lineSegments
					mergeLineSegments(image, lineSegments);
					segments.addAll(lineSegments);
				}
			}
		}
		if(debugMode==DEBUG_SHOW_STAGE1)
			drawEdgels(debugImage, edgels);
		if(debugMode==DEBUG_SHOW_STAGE2)
			drawLineSegments(debugImage, debugSegments);
		mergeLineSegments(image, segments);
		if(debugMode==DEBUG_SHOW_STAGE3)
			drawLineSegments(debugImage, segments);
		extendLines(image, segments);
		if(debugMode==DEBUG_SHOW_STAGE4)
			drawLineSegments(debugImage, segments);
		segments = findLinesWithCorners(image, segments);
		if(debugMode==DEBUG_SHOW_STAGE5)
			drawLineSegments(debugImage, segments);
		List<Marker> markers = new ArrayList<Marker>();
		if (!segments.isEmpty()) {
			do {
				LineSegment chainSegment = segments.remove(0);
				List<LineSegment> chain = new ArrayList<LineSegment>();
				int length = 1;
				findChainOfLines(chainSegment, true, segments, chain, length);
				chain.add(chainSegment);
				if (chain.size() < 4) {
					findChainOfLines(chainSegment, false, segments, chain, chain.size());
				}
				if (chain.size() > 2) {
					Marker marker = new Marker();
					marker.setChain(chain);;
					marker.reconstructCorners();
					markers.add(marker);
				}
			} while (!segments.isEmpty());
		}
		if(debugMode==DEBUG_SHOW_STAGE6)
			drawMarkers(debugImage, markers);
		return markers;
	}

	private void findChainOfLines(LineSegment startSegment, boolean atStartPoint, List<LineSegment> lineSegments, List<LineSegment> chain, int length) {
		boolean isFound = false;
		Vector2d startPoint = atStartPoint ? startSegment.getStart().getPosition() : startSegment.getEnd().getPosition();
		for (int i = 0; i < lineSegments.size() && !isFound; i++) {
			LineSegment segment = lineSegments.get(i);
			isFound = !startSegment.isOrientationCompatible(segment);
			if (isFound) {
				Vector2d subtracted = atStartPoint ? segment.getEnd().getPosition() : segment.getStart().getPosition();
				isFound &= startPoint.subtract(subtracted).getSquaredLength() <= 16.0f;
			}
			if (isFound) {
				double orientation = startSegment.getDirection().getX() * segment.getDirection().getY() - startSegment.getDirection().getY() * segment.getDirection().getX();
				isFound &= !(atStartPoint && orientation <= 0 || !atStartPoint && orientation >= 0);
			}
			if (isFound) {
				length++;
				LineSegment chainSegment = segment;
				lineSegments.remove(segment);
				if (length == 4) {
					chain.add(chainSegment);
				} else {
					if (!atStartPoint) {
						chain.add(chainSegment);
					}
					findChainOfLines(chainSegment, atStartPoint, lineSegments, chain, length);
					if (atStartPoint) {
						chain.add(chainSegment);
					}
				}
			}
		}
	}

	private List<LineSegment> findLinesWithCorners(BufferedImage image, List<LineSegment> segments) {
		List<LineSegment> linesWithCorners = new ArrayList<LineSegment>();
		for (LineSegment segment : segments) {
			int[] composites = null;
			int dx = (int) (segment.getDirection().getX() * 4.0);
			int dy = (int) (segment.getDirection().getY() * 4.0);
			int x = (int) segment.getStart().getX() - dx;
			int y = (int) segment.getEnd().getY() - dy;
			if (imageContains(image, x, y)) {
				composites = ImageOperations.getRGBComposites(image, x, y);
				if (composites[0] > WHITETRESHOLD && composites[1] > WHITETRESHOLD && composites[2] > WHITETRESHOLD) {
					segment.setStartCorner(true);
				}
			}
			x = (int) segment.getEnd().getX() + dx;
			y = (int) segment.getEnd().getY() + dy;
			if (imageContains(image, x, y)) {
				composites = ImageOperations.getRGBComposites(image, x, y);
				if (composites[0] > WHITETRESHOLD && composites[1] > WHITETRESHOLD && composites[2] > WHITETRESHOLD) {
					segment.setEndCorner(true);
				}
			}
			if (segment.isStartCorner() || segment.isEndCorner()) {
				linesWithCorners.add(segment);
			}
		}
		return linesWithCorners;
	}

	private void extendLines(BufferedImage image, List<LineSegment> segments) {
		for (LineSegment segment : segments) {
			mergeAvaliable(image, segment.getEnd().getPosition(), segment.getEnd().getPosition(), 999, segment.getDirection(), segment.getEnd().getDirection());
			Vector2d v = segment.getDirection().negate();
			mergeAvaliable(image, segment.getStart().getPosition(), segment.getStart().getPosition(), 999, v, segment.getEnd().getDirection());
		}
	}

	private List<LineSegment> mergeLineSegments(BufferedImage image, List<LineSegment> segments) {
		final double angleTreshold = 0.99;
		final double maxSquaredLength = 25 * 25;
		for (int i = 0; i < segments.size(); i++) {
			LineSegment segment1 = segments.get(i);
			List<LineSegmentDistance> distances = new ArrayList<LineSegmentDistance>();
			for (int j = 0; j < segments.size(); j++) {
				LineSegment segment2 = segments.get(j);
				if (i != j && Vector2d.dot(segment2.getDirection(), segment1.getDirection()) > angleTreshold) {
					// tutaj moze byc blad
					Vector2d mergeVector = segment2.getEnd().getPosition().subtract(segment1.getStart().getPosition());
					mergeVector.normalize();
					if (Vector2d.dot(mergeVector, segment1.getDirection()) > angleTreshold) {
						double length = segment2.getStart().getPosition().subtract(segment1.getEnd().getPosition()).getSquaredLength();
						if (length < maxSquaredLength)
							distances.add(new LineSegmentDistance(length, j));
					}
				}
			}
			if (distances.size() > 0) {
				Collections.sort(distances);
				for (int j = 0; j < distances.size(); j++) {
					LineSegment segment2 = segments.get(distances.get(j).getIndex());
					Vector2d mergeLineStart = segment1.getEnd().getPosition();
					Vector2d mergeLineEnd = segment2.getStart().getPosition();
					double length = segment2.getStart().getPosition().subtract(segment1.getEnd().getPosition()).getLength();
					Vector2d direction = mergeLineEnd.subtract(mergeLineStart);
					direction.normalize();
					if (mergeAvaliable(image, mergeLineStart, mergeLineEnd, length, direction, segment1.getEnd().getDirection())) {
						segment1.setEnd(segment2.getEnd());
						Vector2d newDir = segment1.getEnd().getPosition().subtract(segment1.getStart().getPosition());
						newDir.normalize();
						segment1.setDirection(newDir);
						segment2.setMerged(true);
					}
				}
				boolean merged = false;
				for (int j = segments.size() - 1; j >= 0; j--) {
					if (segments.get(j).isMerged()) {
						segments.remove(j);
						merged = true;
					}
				}
				if (merged)
					i--;
			}
		}
		return segments;
	}

	private boolean imageContains(BufferedImage image, int x, int y) {
		int width = image.getWidth();
		int height = image.getHeight();
		return x >= 2 && x < width - 3 && y >= 2 && y < height - 3;
	}

	private boolean mergeAvaliable(BufferedImage image, Vector2d mergeLineStart, Vector2d mergeLineEnd, double length, Vector2d direction, Vector2d gradient) {
		Vector2d normal = new Vector2d(direction.getY(), -direction.getX());
		boolean merge = true;
		Vector2d point = mergeLineStart;
		for (int i = 0; i < length && merge; i++) {
			point = point.add(direction);
			int x = (int) point.getX();
			int y = (int) point.getY();
			int xPlusNorm = (int) (point.getX() + normal.getX());
			int yPlusNorm = (int) (point.getY() + normal.getY());
			int xMinusNorm = (int) (point.getX() - normal.getX());
			int yMinusNorm = (int) (point.getY() - normal.getY());
			merge = imageContains(image, x, y) && DerivativeGaussianKernel.applyEdgeKernelX(image, x, y) >= TRESHOLD / 2;
			merge |= imageContains(image, x, y) && DerivativeGaussianKernel.applyEdgeKernelY(image, x, y) >= TRESHOLD / 2;
			if (merge) {
				merge = imageContains(image, x, y) && Vector2d.dot(ImageOperations.calculateSobel(image, x, y), gradient) > 0.38; // zmienna
				if (!merge) {
					merge = imageContains(image, xPlusNorm, yPlusNorm) && Vector2d.dot(ImageOperations.calculateSobel(image, xPlusNorm, yPlusNorm), gradient) > 0.38;
				}
				if (!merge) {
					merge = imageContains(image, xMinusNorm, yMinusNorm) && Vector2d.dot(ImageOperations.calculateSobel(image, xMinusNorm, yMinusNorm), gradient) > 0.38;
				}
			}
		}
		mergeLineEnd.setX(point.getX() - direction.getX());
		mergeLineEnd.setY(point.getY() - direction.getY());
		return merge;
	}

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
					lineSegment.setDirection(r1.getDirection());
					for (Edgel edgelInRegion : edgelsInRegion) {
						if (lineSegment.isInLine(edgelInRegion)) {
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
				Vector2d direction = lineSegmentInRun.getStart().getPosition().subtract(lineSegmentInRun.getEnd().getPosition());
				Vector2d orientation = new Vector2d(-lineSegmentInRun.getStart().getDirection().getX(), lineSegmentInRun.getStart().getDirection().getY());
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
				if (dot < 0.0) {
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
			for (int x = 0; x < width; x++, leftShiftArray(colorArray)) {
				colorArray[colorArray.length - 1] = ImageOperations.getRGBComposites(image, left + x + 2, top + y);
				int[] edgeValues = DerivativeGaussianKernel.applyEdgeKernel(colorArray);
				if (edgeValues[0] > TRESHOLD && edgeValues[1] > TRESHOLD && edgeValues[2] > TRESHOLD)
					edgeValue = edgeValues[0];
				else
					edgeValue = 0;
				if (prevEdgeValue > 0 && prevEdgeValue > prevEdgeValue2 && prevEdgeValue > edgeValue) {
					Edgel edgel = new Edgel(left + x - 1, top + y);
					edgel.setDirection(ImageOperations.calculateSobel(image, (int) Math.round(edgel.getX()), (int) Math.round(edgel.getY())));
					foundEdgels.add(edgel);
				}
				prevEdgeValue2 = prevEdgeValue;
				prevEdgeValue = edgeValue;
			}
		}

		for (int x = 0; x < width; x += SCAN_LINE_DIMENSION) {
			int[][] colorArray = prepareColorArrayForY(image, left + x, top);
			int edgeValue, prevEdgeValue = 0, prevEdgeValue2 = 0;
			for (int y = 0; y < height; y++, leftShiftArray(colorArray)) {
				colorArray[colorArray.length - 1] = ImageOperations.getRGBComposites(image, left + x, top + y + 2);
				int[] edgeValues = DerivativeGaussianKernel.applyEdgeKernel(colorArray);
				if (edgeValues[0] > TRESHOLD && edgeValues[1] > TRESHOLD && edgeValues[2] > TRESHOLD)
					edgeValue = edgeValues[0];
				else
					edgeValue = 0;
				if (prevEdgeValue > 0 && prevEdgeValue > prevEdgeValue2 && prevEdgeValue > edgeValue) {
					Edgel edgel = new Edgel(left + x, top + y - 1);

					edgel.setDirection(ImageOperations.calculateSobel(image, (int) edgel.getX(), (int) edgel.getY()));
					foundEdgels.add(edgel);
				}
				prevEdgeValue2 = prevEdgeValue;
				prevEdgeValue = edgeValue;
			}
		}
		return foundEdgels;
	}

	private int[][] prepareColorArrayForX(BufferedImage image, int x, int y) {
		int[][] colorArray = new int[5][3];
		for (int i = 0; i < colorArray.length - 1; i++) {
			colorArray[i] = ImageOperations.getRGBComposites(image, x - 2 + 1 * i, y);
		}
		return colorArray;
	}

	private int[][] prepareColorArrayForY(BufferedImage image, int x, int y) {
		int[][] colorArray = new int[5][3];
		for (int i = 0; i < colorArray.length - 1; i++) {
			colorArray[i] = ImageOperations.getRGBComposites(image, x, y - 2 + 1 * i);
		}
		return colorArray;
	}

	private void leftShiftArray(int[][] array) {
		for (int i = 0; i < array.length - 1; i++)
			array[i] = array[i + 1];
	}
	
//DEBUG METHODS
	private Image drawEdgels(BufferedImage image, List<Edgel> edgels) {
		Graphics2D g = image.createGraphics();
		for (Edgel edgel : edgels) {
			g.setColor(Color.BLUE);
			g.fillRect((int) edgel.getX() - 2, (int) edgel.getY() - 2, 4, 4);
		}
		return image;
	}
	
	private Image drawLineSegments(BufferedImage image, List<LineSegment> segments) {
		Graphics2D g = image.createGraphics();
		for (LineSegment segment : segments) {
			g.setColor(Color.YELLOW);
			g.setStroke(new BasicStroke(2.0f));
			if (segment.isStartCorner()) {
				g.drawLine((int) segment.getStart().getX(), (int) segment.getStart().getY(), (int) segment.getStart().getX(), (int) segment.getStart().getY());
			}
			if (segment.isEndCorner()) {
				g.drawLine((int) segment.getEnd().getX(), (int) segment.getEnd().getY(), (int) segment.getEnd().getX(), (int) segment.getEnd().getY());
			}
			drawArrow(image, (int) segment.getStart().getX(), (int) segment.getStart().getY(), (int) segment.getEnd().getX(), (int) segment.getEnd().getY(), segment.getDirection().getX(), segment.getDirection().getY());
		}
		return image;
	}
	
	private void drawArrow(BufferedImage image, int x1, int y1, int x2, int y2, double xn, double yn) {
		Graphics g = image.getGraphics();
		g.setColor(Color.GREEN);
		g.drawLine(x1, y1, x2, y2);
		g.setColor(Color.RED);
		g.drawLine(x2, y2, x2 + (int) (5.0 * (-xn + yn)), y2 + (int) (5.0 * (-yn - xn)));
		g.drawLine(x2, y2, x2 + (int) (5.0 * (-xn - yn)), y2 + (int) (5.0 * (-yn + xn)));
	}
	
	public static Image drawMarkers(BufferedImage image, List<Marker> markers) {
		Graphics2D g = image.createGraphics();
		for (Marker marker : markers) {
			g.setColor(Color.YELLOW);
			g.setStroke(new BasicStroke(2.0f));
			g.drawLine((int) marker.getCorner1().getX(), (int) marker.getCorner1().getY(), (int) marker.getCorner2().getX(), (int) marker.getCorner2().getY());
			g.drawLine((int) marker.getCorner2().getX(), (int) marker.getCorner2().getY(), (int) marker.getCorner3().getX(), (int) marker.getCorner3().getY());
			g.drawLine((int) marker.getCorner3().getX(), (int) marker.getCorner3().getY(), (int) marker.getCorner4().getX(), (int) marker.getCorner4().getY());
			g.drawLine((int) marker.getCorner4().getX(), (int) marker.getCorner4().getY(), (int) marker.getCorner1().getX(), (int) marker.getCorner1().getY());
		}
		return image;
	}	
}