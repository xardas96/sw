package tests;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ar.DesktopMarkerFinder;
import ar.image.ImageOperations;
import ar.marker.CornerMarkerFilter;
import ar.marker.InsideMarkerFilter;
import ar.marker.LengthMarkerFilter;
import ar.marker.Marker;
import ar.marker.MarkerFilter;
import ar.marker.MemoryMarkerFilter;
import ar.orientation.CornerBasedOrientationFinder;

public class FilterTester {
	private int iterations;
	private double tp;
	private double fn;
	private double meanRecall;
	private DesktopMarkerFinder finder;

	public FilterTester(int iterations) {
		finder = new DesktopMarkerFinder();
		this.iterations = iterations;
	}

	public void test(String inputFile, String outputFile) throws Exception {
		meanRecall = 0;
		File input = new File(inputFile);
		List<String[]> splits = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] split = line.split(";");
			splits.add(split);
		}
		reader.close();
		File output = new File(outputFile);
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(output));
		for (int it = 0; it < iterations; it++) {
			tp = 0;
			fn = 0;
			outputWriter.write("******************");
			outputWriter.newLine();
			outputWriter.write("Iteration: " + it);
			outputWriter.newLine();
			outputWriter.write("******************");
			outputWriter.newLine();
			for (String[] split : splits) {
				File imageFile = new File(split[0]);
				System.out.println(split[0]);
				BufferedImage img = ImageIO.read(imageFile);
				int declaredMarkers = Integer.valueOf(split[1]);
				MarkerFilter insideFilter = new InsideMarkerFilter();
				MarkerFilter cornerFilter = new CornerMarkerFilter();
				MarkerFilter lengthFilter = new LengthMarkerFilter();
				MarkerFilter memoryFilter = new MemoryMarkerFilter(new MarkerFilter[] { cornerFilter, lengthFilter });
				BufferedImage debugImage = ImageOperations.copyImage(img);
				List<Marker> markers = finder.findMarkers(img, debugImage);
				markers = insideFilter.filterMarkers(markers, img);
				markers = memoryFilter.filterMarkers(markers, img);
				double innerTp = 0;
				double innerFn = 0;
				if (markers != null && !markers.isEmpty()) {
					try {
						markers = new CornerBasedOrientationFinder().setMarkerOrinetation(markers, img);
					} catch (Exception e) {
					}
					if (markers != null && !markers.isEmpty()) {
						double ratio = markers.size() / (double) declaredMarkers;
						tp += ratio;
						innerTp += ratio;
						fn += 1 - ratio;
						innerFn += 1 - ratio;
					} else {
						fn += 1.0;
						innerFn += 1.0;
					}
				} else {
					innerFn = 1;
				}
				outputWriter.write(imageFile.getName());
				outputWriter.newLine();
				double recall = getRecall(innerTp, innerFn);
				outputWriter.write("Marker recall: " + recall);
				outputWriter.newLine();
				outputWriter.write("-------------------");
				outputWriter.newLine();
			}
			double recall = getRecall(tp, fn);
			outputWriter.write("Recall: " + recall);
			outputWriter.newLine();
			outputWriter.newLine();
			meanRecall += recall;
			System.out.println(it + 1);
		}
		outputWriter.write("-------------------");
		outputWriter.newLine();
		outputWriter.write("-------------------");
		outputWriter.newLine();
		outputWriter.write("Mean recall: " + meanRecall / iterations);
		outputWriter.flush();
		outputWriter.close();
	}

	public double getRecall(double tp, double fn) {
		return tp / (tp + fn);
	}

}