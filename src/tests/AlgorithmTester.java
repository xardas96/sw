package tests;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ar.DesktopMarkerFinder;
import ar.image.ImageOperations;
import ar.marker.Marker;
import ar.orientation.CornerBasedOrientationFinder;
import ar.utils.Vector2d;

public class AlgorithmTester {
	private static final double DISTANCE = 10.0;
	private static final double SCORE = 0.25;
	private DesktopMarkerFinder finder;
	private double tp;
	private double fp;
	private double fn;
	private double meanPrecision;
	private double meanRecall;
	private double meanFScore;
	private int iterations;

	public AlgorithmTester(int iterations) {
		finder = new DesktopMarkerFinder();
		this.iterations = iterations;
	}

	public void test(String inputPath, String outputFile) throws Exception {
		meanPrecision = 0;
		meanRecall = 0;
		meanFScore = 0;
		File output = new File(outputFile);
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(output));
		for (int it = 0; it < iterations; it++) {
			tp = 0;
			fp = 0;
			fn = 0;
			outputWriter.write("******************");
			outputWriter.newLine();
			outputWriter.write("Iteration: " + it);
			outputWriter.newLine();
			outputWriter.write("******************");
			outputWriter.newLine();
			File dir = new File(inputPath);
			File[] images = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.getName().endsWith(".png");
				}
			});
			File[] cornerFiles = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.getName().endsWith(".corners");
				}
			});
			for (int i = 0; i < images.length; i++) {
				File imageFile = images[i];
				List<Vector2d> corners = readCorners(cornerFiles[i]);
				BufferedImage img = ImageIO.read(imageFile);
				BufferedImage debugImage = ImageOperations.copyImage(img);
				List<Marker> markers = finder.findMarkers(img, debugImage);
				if (markers != null && !markers.isEmpty()) {
					try {
						markers = new CornerBasedOrientationFinder().setMarkerOrinetation(markers, img);
					} catch (Exception e) {
					}
					if (markers != null && !markers.isEmpty()) {
						double innerTp = 0;
						double innerFp = 0;
						Vector2d[] markerCorners = markers.get(0).getCornerArray();
						for (int j = 0; j < markerCorners.length; j++) {
							Vector2d markerPoint = markerCorners[j];
							double minDistance = Double.MAX_VALUE;
							for (int k = 0; k < corners.size(); k++) {
								double distance = Vector2d.distance(markerPoint, corners.get(k));
								if (distance < minDistance) {
									minDistance = distance;
								}
							}
							if (minDistance <= DISTANCE) {
								tp += SCORE;
								innerTp += SCORE;
							} else {
								fp += SCORE;
								innerFp += SCORE;
							}
						}
						outputWriter.write(imageFile.getName());
						outputWriter.newLine();
						outputWriter.write("Marker precision: " + getPrecision(innerTp, innerFp));
						outputWriter.newLine();
					} else {
						fn += SCORE * 4;
						outputWriter.write(imageFile.getName());
						outputWriter.newLine();
						outputWriter.write("Marker not found");
						outputWriter.newLine();
					}
					outputWriter.write("-------------------");
					outputWriter.newLine();
				}
			}
			double precision = getPrecision(tp, fp);
			double recall = getRecall(tp, fn);
			double fscore = getFScore(precision, recall);
			meanPrecision += precision;
			meanRecall += recall;
			meanFScore += fscore;
			outputWriter.write("Precision: " + precision);
			outputWriter.newLine();
			outputWriter.write("Recall: " + recall);
			outputWriter.newLine();
			outputWriter.write("FScore: " + fscore);
			outputWriter.newLine();
			outputWriter.newLine();
			System.out.println(it + 1);
		}
		outputWriter.write("-------------------");
		outputWriter.newLine();
		outputWriter.write("-------------------");
		outputWriter.newLine();
		outputWriter.write("Mean precision: " + meanPrecision / iterations);
		outputWriter.newLine();
		outputWriter.write("Mean recall: " + meanRecall / iterations);
		outputWriter.newLine();
		outputWriter.write("Mean fScore: " + meanFScore / iterations);
		outputWriter.flush();
		outputWriter.close();
	}

	public double getPrecision(double tp, double fp) {
		return tp / (tp + fp);
	}

	public double getRecall(double tp, double fn) {
		return tp / (tp + fn);
	}

	public double getFScore(double precision, double recall) {
		return 2 * precision * recall / (precision + recall);
	}

	private List<Vector2d> readCorners(File cornerFile) throws Exception {
		List<Vector2d> output = new ArrayList<>();
		List<String> points = Files.readAllLines(cornerFile.toPath(), Charset.forName("UTF-8"));
		for (String line : points) {
			String[] split = line.split(",");
			double x = Double.valueOf(split[0]);
			double y = Double.valueOf(split[1]);
			Vector2d vector = new Vector2d(x, y);
			output.add(vector);
		}
		return output;
	}
}