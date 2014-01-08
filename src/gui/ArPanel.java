package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class ArPanel extends JPanel implements WebcamImageRenderer {
	private static final long serialVersionUID = -7726082525748549543L;
	private SimpleUniverse universe;
	private Canvas3D canvas3D;
	private Background background;
	private BranchGroup scene;
	private BranchGroup content;
	private BranchGroup model;
	private Transform3D rotateTransform;
	private TransformGroup tg;
	private boolean sizeSet;
	private Matrix3d opencvCorrection;

	public ArPanel() {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		setLayout(new BorderLayout());
		add(canvas3D, BorderLayout.CENTER);
		content = new BranchGroup();
		content.setCapability(BranchGroup.ALLOW_DETACH);
		tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		createSceneGraph();
		opencvCorrection = createOpenCVCorrectionMatrix();
		universe = new SimpleUniverse(canvas3D);
		universe.addBranchGraph(scene);
	}

	@Override
	public void setImage(Image image) {
		BufferedImage buff = (BufferedImage) image;
		background.setImage(new ImageComponent2D(BufferedImage.TYPE_INT_ARGB, buff));
		if (!sizeSet) {
			canvas3D.setSize(new Dimension(buff.getWidth(), buff.getHeight()));
			sizeSet = true;
		}
	}

	public void setTranslationAndRotation(double[] translate, double[] rotate, double scale) {
		rotateTransform = createTransform(translate, rotate, scale);
		tg.setTransform(rotateTransform);
	}

	public void setModel(BranchGroup model) {
		if (model != null && !model.equals(this.model)) {
			this.model = model;
			this.model.setCapability(BranchGroup.ALLOW_DETACH);
			content.removeChild(tg);
			tg.removeAllChildren();
			tg.addChild(model);
			content.addChild(tg);
			scene.addChild(content);
		}
	}

	public void removeModel() {
		if (model != null) {
			scene.removeChild(content);
			tg.removeChild(model);
			model = null;
		}
	}

	private Transform3D createTransform(double[] translate, double[] rotate, double scale) {
		Transform3D rotateTransform = new Transform3D();
		Matrix3d rotationMatrix = new Matrix3d(rotate);
//		rotationMatrix.mul(opencvCorrection);
//		rotationMatrix.invert();
		Vector3d translationVector = new Vector3d(translate);
		Matrix4d mat = new Matrix4d(rotationMatrix, translationVector, scale);
		rotateTransform.set(mat);
		return rotateTransform;
	}

	private void createSceneGraph() {
		scene = new BranchGroup();
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		scene.setCapability(BranchGroup.ALLOW_DETACH);
		background = new Background();
		BoundingSphere b = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 320.0);
		background.setApplicationBounds(b);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		Color3f ambientColor = new Color3f(0f, 1.0f, 0f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(b);
		scene.addChild(ambientLightNode);
		scene.addChild(background);
		scene.compile();
	}
	
	private Matrix3d createOpenCVCorrectionMatrix() {
		Matrix3d correction = new Matrix3d();
		Transform3D opencvCorrection = new Transform3D();
		opencvCorrection.rotX(Math.PI/2);
		opencvCorrection.get(correction);
		return correction;
	}
}