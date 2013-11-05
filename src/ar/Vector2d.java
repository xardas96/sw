package ar;

public class Vector2d {
	private double x;
	private double y;

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getLength() {
		return Math.sqrt(x * x + y * y);
	}

	public void normalize() {
		double invLegth = 1.0 / getLength();
		x *= invLegth;
		y *= invLegth;
	}

	public Vector2d subtract(Vector2d vector) {
		double x = this.x - vector.x;
		double y = this.y - vector.y;
		return new Vector2d(x, y);
	}

	public static double dot(Vector2d v1, Vector2d v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}
}