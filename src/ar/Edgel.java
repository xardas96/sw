package ar;

public class Edgel {
	private Vector2d position;
	private Vector2d direction; // znormalizowany wektor 2 elementowy (x i y);

	public Edgel(int x, int y) {
		position = new Vector2d(x, y);
	}
	
	public int getX() {
		return (int) Math.round(position.getX());
	}

	public int getY() {
		return (int) position.getY();
	}
	
	public Vector2d getPosition() {
		return position;
	}

	public Vector2d getDirection() {
		return direction;
	}
	
	public void setDirection(Vector2d direction) {
		this.direction = direction;
	}
	
	public boolean isOrientationCompatible(Edgel edgel) {
		return Vector2d.dot(direction, edgel.direction) > 0.38f;
	}

	public String toString() {
		return "Edgel (" + position.getX() + ", " + position.getY() + ")";
	}
}