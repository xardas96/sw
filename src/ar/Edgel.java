package ar;

public class Edgel {
	private int x;
	private int y;
	private double[] direction; // znormalizowany wektor 2 elementowy (x i y);

	public Edgel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double[] getDirection() {
		return direction;
	}

	public void setDirection(double[] direction) {
		this.direction = direction;
	}

	public boolean isOrientationCompatible(Edgel edgel) {
		return direction[0] * edgel.direction[0] + direction[1] * edgel.direction[1] > 0.38f; // magic
	}

	public String toString() {
		return "Edgel (" + x + ", " + y + ")";
	}
}