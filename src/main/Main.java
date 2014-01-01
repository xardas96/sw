package main;

import gui.MainFrame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Jama.Matrix;
import ar.DesktopMarkerFinder;
import ar.MarkerFinder;
import ar.code.CodeDecryptor;
import ar.code.CodeRetreiver;
import ar.image.ImageOperations;
import ar.marker.CornerMarkerFilter;
import ar.marker.InsideMarkerFilter;
import ar.marker.LengthMarkerFilter;
import ar.marker.Marker;
import ar.marker.MarkerFilter;
import ar.marker.MemoryMarkerFilter;
import ar.orientation.CornerBasedOrientationFinder;
import ar.perspective.PerspectiveFinder;
import ar.utils.Vector2d;

import com.github.sarxos.webcam.Webcam;

public class Main {

	public static void main(String[] args) throws Exception {
		// testMarkerFinder("fixedTest.png");
		testCamera(new Dimension(320, 240));
		// testMarkerPerspective("testowy.jpg");
		// testMarkerPerspective("rotTest.png");
	}

	// private static void testMarkerFinder(String fileName) throws Exception {
	// InputStream is = new FileInputStream(fileName);
	// InputStream is2 = new FileInputStream(fileName);
	// DesktopMarkerFinder finder = new DesktopMarkerFinder();
	// // Image im = finder.drawEdgels(is);
	// // Image im = finder.drawLineSegments(is);
	// Image im = finder.drawMarkers(is);
	// BufferedImage image = ImageIO.read(is2);
	// Image edgesX = DerivativeGaussianKernel.applyEdgeKernelX(image);
	// Image edgesY = DerivativeGaussianKernel.applyEdgeKernelY(image);
	// final MainFrame mf = new MainFrame(im, "Obraz");
	// SwingUtilities.invokeLater(new Runnable() {
	// @Override
	// public void run() {
	// mf.setVisible(true);
	// }
	// });
	//
	// final MainFrame mf2 = new MainFrame(edgesX, "Konwolucja kolumn¹");
	// SwingUtilities.invokeLater(new Runnable() {
	// @Override
	// public void run() {
	// mf2.setVisible(true);
	// }
	// });
	//
	// final MainFrame mf3 = new MainFrame(edgesY, "Konwolucja wierszem");
	// SwingUtilities.invokeLater(new Runnable() {
	// @Override
	// public void run() {
	// mf3.setVisible(true);
	// }
	// });
	// MarkerFinder ir = new DesktopMarkerFinder();
	// List<Marker> markers = ir.readImage(is);
	// }

	private static void testCamera(Dimension cameraDimension) throws IOException {
		DesktopMarkerFinder finder = new DesktopMarkerFinder();
		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(cameraDimension);
		webcam.open();
		final MainFrame mf = new MainFrame(webcam.getImage(), "Obraz", false);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf.setVisible(true);
			}
		});

		Timer timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mf.setFPS();
			}
		});

		final MainFrame mf2 = new MainFrame(null, "Marker", false);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf2.setVisible(true);
			}
		});

		final MainFrame mf3 = new MainFrame(null, "Silnik", true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf3.setVisible(true);
			}
		});

		timer.start();
		MarkerFilter insideFilter = new InsideMarkerFilter();
		MarkerFilter cornerFilter = new CornerMarkerFilter();
		MarkerFilter lengthFilter = new LengthMarkerFilter();
		MarkerFilter memoryFilter = new MemoryMarkerFilter(new MarkerFilter[] { cornerFilter, lengthFilter });
		while (true) {
			BufferedImage img = webcam.getImage();
			if (img != null) {
				mf3.setImage(img);
				BufferedImage debugImage = ImageOperations.copyImage(img);
				List<Marker> markers = finder.findMarkers(img, debugImage);
				mf.setImage(debugImage);
				markers = insideFilter.filterMarkers(markers, img);
				markers = memoryFilter.filterMarkers(markers, img);
				try {
					if (markers != null && !markers.isEmpty()) {
						markers = new CornerBasedOrientationFinder().setMarkerOrinetation(markers, img);
						DesktopMarkerFinder.drawMarkers(debugImage, markers);
						mf.setImage(debugImage);
						if (!markers.isEmpty()) {
							Marker marker = markers.get(0);
							double maxX = Marker.getMaxX(marker);
							double minX = Marker.getMinX(marker);
							double maxY = Marker.getMaxY(marker);
							double minY = Marker.getMinY(marker);
							Vector2d minV = new Vector2d(minX, minY);
							BufferedImage subImage = img.getSubimage((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
							File temp = new File("temp.png");
							temp.deleteOnExit();
							ImageIO.write(subImage, "png", temp);
							subImage = ImageIO.read(temp);
							Marker subMarker = new Marker(marker.getCorner1().subtract(minV), marker.getCorner2().subtract(minV), marker.getCorner3().subtract(minV), marker.getCorner4().subtract(minV));
							PerspectiveFinder pFinder = new PerspectiveFinder(MarkerFinder.MARKER_DIMENSION.width, MarkerFinder.MARKER_DIMENSION.height);
							Matrix m = pFinder.findPerspectiveMatrix(subMarker);
							BufferedImage markerImage = pFinder.transformBufferedImage(m, subImage, MarkerFinder.MARKER_DIMENSION);
							CodeRetreiver cr = new CodeRetreiver();
							int[] code = cr.retreiveCode(markerImage);
							System.out.println(CodeDecryptor.decryptCode(code));
							mf2.setImage(markerImage);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// private static void testMarkerPerspective(String test) throws IOException
	// {
	// InputStream is = new FileInputStream(test);
	// BufferedImage image = ImageIO.read(is);
	// DesktopMarkerFinder finder = new DesktopMarkerFinder();
	// List<Marker> markers = finder.findMarkers(image);
	// markers = new
	// CornerBasedOrientationFinder().setMarkerOrinetation(markers, image);
	// if (!markers.isEmpty()) {
	// long time1 = System.currentTimeMillis();
	// Marker marker = markers.get(0);
	// double maxX = Marker.getMaxX(marker);
	// double minX = Marker.getMinX(marker);
	// double maxY = Marker.getMaxY(marker);
	// double minY = Marker.getMinY(marker);
	// Vector2d minV = new Vector2d(minX, minY);
	// // BufferedImage subImage = image.getSubimage((int)minX, (int)minY,(int)
	// (maxX-minX),(int) (maxY-minY));
	// // File temp = new File("temp.png");
	// // ImageIO.write(subImage, "png", temp);
	// // subImage = ImageIO.read(temp);
	// // Marker subMarker = new Marker(marker.getCorner1().subtract(minV),
	// marker.getCorner2().subtract(minV), marker.getCorner3().subtract(minV),
	// marker.getCorner4().subtract(minV));
	// PerspectiveFinder pFinder = new
	// PerspectiveFinder(MarkerFinder.MARKER_DIMENSION.width,
	// MarkerFinder.MARKER_DIMENSION.height);
	// Matrix m = pFinder.findPerspectiveMatrix(marker);
	// BufferedImage markerImage = pFinder.transformBufferedImage(m,image,
	// MarkerFinder.MARKER_DIMENSION);
	// long time2 = System.currentTimeMillis();
	// System.out.println("Czas: " + (time2-time1));
	// final MainFrame mf = new MainFrame(markerImage, "Obraz");
	// SwingUtilities.invokeLater(new Runnable() {
	// @Override
	// public void run() {
	// mf.setVisible(true);
	// }
	// });
	// }

	// }
}