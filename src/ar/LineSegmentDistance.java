package ar;

public class LineSegmentDistance implements Comparable<LineSegmentDistance>{
	private double distance;
	private int index;
	public LineSegmentDistance(double d, int i){
		distance = d;
		index = i;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public int compareTo(LineSegmentDistance o) {
		return new Double(distance).compareTo(o.distance);
	}
	
	
	
	
}
