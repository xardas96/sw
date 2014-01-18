package ar.utils;

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

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getLength() {
		return Math.sqrt(x * x + y * y);
	}

	public double getSquaredLength() {
		return x * x + y * y;
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

	public Vector2d add(Vector2d vector) {
		double x = this.x + vector.x;
		double y = this.y + vector.y;
		return new Vector2d(x, y);
	}

	public Vector2d multiply(Vector2d vector) {
		double x = this.x * vector.x;
		double y = this.y * vector.y;
		return new Vector2d(x, y);
	}

	public void multiply(double d) {
		x *= d;
		y *= d;
	}

	public void divide(double d) {
		x /= d;
		y /= d;
	}

	public Vector2d negate() {
		return new Vector2d(-x, -y);
	}

	public static double dot(Vector2d v1, Vector2d v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}

	public static double cross(Vector2d v1, Vector2d v2) {
		return v1.getX() * v2.getY() - v2.getX() * v1.getY();
	}

	public static double distance(Vector2d v1, Vector2d v2) {
		double distanceSquared = (v2.x - v1.x) * (v2.x - v1.x) + (v2.y - v1.y) * (v2.y - v1.y);
		return Math.sqrt(distanceSquared);
	}

	public String toString() {
		return "Vector2d(" + x + ", " + y + ")";
	}
}