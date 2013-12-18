package ar;

import java.awt.Dimension;
import java.io.InputStream;
import java.util.List;

import ar.marker.Marker;

/**
 * An interface to enable desktop and Android implementations of reading an
 * image
 * 
 * @author XardaS
 * 
 */
public interface MarkerFinder {
	public static final int REGION_DIMENSION = 40;
	public static final int SCAN_LINE_DIMENSION = 5;
	public static final int TRESHOLD = 20 * 20;
	public static final int EDGELS_ONLINE = 5;
	public static final int WHITETRESHOLD = 10;
	public static final Dimension MARKER_DIMENSION = new Dimension(125, 125);

	public List<Marker> readImage(InputStream is) throws Exception;
}