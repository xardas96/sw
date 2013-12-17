package ar.marker;

import java.awt.image.BufferedImage;
import java.util.List;

import ar.Marker;

public interface MarkerFilter {
	public List<Marker> filterMarkers(List<Marker> marker, BufferedImage image);
}
