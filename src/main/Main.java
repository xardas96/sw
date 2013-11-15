package main;

import gui.MainFrame;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import ar.DerivativeGaussianKernel;
import ar.DesktopMarkerFinder;

public class Main {

	public static void main(String[] args) throws Exception {
		InputStream is = new FileInputStream("londonTest.jpeg");
		InputStream is2 = new FileInputStream("londonTest.jpeg");
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
//		MarkerFinder ir = new DesktopMarkerFinder();
//		List<Marker> markers = ir.readImage(is);
	}
}