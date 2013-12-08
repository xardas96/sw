package ar;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.PerspectiveTransform;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.WarpPerspective;

import Jama.Matrix;

public class PerspectiveFinder {
	private int width;
	private int height;
	
	public PerspectiveFinder(int width, int height){
		this.width = width;
		this.height = height;
	}
	
	public Matrix findPerspectiveMatrix(Marker marker){
		Vector2d c1 = marker.getCorner1();
		Vector2d c2 = marker.getCorner2();
		Vector2d c3 = marker.getCorner3();
		Vector2d c4 = marker.getCorner4();
		double[][] big = new double[][]{{c1.getX(), c1.getY(),1,0,0,0,0,0},
										{c2.getX(), c2.getY(),1,0,0,0,-c2.getX()*width, -c2.getY()*width},
										{c3.getX(), c3.getY(),1,0,0,0,-c3.getX()*width, -c3.getY()*width},
										{c4.getX(), c4.getY(),1,0,0,0,0,0},
										{0,0,0,c1.getX(), c1.getY(), 1, 0,0},
										{0,0,0,c2.getX(), c2.getY(),1,0,0},
										{0,0,0,c3.getX(), c3.getY(),1,-c3.getX()*height,-c3.getY()*height},
										{0,0,0,c4.getX(), c4.getY(),1,-c4.getX()*height,-c4.getY()*height}};
		Matrix bigM = new Matrix(big);
		Matrix small = new Matrix(new double[][]{{0},{width},{width},{0}, {0}, {0},{height}, {height}});
		//Matrix small = new Matrix(new double[][]{{0,0,width,width, 0, height, 0, height}});
		bigM = bigM.inverse();
		Matrix res = bigM.times(small);
		res = new Matrix(new double[][]{{res.get(0, 0), res.get(1, 0), res.get(2, 0)}, 
										{res.get(3,0), res.get(4, 0), res.get(5, 0)},
										{res.get(6, 0),res.get(7,0),1}});
		return res;
	}
	
	public BufferedImage transformBufferedImage(Matrix transform, BufferedImage image, Dimension markerDimension){
		transform = transform.inverse();
		PerspectiveTransform trans = new PerspectiveTransform(transform.getArray());
		WarpPerspective warp = new WarpPerspective(trans);
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(PlanarImage.wrapRenderedImage(image));
		pb.add(warp);
		pb.add(new InterpolationBilinear());
		RenderedOp op = JAI.create("warp", pb);
		PlanarImage pImage = op.createInstance();
		return new BufferedImage(pImage.getColorModel(), (WritableRaster) pImage.getData(new Rectangle(markerDimension)), false, null);
	}
}
