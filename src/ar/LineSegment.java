package ar;

import java.util.ArrayList;
import java.util.List;

public class LineSegment {
	private Edgel start;
	private Edgel end;
	private Vector2d direction;
	private List<Edgel> supportingEdgels;

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
			float distance = (float) (cross / Math.sqrt((d1 * d1) + (d2 * d2)));
			isCompatible = Math.abs(distance) < 0.75f;
		}
		return isCompatible;
	}
}