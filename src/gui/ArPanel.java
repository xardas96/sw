package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import ar.models.ArModel;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class ArPanel extends JPanel implements WebcamImageRenderer {
	private static final long serialVersionUID = -7726082525748549543L;
	private SimpleUniverse universe;
	private Canvas3D canvas3D;
	private Background background;
	private BranchGroup scene;
	private boolean sizeSet;
	private Matrix3d openCVCorrectionMatrix;

	private Map<Integer, LinkedList<ArModel>> models = new HashMap<>();
	private Map<Integer, LinkedList<TransformGroup>> transforms = new HashMap<>();
	private Map<Integer, LinkedList<BranchGroup>> innerContents = new HashMap<>();

	public ArPanel() {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		setLayout(new BorderLayout());
		add(canvas3D, BorderLayout.CENTER);
		createSceneGraph();
		openCVCorrectionMatrix = createOpenCVCorrectionMatrix();
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

	public void setModel(ArModel model, double[] translate, double[] rotate, double scale) {
		if (model != null) {
			if (models.containsKey(model.getMarkerCode())) {
				List<ArModel> list = models.get(model.getMarkerCode());
				boolean found = false;
				for (int i = 0; i < list.size() && !found; i++) {
					if (!list.get(i).isRendered()) {
						ArModel m = list.get(i);
						m.setRendered(true);
						Transform3D rot = createTransform(translate, rotate, scale);
						transforms.get(model.getMarkerCode()).get(i).setTransform(rot);
						found = true;
					}
				}
				if (!found) {
					model.setRendered(true);
					LinkedList<ArModel> modelList = models.get(model.getMarkerCode());
					LinkedList<TransformGroup> groupList = transforms.get(model.getMarkerCode());
					LinkedList<BranchGroup> innerList = innerContents.get(model.getMarkerCode());
					modelList.add(model);
					Transform3D rot = createTransform(translate, rotate, scale);
					TransformGroup tg = new TransformGroup();
					tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
					tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
					tg.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
					tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
					tg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
					tg.setTransform(rot);
					tg.addChild(model.getModel());
					groupList.add(tg);
					BranchGroup innerContent = new BranchGroup();
					innerContent.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
					innerContent.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
					innerContent.setCapability(BranchGroup.ALLOW_DETACH);
					innerList.add(innerContent);
					innerContent.addChild(tg);
					scene.addChild(innerContent);
				}
			} else {
				model.setRendered(true);
				LinkedList<ArModel> modelList = new LinkedList<>();
				LinkedList<TransformGroup> groupList = new LinkedList<>();
				LinkedList<BranchGroup> innerList = new LinkedList<>();
				modelList.add(model);
				Transform3D rot = createTransform(translate, rotate, scale);
				TransformGroup tg = new TransformGroup();
				tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
				tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
				tg.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
				tg.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
				tg.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
				tg.setTransform(rot);
				tg.addChild(model.getModel());
				groupList.add(tg);
				BranchGroup innerContent = new BranchGroup();
				innerContent.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
				innerContent.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
				innerContent.setCapability(BranchGroup.ALLOW_DETACH);
				innerList.add(innerContent);
				innerContent.addChild(tg);
				innerContents.put(model.getMarkerCode(), innerList);
				models.put(model.getMarkerCode(), modelList);
				transforms.put(model.getMarkerCode(), groupList);
				scene.addChild(innerContent);
			}
		}
	}

	public void clearModels() {
		Map<Integer, Integer> toRemove = new HashMap<>();
		for (Integer id : models.keySet()) {
			for (ArModel model : models.get(id)) {
				if (model.isRendered()) {
					model.setRendered(false);
				} else {
					Integer count = toRemove.get(id);
					count = (count == null ? 1 : count + 1);
					toRemove.put(id, count);
				}
			}
		}
		for (Integer id : toRemove.keySet()) {
			for (int i = 0; i < toRemove.get(id); i++) {
				LinkedList<ArModel> mods = models.get(id);
				LinkedList<TransformGroup> tgs = transforms.get(id);
				LinkedList<BranchGroup> bgs = innerContents.get(id);
				if (!mods.isEmpty()) {
					ArModel mod = mods.removeLast();
					mod.setRendered(false);
				}
				if (!tgs.isEmpty()) {
					TransformGroup trans = tgs.removeLast();
					trans.removeAllChildren();
				}
				if (!bgs.isEmpty()) {
					BranchGroup bg = bgs.removeLast();
					scene.removeChild(bg);
				}
			}
		}
	}

	private Transform3D createTransform(double[] translate, double[] rotate, double scale) {
		Transform3D rotateTransform = new Transform3D();
		Matrix3d rotationMatrix = new Matrix3d(rotate);
		rotationMatrix.mul(openCVCorrectionMatrix);
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
		BoundingSphere b = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 500.0);
		background.setApplicationBounds(b);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		Color3f lightColor = new Color3f(1f, 1f, 1f);
		Vector3f lightDirection = new Vector3f(0f, 0f, -1f);
		DirectionalLight light = new DirectionalLight(lightColor, lightDirection);
		light.setInfluencingBounds(b);
		AmbientLight ambient = new AmbientLight(new Color3f(1f, 1f, 1f));
		ambient.setInfluencingBounds(b);
		scene.addChild(light);
		scene.addChild(ambient);
		scene.addChild(background);
		scene.compile();
	}

	private Matrix3d createOpenCVCorrectionMatrix() {
		Matrix3d correction = new Matrix3d();
		Transform3D opencvCorrection = new Transform3D();
		opencvCorrection.rotX(-Math.PI / 2);
		opencvCorrection.get(correction);
		return correction;
	}
}