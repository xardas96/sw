package main;

import gui.MainFrame;

import java.awt.Color;
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
import ar.camera.CameraIntristics;
import ar.code.CodeDecryptor;
import ar.code.CodeRetreiver;
import ar.image.ImageOperations;
import ar.marker.CornerMarkerFilter;
import ar.marker.InsideMarkerFilter;
import ar.marker.LengthMarkerFilter;
import ar.marker.Marker;
import ar.marker.MarkerFilter;
import ar.marker.MemoryMarkerFilter;
import ar.models.ArModel;
import ar.models.ModelLibrary;
import ar.orientation.CornerBasedOrientationFinder;
import ar.perspective.PerspectiveFinder;
import ar.posit.Posit;
import ar.utils.Vector2d;

import com.github.sarxos.webcam.Webcam;

public class Main {

	public static void main(String[] args) throws Exception {
		ModelLibrary.init();
		CameraIntristics.loadInstisticsFromFile("out_camera_data_320.xml");
		testCamera(new Dimension(320, 240));
	}

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
				mf2.setLocation(0, 350);
				mf2.setVisible(true);
			}
		});

		final MainFrame mf3 = new MainFrame(null, "Silnik", true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf3.setLocation(350, 0);
				mf3.setVisible(true);
			}
		});

		timer.start();
		MarkerFilter insideFilter = new InsideMarkerFilter();
		MarkerFilter cornerFilter = new CornerMarkerFilter();
		MarkerFilter lengthFilter = new LengthMarkerFilter();
		MarkerFilter memoryFilter = new MemoryMarkerFilter(new MarkerFilter[] { cornerFilter, lengthFilter });

		Posit posit = new Posit(cameraDimension);

		while (true) {
			BufferedImage img = webcam.getImage();
			if (img != null) {
				BufferedImage debugImage = ImageOperations.copyImage(img);
				BufferedImage erasedMarkers = ImageOperations.copyImage(img);
				List<Marker> markers = finder.findMarkers(img, debugImage);
				markers = insideFilter.filterMarkers(markers, img);
				markers = memoryFilter.filterMarkers(markers, img);
				try {
					if (markers != null && !markers.isEmpty()) {
						markers = new CornerBasedOrientationFinder().setMarkerOrinetation(markers, img);
						DesktopMarkerFinder.drawMarkers(debugImage, markers);
						mf.setImage(debugImage);
						ImageOperations.eraseMarkers(erasedMarkers, markers, Color.BLACK);
						mf3.setImage(erasedMarkers);
						for (Marker marker : markers) {
							double maxX = Marker.getMaxX(marker);
							double minX = Marker.getMinX(marker);
							double maxY = Marker.getMaxY(marker);
							double minY = Marker.getMinY(marker);
							Vector2d minV = new Vector2d(minX, minY);
							BufferedImage subImage = img.getSubimage((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
							File temp = new File("temp.png");
							ImageIO.write(subImage, "png", temp);
							subImage = ImageIO.read(temp);
							temp.delete();
							Marker subMarker = new Marker(marker.getCorner1().subtract(minV), marker.getCorner2().subtract(minV), marker.getCorner3().subtract(minV), marker.getCorner4().subtract(minV));
							PerspectiveFinder pFinder = new PerspectiveFinder(MarkerFinder.MARKER_DIMENSION.width, MarkerFinder.MARKER_DIMENSION.height);
							Matrix m = pFinder.findPerspectiveMatrix(subMarker);
							BufferedImage markerImage = pFinder.transformBufferedImage(m, subImage, MarkerFinder.MARKER_DIMENSION);
							CodeRetreiver cr = new CodeRetreiver();
							int[] code = cr.retreiveCode(markerImage);
							mf2.setImage(markerImage);
							mf2.setFPS(CodeDecryptor.decryptCode(code));
							posit.calculatePosit(marker);
							ArModel model = ModelLibrary.getModel(CodeDecryptor.decryptCode(code));
							mf3.setModel(model, posit.getTranslate(), posit.getRotate(), 1.0);
						}
					} else {
						mf.setImage(img);
						mf3.setImage(img);
					}
				} catch (Exception e) {
				}
			}
			mf3.clearModels();
		}
	}
}