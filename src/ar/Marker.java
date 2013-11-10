package ar;

import java.util.List;

public class Marker {
	private List<LineSegment> chain;
	private Vector2d corner1;
	private Vector2d corner2;
	private Vector2d corner3;
	private Vector2d corner4;

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
}