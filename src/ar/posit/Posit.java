package ar.posit;

import static com.googlecode.javacv.cpp.opencv_calib3d.ITERATIVE;
import static com.googlecode.javacv.cpp.opencv_calib3d.cvRodrigues2;
import static com.googlecode.javacv.cpp.opencv_calib3d.solvePnP;

import java.awt.Dimension;

import javax.vecmath.Point2d;

import ar.MarkerFinder;
import ar.camera.CameraIntristics;
import ar.marker.Marker;

import com.googlecode.javacv.cpp.opencv_core.CvMat;

public class Posit {
	private double[] translate;
	private double[] rotate;
	private CvMat markerPoints;
	private CvMat imagePoints;
	private CvMat cameraIntristics;
	private CvMat distortion;
	private Dimension cameraDimension;

	public Posit(Dimension cameraDimension) {
		this.cameraDimension = cameraDimension;
		markerPoints = CvMat.create(4, 3);
		markerPoints.put(0, 0, 0).put(0, 1, 0).put(0, 2, 0);
		markerPoints.put(1, 0, MarkerFinder.MARKER_DIMENSION.width).put(1, 1, 0).put(1, 2, 0);
		markerPoints.put(2, 0, MarkerFinder.MARKER_DIMENSION.width).put(2, 1, MarkerFinder.MARKER_DIMENSION.height).put(2, 2, 0);
		markerPoints.put(3, 0, 0).put(3, 1, MarkerFinder.MARKER_DIMENSION.height).put(3, 2, 0);
		imagePoints = CvMat.create(4, 2);
		cameraIntristics = CvMat.create(3, 3);
		cameraIntristics.put(CameraIntristics.getIntristics());
		distortion = CvMat.create(1, 5);
		distortion.put(CameraIntristics.getDistortion());
	}

	public void calculatePosit(Marker marker) {
		Point2d[] imagePoints = { 
					new Point2d(marker.getCorner1().getX(), marker.getCorner1().getY()),
					new Point2d(marker.getCorner2().getX(), marker.getCorner2().getY()),
					new Point2d(marker.getCorner3().getX(), marker.getCorner3().getY()),
					new Point2d(marker.getCorner4().getX(), marker.getCorner4().getY())
				};
		calculatePosit(imagePoints);
	}

	public void calculatePosit(Point2d[] imagePoints) {
		CvMat rVec = CvMat.create(1, 9);
		CvMat tVec = CvMat.create(1, 3);
		this.imagePoints.put(0, 0, imagePoints[0].x).put(0, 1, imagePoints[0].y);
		this.imagePoints.put(1, 0, imagePoints[1].x).put(1, 1, imagePoints[1].y);
		this.imagePoints.put(2, 0, imagePoints[2].x).put(2, 1, imagePoints[2].y);
		this.imagePoints.put(3, 0, imagePoints[3].x).put(3, 1, imagePoints[3].y);
		solvePnP(markerPoints, this.imagePoints, cameraIntristics, distortion, rVec, tVec, false, ITERATIVE);
		CvMat rMat = CvMat.create(3, 3);
		cvRodrigues2(rVec, rMat, null);
		translate = tVec.get();
		rotate = rMat.get();
	}
	
	public double[] getTranslate() {
		translate[0] = translate[0] / cameraDimension.getWidth();
		translate[1] = -(translate[1] / cameraDimension.getHeight());
		translate[2] = -(translate[2] / 100);
		return translate;
	}
	
	public double[] getRotate() {
		return rotate;
	}
}