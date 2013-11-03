package main;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import ar.DesktopMarkerFinder;
import ar.Marker;
import ar.MarkerFinder;

public class Main {

	public static void main(String[] args) throws Exception {
		InputStream is = new FileInputStream("test.jpg");
		MarkerFinder ir = new DesktopMarkerFinder();
		List<Marker> markers = ir.readImage(is);
	}
}