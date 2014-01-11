package ar.models;

import java.util.HashMap;

import javax.media.j3d.BranchGroup;

import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

public abstract class ModelLibrary {
	private static HashMap<Integer, Scene> modelMap;

	public static void init() throws Exception {
		modelMap = new HashMap<>();
		modelMap.put(1, loadScene("obj\\skyscraper.obj"));
		modelMap.put(4, loadScene("obj\\airboat.obj"));
	}

	public static ArModel getModel(int code) {
		ArModel output = null;
		if (modelMap.containsKey(code)) {
			BranchGroup model = modelMap.get(code).getSceneGroup();
			output = new ArModel(model);
			output.setMarkerCode(code);
		}
		return output;
	}

	private static Scene loadScene(String fileName) throws Exception {
		Scene scene = null;
		ObjectFile f = new ObjectFile();
		f.setFlags(Loader.LOAD_ALL);
		scene = f.load(fileName);
		scene.getSceneGroup().setCapability(BranchGroup.ALLOW_DETACH);
		return scene;
	}
}