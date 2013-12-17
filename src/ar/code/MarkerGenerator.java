package ar.code;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class MarkerGenerator {
	private int size = 500;
	private int bitSize = 100;
	private int borderThickness = 40;
	private int whiteBorderThickness = 10;
	private int codeGridSize = 3;
	private int orientationTriangleHeight = 60;
	
	public MarkerGenerator(){
		
	}
	
	public MarkerGenerator(int size, int bitSize, int inMarkerSize, int borderThickness, int whiteBorder, int codeGrid, int orHeight){
		this.size = size; 
		this.bitSize = bitSize;
		this.borderThickness = borderThickness;
		this.whiteBorderThickness =whiteBorder;
		codeGridSize = codeGrid;
		orientationTriangleHeight = orHeight;
	}
	
	public BufferedImage makeMarker(int code){
		BufferedImage image = new BufferedImage(size +2*(borderThickness+whiteBorderThickness), size +2*(borderThickness+whiteBorderThickness), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		drawMarker(g);
		drawOrientationSign(g);
		drawCode(g, encryptCode(code));
		return image;
	}
	

	private void drawMarker(Graphics2D g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size +2*(borderThickness+whiteBorderThickness), size +2*(borderThickness+whiteBorderThickness));
		g.setColor(Color.BLACK);
		g.fillRect(whiteBorderThickness, whiteBorderThickness, size+2*(borderThickness), size+2*(borderThickness));
		g.setColor(Color.WHITE);
		g.fillRect(borderThickness+whiteBorderThickness, borderThickness+whiteBorderThickness, size, size);
	}
	
	private void drawOrientationSign(Graphics2D g){
		int width =(int) (2*orientationTriangleHeight/Math.sqrt(2));
		int sumSize = size +2*(borderThickness+whiteBorderThickness);
		int x = sumSize -borderThickness-whiteBorderThickness;
		g.setColor(Color.BLACK);
		g.fillPolygon(new int[]{x, x-width, x}, new int[]{x, x, x-width}, 3);
	}
	
	private void drawCode(Graphics2D g, int[] code){
		int gridStart = borderThickness+whiteBorderThickness+orientationTriangleHeight;
		for(int x = 0; x < codeGridSize; x++){
			for(int y = 0;  y < codeGridSize; y++){
				if(code[y*codeGridSize+x]==1)
					g.setColor(Color.WHITE);
				else
					g.setColor(Color.BLACK);
				g.fillRect(gridStart+x*bitSize, gridStart+y*bitSize, bitSize, bitSize);
			}
		}
	}
	
	private int[] encryptCode(int code) {
		int[] bits = new int[codeGridSize*codeGridSize];
		for(int i = 0; i < bits.length; i++){
			bits[bits.length-1-i] = code&1;
			code = code >> 1;
		}
		return bits;
	}
}
