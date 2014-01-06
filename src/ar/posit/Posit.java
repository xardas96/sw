package ar.posit;

import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_core.CV_TERMCRIT_EPS;
import static com.googlecode.javacv.cpp.opencv_core.CV_TERMCRIT_ITER;
import static com.googlecode.javacv.cpp.opencv_core.cvTermCriteria;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

import ar.MarkerFinder;
import ar.marker.Marker;

import com.googlecode.javacv.cpp.opencv_calib3d.CvPOSITObject;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvPoint3D32f;
import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;

public class Posit {
	    private final Point3f[] points = {
	    			new Point3f(0, 0, 0),
	    			new Point3f(0, 0, MarkerFinder.MARKER_DIMENSION.width),
	    			new Point3f(MarkerFinder.MARKER_DIMENSION.width, 0, 0),
	    			new Point3f(0, MarkerFinder.MARKER_DIMENSION.width, 0)
	    		};
	    private CvPoint3D32f targetPoints;
	    private int nPoints;
	    private CvPOSITObject positObj;
	    private double focalLength;
	    private CvTermCriteria criteria;
	    private float[] rotationMatrix = new float[9];
	    private float[] translationVector = new float[3];
	    private CvPoint2D32f cvImagePoints;
	    private int nImagePoints;

	    public Posit(double focalLength) {
	    	this.focalLength = focalLength;
	        nPoints = points.length;
	        targetPoints = new CvPoint3D32f(nPoints);
	        for (int i = 0; i < nPoints; i++) {
	            targetPoints.position(i).put(points[i].x, points[i].y, points[i].z);
	        }
	        nImagePoints = nPoints;
	        cvImagePoints = new CvPoint2D32f(nImagePoints);
	        criteria = cvTermCriteria(CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 100, 1.0e-5f);
	    }
	    
	    public void calculatePosit(Marker marker) {
	    	Point2f[] imagePoints = {new Point2f((float)marker.getCorner1().getX(), (float)marker.getCorner1().getY()),
	    			new Point2f((float)marker.getCorner2().getX(), (float)marker.getCorner2().getY()),
	    			new Point2f((float)marker.getCorner3().getX(), (float)marker.getCorner3().getY()),
	    			new Point2f((float)marker.getCorner4().getX(), (float)marker.getCorner4().getY())};
	    	calculatePosit(imagePoints);
	    }
	    
	public void calculatePosit(Point2f[] imagePoints) {
        for (int i = 0; i < nImagePoints; i++) {
            double x = imagePoints[i].x;
            double y = imagePoints[i].y;
            cvImagePoints.position(i).put(x, y);
        }
        positObj = CvPOSITObject.create(targetPoints.position(0), nPoints);
        cvPOSIT(positObj, cvImagePoints.position(0), focalLength, criteria, rotationMatrix, translationVector);
	}
	
	public float[] getTranslationVector() {
		return translationVector;
	}
	
	public float[] getRotationMatrix() {
		return rotationMatrix;
	}
}