package ar;

import java.util.ArrayList;
import java.util.List;

import ar.utils.Vector2d;

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
			double cross = (end.getX() - start.getX()) * (edgel.getY() - start.getY());
			cross -= (end.getY() - start.getY()) * (edgel.getX() - start.getX());
			double d1 = start.getX() - end.getX();
			double d2 = start.getY() - end.getY();
			float distance = (float) (cross / new Vector2d(d1, d2).getLength());
			isCompatible &= Math.abs(distance) < 0.75;
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
		return Vector2d.dot(direction, segment.direction) > 0.92;
	}

	public Vector2d getIntersection(LineSegment segment) {
		double denom = ((segment.end.getPosition().getY() - segment.start.getPosition().getY()) * (end.getPosition().getX() - start.getPosition().getX())) - ((segment.end.getPosition().getX() - segment.start.getPosition().getX()) * (end.getPosition().getY() - start.getPosition().getY()));
		double nume_a = ((segment.end.getPosition().getX() - segment.start.getPosition().getX()) * (start.getPosition().getY() - segment.start.getPosition().getY())) - ((segment.end.getPosition().getY() - segment.start.getPosition().getY()) * (start.getPosition().getX() - segment.start.getPosition().getX()));
		double ua = nume_a / denom;
		double x = start.getPosition().getX() + ua * (end.getPosition().getX() - start.getPosition().getX());
		double y = start.getPosition().getY() + ua * (end.getPosition().getY() - start.getPosition().getY());
		return new Vector2d(x, Math.abs(y));
	}
}