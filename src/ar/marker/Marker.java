package ar.marker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.LineSegment;
import ar.utils.Vector2d;


public class Marker {
	private List<LineSegment> chain;
	private Vector2d corner1;
	private Vector2d corner2;
	private Vector2d corner3;
	private Vector2d corner4;

	public Marker() {

	}

	public Marker(Vector2d c1, Vector2d c2, Vector2d c3, Vector2d c4) {
		corner1 = c1;
		corner2 = c2;
		corner3 = c3;
		corner4 = c4;
	}

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
	
	public void setCorner1(Vector2d c) {
		corner1 =c;
	}

	public void setCorner2(Vector2d c) {
		corner2=  c;
	}

	public void setCorner3(Vector2d c) {
		corner3 = c;
	}

	public void  setCorner4(Vector2d c) {
		corner4 = c;
	}
	

	/**
	 * Checks if m2 is inside m1
	 */
	public static boolean isInside(Marker m1, Marker m2) {
		double maxX = Marker.getMaxX(m1);
		double minX = Marker.getMinX(m1);
		double maxY = Marker.getMaxY(m1);
		double minY = Marker.getMinY(m1);
		boolean result = m2.corner1.getX() < maxX && m2.corner1.getX() > minX && m2.corner1.getY() < maxY && m2.corner1.getY() > minY;
		result &= m2.corner2.getX() < maxX && m2.corner2.getX() > minX && m2.corner2.getY() < maxY && m2.corner2.getY() > minY;
		result &= m2.corner3.getX() < maxX && m2.corner3.getX() > minX && m2.corner3.getY() < maxY && m2.corner3.getY() > minY;
		result &= m2.corner4.getX() < maxX && m2.corner4.getX() > minX && m2.corner4.getY() < maxY && m2.corner4.getY() > minY;
		return result;
	}
	
	public static double getMaxX(Marker m){
		return Math.max(m.corner1.getX(), Math.max(m.corner2.getX(), Math.max(m.corner3.getX(),m.corner4.getX())));
	}
	
	public static double getMinX(Marker m){
		return Math.min(m.corner1.getX(), Math.min(m.corner2.getX(), Math.min(m.corner3.getX(),m.corner4.getX())));
	}
	
	public static double getMaxY(Marker m){
		return Math.max(m.corner1.getY(), Math.max(m.corner2.getY(), Math.max(m.corner3.getY(),m.corner4.getY())));
	}
	
	public static double getMinY(Marker m){
		return Math.min(m.corner1.getY(), Math.min(m.corner2.getY(), Math.min(m.corner3.getY(),m.corner4.getY())));
	}
}