package ar.models;

import java.util.HashMap;

import javax.media.j3d.BranchGroup;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

public abstract class ModelLibrary {
	private static HashMap<Integer, Scene> modelMap;

	public static void init() throws Exception {
		modelMap = new HashMap<>();
		modelMap.put(1, loadScene("obj\\airtable.obj"));
	}

	public static BranchGroup getModel(int code) {
		BranchGroup output = null;
		if (modelMap.containsKey(code)) {
			output = modelMap.get(code).getSceneGroup();
		}
		return output;
	}

	private static Scene loadScene(String fileName) throws Exception {
		Scene scene = null;
		ObjectFile f = new ObjectFile();
		f.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
		scene = f.load(fileName);
		return scene;
	}
}