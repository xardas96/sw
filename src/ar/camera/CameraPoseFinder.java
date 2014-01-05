package ar.camera;

import Jama.Matrix;

public class CameraPoseFinder {

	public Matrix findCameraPose(Matrix perspectiveTransform) {
		Matrix pose = createIdentityMatrix(4, 4); //TODO 3x4?
		double norm1 = columnNorm(getColumn(perspectiveTransform, 0));
		double norm2 = columnNorm(getColumn(perspectiveTransform, 1));
		double tnorm = (norm1 + norm2) / 2.0;
		double[] perspectiveColumn = getColumn(perspectiveTransform, 0);
		setColumn(pose, normalize(perspectiveColumn), 0);
		perspectiveColumn = getColumn(perspectiveTransform, 1);
		setColumn(pose, normalize(perspectiveColumn), 1);
		double[] poseColumn0 = getColumn(pose, 0);
		double[] poseColumn1 = getColumn(pose, 1);
		double[] poseColumn2 = crossProduct3(poseColumn0, poseColumn1);
		setColumn(pose, poseColumn2, 2);
		double[] poseColumn3 = div(getColumn(perspectiveTransform, 2), tnorm);
		setColumn(pose, poseColumn3, 3);
		return pose;
	}
	
	private double[] div(double[] vector, double factor) {
		double[] result = new double[vector.length];
		for(int i = 0; i<result.length; i++) {
			result[i] = vector[i] / factor;
		}
		return result;
	}
	
	private double[] crossProduct3(double[] vector1, double[] vector2) {
		double[] result = new double[3];
		result[0] = vector1[1] * vector2[2] - vector2[1] * vector1[2];
		result[1] = vector1[2] * vector2[0] - vector2[2] * vector1[0];
		result[2] = vector1[0] * vector2[1] - vector2[0] * vector1[1];
		return result;
	}
	
	/**
	 * cv::normalize - L2, alpha = 1
	 * @param column1
	 * @return
	 */
	private double[] normalize(double[] vector) {
		double[] normalized = new double[vector.length];
		double length = 0;
		for(int i = 0; i< vector.length; i++) {
			length += vector[i] * vector[i];
		}
		length = Math.sqrt(length);
		double invLength = 1.0 / length;
		for(int i = 0; i<vector.length; i++) {
			normalized[i] = vector[i] * invLength;
		}
		return normalized;
	}

	/**
	 * cv::norm - L2
	 */
	private double columnNorm(double[] column) {
		double sum = 0;
		for (int i = 0; i < column.length; i++) {
			sum += column[i] * column[i];
		}
		return Math.sqrt(sum);
	}

	/**
	 * cv::eye
	 */
	private Matrix createIdentityMatrix(int rows, int cols) {
		double[][] matrix = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = (i == j) ? 1 : 0;
			}
		}
		return new Matrix(matrix);
	}

	private void setColumn(Matrix matrix, double[] column, int columnIndex) {
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			for (int j = 0; j < matrix.getColumnDimension(); j++) {
				if (j == columnIndex) {
					matrix.set(i, j, column[i]);
				}
			}
		}
	}

	private double[] getColumn(Matrix matrix, int columnIndex) {
		double[] column = new double[matrix.getRowDimension()];
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			for (int j = 0; j < matrix.getColumnDimension(); j++) {
				if (j == columnIndex) {
					column[i] = matrix.get(i, j);
				}
			}
		}
		return column;
	}
}