package main;

import gui.MainFrame;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.swing.SwingUtilities;

import ar.DesktopMarkerFinder;
import ar.Marker;
import ar.MarkerFinder;

public class Main {

	public static void main(String[] args) throws Exception {
		InputStream is = new FileInputStream("londonTest.jpeg");
		DesktopMarkerFinder finder = new DesktopMarkerFinder();
		Image im = finder.drawEdgels(is);
		final MainFrame mf = new MainFrame(im);
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				mf.setVisible(true);
			}
		});
//		MarkerFinder ir = new DesktopMarkerFinder();
//		List<Marker> markers = ir.readImage(is);
	}
}