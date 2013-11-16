package main;

import gui.MainFrame;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import ar.DerivativeGaussianKernel;
import ar.DesktopMarkerFinder;
import ar.Marker;
import ar.MarkerFinder;

import com.github.sarxos.webcam.Webcam;

public class Main {

	public static void main(String[] args) throws Exception {
//		testMarkerFinder("fixedTest.png");
		testCamera(new Dimension(176,144));
	}
	
	private static void testMarkerFinder(String fileName) throws Exception {
		InputStream is = new FileInputStream(fileName);
		InputStream is2 = new FileInputStream(fileName);
		DesktopMarkerFinder finder = new DesktopMarkerFinder();
//		Image im = finder.drawEdgels(is);
//		Image im = finder.drawLineSegments(is);
		Image im = finder.drawMarkers(is);
		BufferedImage image = ImageIO.read(is2);
		Image edgesX = DerivativeGaussianKernel.applyEdgeKernelX(image);
		Image edgesY = DerivativeGaussianKernel.applyEdgeKernelY(image);
		final MainFrame mf = new MainFrame(im, "Obraz");
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				mf.setVisible(true);
			}
		});
		
		final MainFrame mf2 = new MainFrame(edgesX, "Konwolucja kolumn¹");
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				mf2.setVisible(true);
			}
		});
		
		final MainFrame mf3 = new MainFrame(edgesY, "Konwolucja wierszem");
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				mf3.setVisible(true);
			}
		});
		MarkerFinder ir = new DesktopMarkerFinder();
		List<Marker> markers = ir.readImage(is);
	}
	
	private static void testCamera(Dimension cameraDimension) {
		Webcam webcam = Webcam.getDefault();
		webcam.open();
		double start = System.currentTimeMillis();
		BufferedImage img = webcam.getImage();
		double stop = System.currentTimeMillis();
		try {
			ImageIO.write(img, "png", new File("test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(stop - start);
		webcam.close();
	}
}