package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class ArPanel extends JPanel implements WebcamImageRenderer {
	private static final long serialVersionUID = -7726082525748549543L;
	private SimpleUniverse universe;
	private Canvas3D canvas3D;
	private BranchGroup scene;
	private Background background;

	public ArPanel() {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		setLayout(new BorderLayout());
		add(canvas3D, BorderLayout.CENTER);
		scene = createSceneGraph();
		universe = new SimpleUniverse(canvas3D);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(scene);
	}

	@Override
	public void setImage(Image image) {
		background.setImage(new ImageComponent2D(BufferedImage.TYPE_INT_ARGB, (BufferedImage) image));
		canvas3D.setSize(new Dimension(image.getWidth(null), image.getHeight(null)));
	}

	// TODO
	private Transform3D createTransform() {
		Transform3D rotate = new Transform3D();
		Transform3D tempRotate = new Transform3D();
		Transform3D scale = new Transform3D();
		Transform3D vector = new Transform3D();
		rotate.rotX(Math.PI / 4.0);
		tempRotate.rotY(Math.PI / 20.0);
		scale.setScale(0.5d);
		vector.set(new Vector3d(0.5, 0.5, 0.5));
		rotate.mul(tempRotate);
		rotate.mul(scale);
		rotate.mul(vector);
		return rotate;
	}
	
	//TODO
	private Scene loadObjScene(String code) {
		Scene scene = null;
		ObjectFile f = new ObjectFile();
		f.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
		try {
			scene = f.load(code);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return scene;
	}

	public BranchGroup createSceneGraph() {
		BranchGroup objRoot = new BranchGroup();
		Transform3D rotate = createTransform();
		TransformGroup objRotate = new TransformGroup(rotate);
		objRoot.addChild(objRotate);
		Scene loadedScene = loadObjScene("airtable.obj"); //TODO
		objRotate.addChild(loadedScene.getSceneGroup());
		background = new Background();
		BoundingSphere b = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 50.0);
		background.setApplicationBounds(b);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		objRoot.addChild(background);
		objRoot.compile();
		return objRoot;
	}
}