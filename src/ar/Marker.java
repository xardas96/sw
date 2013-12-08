package ar;

import java.util.ArrayList;
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
						List<Vector2d> sCorners = sortCornersByLengthTo(new Vector2d[]{m1.corner1, m1.corner2, m1.corner3, m1.corner4}, m2.getCorner1());
						m1.corner1 = sCorners.get(sCorners.size()-1);
						m1.corner3 = sCorners.get(0);
						Vector2d center = m1.corner1.add(m1.corner3);
						center.divide(2);
						Vector2d c_c1 = m1.corner1.subtract(center);
						c_c1.normalize();
						Vector2d c_v1 = sCorners.get(1).subtract(center);
						c_v1.normalize();
						double dot = Vector2d.dot(c_c1, c_v1);
						if(dot>=0){
							m1.corner2 = sCorners.get(1);
							m1.corner4 = sCorners.get(2);
						}
						else{
							m1.corner2 = sCorners.get(2);
							m1.corner4 = sCorners.get(1);
						}
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
		Vector2d cornerIn = m2.corner1;
		return cornerIn.getX() < getMaxX(m1) && cornerIn.getX() > getMinX(m1) && cornerIn.getY() < getMaxY(m1) && cornerIn.getY() > getMinY(m1);
	}
	
	private static double getMaxX(Marker m){
		return Math.max(m.corner1.getX(), Math.max(m.corner2.getX(), Math.max(m.corner3.getX(),m.corner4.getX())));
	}
	
	private static double getMinX(Marker m){
		return Math.min(m.corner1.getX(), Math.min(m.corner2.getX(), Math.min(m.corner3.getX(),m.corner4.getX())));
	}
	
	private static double getMaxY(Marker m){
		return Math.max(m.corner1.getY(), Math.max(m.corner2.getY(), Math.max(m.corner3.getY(),m.corner4.getY())));
	}
	
	private static double getMinY(Marker m){
		return Math.min(m.corner1.getY(), Math.min(m.corner2.getY(), Math.min(m.corner3.getY(),m.corner4.getY())));
	}
}