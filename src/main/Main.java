package main;

import gui.MainFrame;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ar.DerivativeGaussianKernel;
import ar.DesktopMarkerFinder;
import ar.Marker;
import ar.MarkerFinder;

import com.github.sarxos.webcam.Webcam;

public class Main {

	public static void main(String[] args) throws Exception {
		// testMarkerFinder("fixedTest.png");
		testCamera(new Dimension(320,240));
	}

	private static void testMarkerFinder(String fileName) throws Exception {
		InputStream is = new FileInputStream(fileName);
		InputStream is2 = new FileInputStream(fileName);
		DesktopMarkerFinder finder = new DesktopMarkerFinder();
		// Image im = finder.drawEdgels(is);
		// Image im = finder.drawLineSegments(is);
		Image im = finder.drawMarkers(is);
		BufferedImage image = ImageIO.read(is2);
		Image edgesX = DerivativeGaussianKernel.applyEdgeKernelX(image);
		Image edgesY = DerivativeGaussianKernel.applyEdgeKernelY(image);
		final MainFrame mf = new MainFrame(im, "Obraz");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf.setVisible(true);
			}
		});

		final MainFrame mf2 = new MainFrame(edgesX, "Konwolucja kolumn¹");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf2.setVisible(true);
			}
		});

		final MainFrame mf3 = new MainFrame(edgesY, "Konwolucja wierszem");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf3.setVisible(true);
			}
		});
		MarkerFinder ir = new DesktopMarkerFinder();
		List<Marker> markers = ir.readImage(is);
	}

	private static void testCamera(Dimension cameraDimension) {
		DesktopMarkerFinder finder = new DesktopMarkerFinder();
		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(cameraDimension);
		webcam.open();

		final MainFrame mf = new MainFrame(webcam.getImage(), "Obraz");
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
		
		timer.start();
		while (true) {
			Image im = finder.drawMarkers(webcam.getImage());
			//Image im = finder.drawLineSegments(webcam.getImage());
			mf.setImage(im);
		}
	}
}