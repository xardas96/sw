package ar.camera;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public abstract class CameraIntristics {
	private static double[] INTRISTICS_MATRIX;
	private static double[] DISTORTION;

	public static void loadInstisticsFromFile(String path) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(path));
		Node cameraMatrix = document.getRootElement().selectSingleNode("//Camera_Matrix");
		Node cameraData = cameraMatrix.selectSingleNode("data");
		String data = cameraData.getText().replace("\n", "");
		String[] split = data.split(" ");
		INTRISTICS_MATRIX = new double[9];
		int j = 0;
		for (int i = 0; i < split.length; i++) {
			if (!split[i].equals("")) {
				INTRISTICS_MATRIX[j] = Double.valueOf(split[i]);
				j++;
			}
		}
		Node distortion = document.getRootElement().selectSingleNode("//Distortion_Coefficients");
		Node distortionData = distortion.selectSingleNode("data");
		data = distortionData.getText().replace("\n", "");
		split = data.split(" ");
		DISTORTION = new double[5];
		j = 0;
		for (int i = 0; i < split.length; i++) {
			if (!split[i].equals("")) {
				DISTORTION[j] = Double.valueOf(split[i]);
				j++;
			}
		}
	}

	public static double[] getDistortion() {
		return DISTORTION;
	}

	public static double[] getIntristics() {
		return INTRISTICS_MATRIX;
	}
}