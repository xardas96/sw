package ar;

import java.util.ArrayList;
import java.util.List;

public class LineSegment {
	private Edgel start;
	private Edgel end;
	private Vector2d direction;
	private List<Edgel> supportingEdgels;
	private boolean merged;
	private boolean startCorner;
	private boolean endCorner;

	public LineSegment() {
		supportingEdgels = new ArrayList<Edgel>();
	}

	public void setStart(Edgel start) {
		this.start = start;
	}

	public void setEnd(Edgel stop) {
		this.end = stop;
	}

	public Vector2d getDirection() {
		return direction;
	}

	public void setDirection(Vector2d direction) {
		this.direction = direction;
	}

	public Edgel getStart() {
		return start;
	}

	public Edgel getEnd() {
		return end;
	}

	public void swapEndpoints() {
		Edgel temp = start;
		start = end;
		end = temp;
	}

	public void addSupportingEdgel(Edgel supprotingEdgel) {
		supportingEdgels.add(supprotingEdgel);
	}

	public List<Edgel> getSupportingEdgels() {
		return supportingEdgels;
	}

	public int getSupportingEdgelsSize() {
		return supportingEdgels.size();
	}

	public boolean isInLine(Edgel edgel) {
		boolean isCompatible = start.isOrientationCompatible(edgel);
		if (isCompatible) {
			int cross = (end.getX() - start.getX()) * (edgel.getY() - start.getY());
			cross -= (end.getY() - start.getY()) * (edgel.getX() - start.getX());
			int d1 = start.getX() - end.getX();
			int d2 = start.getY() - end.getY();
			float distance = (float) (cross / new Vector2d(d1, d2).getLength());
			isCompatible &= Math.abs(distance) < 0.75f;
		}
		return isCompatible;
	}

	public boolean isMerged() {
		return merged;
	}

	public void setMerged(boolean merged) {
		this.merged = merged;
	}

	public void setStartCorner(boolean startCorner) {
		this.startCorner = startCorner;
	}

	public boolean isStartCorner() {
		return startCorner;
	}

	public void setEndCorner(boolean endCorner) {
		this.endCorner = endCorner;
	}

	public boolean isEndCorner() {
		return endCorner;
	}
	
	@Override
	public String toString() {
		return "Line segment start: " + start.toString() + " stop: " + end.toString();
	}

	public boolean isOrientationCompatible(LineSegment segment) {
		return Vector2d.dot(direction, segment.direction) > 0.92f;
	}

	public Vector2d getIntersection(LineSegment segment) {
		Vector2d otherStartPosition = segment.start.getPosition();
		Vector2d otherEndPosition = segment.end.getPosition();
		double denom = ((otherEndPosition.getY() - otherStartPosition.getY()) * (end.getX() - start.getX())) - ((otherEndPosition.getX() - otherStartPosition.getX()) * (end.getY() - start.getY()));
		double nume_a = ((otherEndPosition.getX() - otherStartPosition.getX()) * (start.getY() - otherStartPosition.getY())) - ((otherEndPosition.getY() - otherStartPosition.getY()) * (start.getX() - otherStartPosition.getX()));
		double ua = nume_a / denom;
		double x = start.getX() + ua * (end.getX() - start.getX());
		double y = start.getY() + ua * (end.getY() - start.getY());
		return new Vector2d(x, y);
	}
}