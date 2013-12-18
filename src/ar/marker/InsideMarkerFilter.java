package ar.marker;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class InsideMarkerFilter implements MarkerFilter{

	@Override
	public List<Marker> filterMarkers(List<Marker> marker, BufferedImage image) {
		List<Marker> result = new ArrayList<Marker>();
		for(int i = 0; i < marker.size(); i++){
			boolean found = false;
			for(int j = 0; j < marker.size() && !found; j++)
				if(i != j)
					found = Marker.isInside(marker.get(j), marker.get(i));
			if(!found)
				result.add(marker.get(i));
		}
		return result;
	}

}
