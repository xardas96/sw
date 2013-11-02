package ar.dividing;

public class Pixel {
	private int x;
	private int y;
	private int r;
	private int g;
	private int b;
	
	public Pixel(int x, int y, int r, int g, int b) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getR() {
		return r;
	}
	
	public int getG() {
		return g;
	}
	
	public int getB() {
		return b;
	}
}