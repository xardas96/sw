package ar.orientation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ar.Marker;
import ar.MarkerFinder;
import ar.Vector2d;
import ar.image.ImageOperations;

public class CornerBasedOrientationFinder extends OrientationFinder{
	private boolean debug = false;
	private double blacknessTreshold = 100;
	private double blackCornerDistance = 20;
	
	public CornerBasedOrientationFinder(){
		
	}
	
	public CornerBasedOrientationFinder(double blacknessTreshold,double blackCornerDistance, boolean debug){
		this.blacknessTreshold = blacknessTreshold;
		this.blackCornerDistance =blackCornerDistance;
		this.debug = debug;
	}

	@Override
	public List<Marker> setMarkerOrinetation(List<Marker> markers, BufferedImage image) {
		List<Marker> result = new ArrayList<Marker>();
		Graphics2D g = null;
		if(debug){
			g = (Graphics2D) image.getGraphics();
			g.setColor(Color.GREEN);
		}
		for(int i = markers.size()-1; i >= 0; i--){
			Marker m = markers.get(i);
			Vector2d center = m.getCorner1().add(m.getCorner3()).add(m.getCorner2()).add(m.getCorner4());
			center.divide(4);
			Vector2d[] cornerArray = new Vector2d[]{m.getCorner1(), m.getCorner2(), m.getCorner3(), m.getCorner4()};
			Vector2d[] debugPoints = null;
			if(debug)
				debugPoints = new Vector2d[cornerArray.length];
			double[] blackness = getCornerBlackness(cornerArray, center, image, debugPoints);
			if(debug){
				sortByBlackness(debugPoints, blackness);
				Vector2d p = debugPoints[0];
				g.fillOval((int) p.getX()-2,(int) p.getY()-2, 4, 4);
			}
			blackness = sortByBlackness(cornerArray, blackness);
			if(meetsConditions(blackness)){
				m.setCorner3(cornerArray[0]);
				setOtherCornersBasedOnThrid(m, center);
				result.add(m);
			}
		}
		return result;
	}
	
	private double[] getCornerBlackness(Vector2d[] cornerArray, Vector2d center, BufferedImage image, Vector2d[] debugPoints){
		double[] blackness = new double[cornerArray.length];
		for(int j = 0; j < cornerArray.length; j++){
			Vector2d c = cornerArray[j];
			Vector2d c_center = center.subtract(c);
			double d = blackCornerDistance;
			d*= c_center.getLength()/(MarkerFinder.MARKER_DIMENSION.width/2*Math.sqrt(2));
			c_center.normalize();
			c_center.multiply(d);
			Vector2d p = c.add(c_center);
			blackness[j] = ImageOperations.calculateBlackness(p, image); 
			if(debugPoints != null)
				debugPoints[j] = p;
		}
		return blackness;
	}
	
	private boolean meetsConditions(double[] blackness){
		boolean b = blackness[0] <= blacknessTreshold;
		for(int i = 1; i < blackness.length && b; i++)
			b &= blackness[i] > blacknessTreshold;
		return b;
	}

}
