package ar;

import java.io.InputStream;
import java.util.List;

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

	public List<Marker> readImage(InputStream is) throws Exception;
}