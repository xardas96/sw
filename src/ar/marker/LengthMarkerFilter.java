package ar.marker;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LengthMarkerFilter implements MarkerFilter {
	// przy ma³ym tresholdzie (0.1), Dobrze dzia³a eleminuj¹c "niepe³ne"
	// markery,
	// ale niestety eliminuje tez markery pokazywane pod du¿ym k¹tem
	public static double lengthTreshold = 0.1;

	@Override
	public List<Marker> filterMarkers(List<Marker> marker, BufferedImage image) {
		List<Marker> result = new ArrayList<Marker>();
		for (int i = 0; i < marker.size(); i++) {
			Marker m = marker.get(i);
			double l1 = m.getCorner1().subtract(m.getCorner2()).getLength();
			double l2 = m.getCorner1().subtract(m.getCorner4()).getLength();
			if (checkLength(l1, l2)) {
				double l3 = m.getCorner2().subtract(m.getCorner3()).getLength();
				if (checkLength(l1, l3)) {
					double l4 = m.getCorner3().subtract(m.getCorner4()).getLength();
					if (checkLength(l1, l4))
						result.add(m);
				}
			}
		}
		return result;
	}

	private boolean checkLength(double l1, double l2) {
		return Math.abs(l1 - l2) / l1 < lengthTreshold;
	}

}
