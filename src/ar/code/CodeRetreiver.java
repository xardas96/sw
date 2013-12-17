package ar.code;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ar.MarkerFinder;
import ar.Vector2d;
import ar.image.ImageOperations;

public class CodeRetreiver {
	private static final double scalar = (MarkerFinder.MARKER_DIMENSION.width/500.0);
	private int startPoint = (int) (100*scalar)-3;
	private int bitSize = (int) (100*scalar)-3;
	private int codeGridSize = 3;
	private boolean debug = true;
	private double blacknessTreshold = 100;
	
	public CodeRetreiver(){
	}
	
	public int[] retreiveCode(BufferedImage image){
		int[] code = new int[codeGridSize*codeGridSize];
		for(int x = 0; x < codeGridSize; x++){
			for(int y = 0; y < codeGridSize; y++){
				Vector2d point = new Vector2d(startPoint+x*bitSize+bitSize/2, startPoint+y*bitSize+bitSize/2);
				double blackness = ImageOperations.calculateBlackness(point, image);
				if(blackness < blacknessTreshold)
					code[y*codeGridSize + x] = 0;
				else
					code[y*codeGridSize + x] = 1;
				if(debug){
					Graphics2D g = (Graphics2D) image.getGraphics(); 
					g.setColor(Color.BLUE);
					g.fillOval((int) point.getX()-2,(int) point.getY()-2, 4, 4);
				}
			}
		}
		return code;
	}
}
