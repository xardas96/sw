package ar.marker;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ar.utils.Vector2d;

/**
 *Works after orientation fix in markers! 
 */
public class CornerMarkerFilter implements MarkerFilter{
	public static double dotTreshold = 0.3;

	@Override
	public List<Marker> filterMarkers(List<Marker> marker, BufferedImage image) {
		List<Marker> result = new ArrayList<Marker>();
		for(int i = 0; i < marker.size(); i++){
			Marker m = marker.get(i);
			Vector2d[] corners = m.getCornerArray();
			boolean good = true;
			for(int j = 0; j < corners.length && good; j++){
				int less = j==0?corners.length-1:j-1;
				int more = j==corners.length-1?0:j+1;
				Vector2d c1 = corners[less].subtract(corners[j]);
				c1.normalize();
				Vector2d c2 = corners[more].subtract(corners[j]);
				c2.normalize();
				double dot = Math.abs(Vector2d.dot(c1, c2));
				good = dot < dotTreshold;
			}
			if(good)
				result.add(m);
		}
		return result;
	}

}
