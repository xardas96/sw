package ar.marker;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ar.utils.Vector2d;

/**
 * Works after orientation fix in markers!
 */

public class MemoryMarkerFilter extends CornerMarkerFilter {
	private MarkerFilter[] filters;
	private List<Marker> memory = new ArrayList<Marker>();
	private static final double matchTreshlod = 6;

	public MemoryMarkerFilter(MarkerFilter[] filters) {
		this.filters = filters;
	}

	@Override
	public List<Marker> filterMarkers(List<Marker> marker, BufferedImage image) {
		List<Marker> result = marker;
		for (int i = 0; i < filters.length; i++)
			result = filters[i].filterMarkers(result, image);
		for (int i = 0; i < marker.size(); i++) {
			Marker m = marker.get(i);
			if (!result.contains(m)) {
				Marker goodM = memoryCheck(m);
				if (goodM != null)
					result.add(goodM);
			}
		}
		memory.clear();
		memory.addAll(result);
		return result;
	}

	private Marker memoryCheck(Marker m) {
		Marker result = null;
		for (int i = 0; i < memory.size() && result == null; i++) {
			Marker mem = memory.get(i);
			Vector2d[] memC = mem.getCornerArray();
			Vector2d[] mC = m.getCornerArray();
			Vector2d[] cV = new Vector2d[memC.length];
			double[] length = new double[memC.length];
			for (int j = 0; j < memC.length; j++) {
				cV[j] = memC[j].subtract(mC[j]);
				length[j] = cV[j].getLength();
				cV[j].normalize();
			}
			int lengthMatch = 0;
			int directionMatch = 0;
			for (int j = 0; j < length.length; j++) {
				for (int k = j + 1; k < length.length; k++) {
					double l = Math.abs(length[j] - length[k]) / length[j];
					if (l < 0.1 && Math.abs(length[j] - length[k]) < 50)
						;
					lengthMatch++;
					double dot = Math.abs(Vector2d.dot(cV[j], cV[k]));
					if (dot > 0.85)
						directionMatch++;
				}
			}
			if (lengthMatch >= matchTreshlod && directionMatch >= matchTreshlod)
				result = mem;
		}
		return result;
	}

}
