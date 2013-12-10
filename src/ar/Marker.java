package ar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Marker {
	private List<LineSegment> chain;
	private Vector2d corner1;
	private Vector2d corner2;
	private Vector2d corner3;
	private Vector2d corner4;

	public Marker() {

	}

	public Marker(Vector2d c1, Vector2d c2, Vector2d c3, Vector2d c4) {
		corner1 = c1;
		corner2 = c2;
		corner3 = c3;
		corner4 = c4;
	}

	public void setChain(List<LineSegment> chain) {
		this.chain = chain;
	}

	public void reconstructCorners() {
		corner1 = chain.get(0).getIntersection(chain.get(1));
		corner2 = chain.get(1).getIntersection(chain.get(2));
		if (chain.size() == 4) {
			corner3 = chain.get(2).getIntersection(chain.get(3));
			corner4 = chain.get(3).getIntersection(chain.get(0));
		} else {
			corner3 = chain.get(2).getEnd().getPosition();
			corner4 = chain.get(0).getStart().getPosition();
		}
	}

	public Vector2d getCorner1() {
		return corner1;
	}

	public Vector2d getCorner2() {
		return corner2;
	}

	public Vector2d getCorner3() {
		return corner3;
	}

	public Vector2d getCorner4() {
		return corner4;
	}
	
	public void setCorner1(Vector2d c) {
		corner1 =c;
	}

	public void setCorner2(Vector2d c) {
		corner2=  c;
	}

	public void setCorner3(Vector2d c) {
		corner3 = c;
	}

	public void  setCorner4(Vector2d c) {
		corner4 = c;
	}

	public static List<Marker> setMarkerOrinetation(List<Marker> markers) {
		List<Marker> result = new ArrayList<Marker>();
		for(int i = markers.size()-1; i >= 0; i--){
			boolean found = false;
			for(int j = 0; j < markers.size() && !found; j++){
				if(i != j){
					Marker m1 = markers.get(i);
					Marker m2 = markers.get(j);
					if(isInside(m1, m2)){
						Vector2d center = m1.corner1.add(m1.corner3).add(m1.corner2).add(m1.corner4);
						center.divide(4);
						Vector2d littleCenter = m2.corner1.add(m2.corner3).add(m2.corner2).add(m2.corner4);
						littleCenter.divide(4);
						List<Vector2d> sCorners = sortCornersByLengthTo(new Vector2d[]{m1.corner1, m1.corner2, m1.corner3, m1.corner4}, littleCenter);
						m1.corner3 = sCorners.get(0);
						Vector2d c_c3 = m1.corner3.subtract(center);
						c_c3.normalize();
						sCorners.remove(0);
						sortByCross(sCorners, c_c3, center);
						m1.corner4 = sCorners.get(2);
						m1.corner1 = sCorners.get(1);
						m1.corner2 = sCorners.get(0);
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
	
	public static List<Vector2d> sortByCross(List<Vector2d> vectors, final Vector2d normV, final Vector2d center){
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
	
	public static List<Vector2d> sortCornersByLengthTo(Vector2d[] corners, Vector2d v){
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

	/**
	 * Checks if m2 is inside m1
	 */
	private static boolean isInside(Marker m1, Marker m2) {
		double maxX = Marker.getMaxX(m1);
		double minX = Marker.getMinX(m1);
		double maxY = Marker.getMaxY(m1);
		double minY = Marker.getMinY(m1);
		boolean result = m2.corner1.getX() < maxX && m2.corner1.getX() > minX && m2.corner1.getY() < maxY && m2.corner1.getY() > minY;
		result &= m2.corner2.getX() < maxX && m2.corner2.getX() > minX && m2.corner2.getY() < maxY && m2.corner2.getY() > minY;
		result &= m2.corner3.getX() < maxX && m2.corner3.getX() > minX && m2.corner3.getY() < maxY && m2.corner3.getY() > minY;
		result &= m2.corner4.getX() < maxX && m2.corner4.getX() > minX && m2.corner4.getY() < maxY && m2.corner4.getY() > minY;
		return result;
	}
	
	public static double getMaxX(Marker m){
		return Math.max(m.corner1.getX(), Math.max(m.corner2.getX(), Math.max(m.corner3.getX(),m.corner4.getX())));
	}
	
	public static double getMinX(Marker m){
		return Math.min(m.corner1.getX(), Math.min(m.corner2.getX(), Math.min(m.corner3.getX(),m.corner4.getX())));
	}
	
	public static double getMaxY(Marker m){
		return Math.max(m.corner1.getY(), Math.max(m.corner2.getY(), Math.max(m.corner3.getY(),m.corner4.getY())));
	}
	
	public static double getMinY(Marker m){
		return Math.min(m.corner1.getY(), Math.min(m.corner2.getY(), Math.min(m.corner3.getY(),m.corner4.getY())));
	}
}