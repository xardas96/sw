package ar.orientation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ar.marker.Marker;
import ar.utils.Vector2d;

public class MarkerBasedOrientationFinder extends OrientationFinder{

	@Override
	public List<Marker> setMarkerOrinetation(List<Marker> markers, BufferedImage image) {
		List<Marker> result = new ArrayList<Marker>();
		for(int i = markers.size()-1; i >= 0; i--){
			boolean found = false;
			for(int j = 0; j < markers.size() && !found; j++){
				if(i != j){
					Marker m1 = markers.get(i);
					Marker m2 = markers.get(j);
					if(Marker.isInside(m1, m2)){
						Vector2d center = m1.getCorner1().add(m1.getCorner3()).add(m1.getCorner2()).add(m1.getCorner4());
						center.divide(4);
						Vector2d littleCenter = m2.getCorner1().add(m2.getCorner3()).add(m2.getCorner2()).add(m2.getCorner4());
						littleCenter.divide(4);
						List<Vector2d> sCorners = sortCornersByLengthTo(new Vector2d[]{m1.getCorner1(), m1.getCorner2(), m1.getCorner3(), m1.getCorner4()}, littleCenter);
						m1.setCorner3(sCorners.get(0));
						setOtherCornersBasedOnThrid(m1, center);
						found = true;
						markers.remove(m1);
						markers.remove(m2);
						i--;
						result.add(m1);
					}
				}
			}
		}
		return result;
	}

}
