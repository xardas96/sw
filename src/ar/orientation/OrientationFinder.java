package ar.orientation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.Marker;
import ar.Vector2d;

public abstract class OrientationFinder {
	public abstract List<Marker> setMarkerOrinetation(List<Marker> markers, BufferedImage image);
	public List<Vector2d> sortCornersByLengthTo(Vector2d[] corners, Vector2d v){
		List<Double> lengths = new ArrayList<Double>(corners.length);
		List<Vector2d> list = new ArrayList<Vector2d>(corners.length);
		for(int i = 0; i < corners.length; i++){
			double l = corners[i].subtract(v).getLength();
			int index = 0;
			boolean found = false;
			for(int j = 0; j < lengths.size()&&!found; j++){
				if(found = (lengths.get(j) > l))
					index = j;
			}
			if(!found){
				lengths.add(l);
				list.add(corners[i]);
			}
			else{
				lengths.add(index, l);
				list.add(index,corners[i]);
			}
		}
		return list;
	}
	
	public List<Vector2d> sortByCross(List<Vector2d> vectors, final Vector2d normV, final Vector2d center){
		Collections.sort(vectors, new Comparator<Vector2d>(){
			@Override
			public int compare(Vector2d v1, Vector2d v2) {
				Vector2d tempV1 = v1.subtract(center);
				tempV1.normalize();
				Vector2d tempV2 = v2.subtract(center);
				tempV2.normalize();
				double crossV1 = Vector2d.cross(normV, v1);
				double crossV2 = Vector2d.cross(normV, v2);
				return crossV1 > crossV2?1:-1;
			}			
		});
		return vectors;
	}
	
	
	public double[] sortByBlackness(Vector2d[] vectors, double[] bl){
		double[] blackness = Arrays.copyOf(bl, bl.length);
		for(int i = 0; i < blackness.length-1; i++){
			for(int j = i; j < blackness.length; j++){
				if(blackness[i] > blackness[j]){
					double t = blackness[i];
					blackness[i] = blackness[j];
					blackness[j] = t;
					Vector2d temp = vectors[i];
					vectors[i] = vectors[j];
					vectors[j] = temp;
				}
			}
		}
		return blackness;
	}
	
	protected void setOtherCornersBasedOnThrid(Marker m, Vector2d center){
		Vector2d c_c3 = m.getCorner3().subtract(center);
		c_c3.normalize();
		List<Vector2d> sCorners = new ArrayList<>();
		sCorners.add(m.getCorner1());
		sCorners.add(m.getCorner2());
		sCorners.add(m.getCorner4());
		sortByCross(sCorners, c_c3, center);
		m.setCorner4(sCorners.get(2));
		m.setCorner1(sCorners.get(1));
		m.setCorner2(sCorners.get(0));
	}
}
