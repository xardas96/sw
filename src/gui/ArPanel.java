package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Matrix3d;
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

	public void setTranslationAndRotation(double[] translate, double[] rotate) {
		rotateTransform = createTransform(translate, rotate);
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

	private Transform3D createTransform(double[] translate, double[] rotate) {
		Transform3D rotateTransform = new Transform3D();
		rotateTransform.setRotation(new Matrix3d(rotate));
		Transform3D vector = new Transform3D();
		vector.set(new Vector3d(translate));
		rotateTransform.mul(vector);
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
		scene.addChild(background);
		scene.compile();
	}
}