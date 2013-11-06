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
	public static final int[] FILTER_VECTOR= {-3,-5,0,5,3};
	public static final int TRESHOLD = 16*16; //taki by³ u nich
	public static final int RED_SHIFT = 16;
	public static final int GREEN_SHIFT = 8;
	public static final int BLUE_SHIFT = 0;
	public static final int EDGELS_ONLINE = 5;

	public List<Marker> readImage(InputStream is) throws Exception;
}