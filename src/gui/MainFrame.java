package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.media.j3d.BranchGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.sun.j3d.loaders.Scene;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -8026416994513756565L;
	private WebcamImageRenderer panel;
	private JLabel fpsLabel;
	private int fpsCounter;
	private boolean ar;

	public MainFrame(Image image, String title, boolean ar) {
		this.ar = ar;
		if (ar) {
			panel = new ArPanel();
		} else {
			panel = new DrawPanel(image);
		}
		fpsLabel = new JLabel();
		JScrollPane pane = new JScrollPane((Component) panel);
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		add(fpsLabel, BorderLayout.SOUTH);
		setSize(new Dimension(350, 350));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(title);
	}

	public void setTransform(float[] translate) {
		if (ar) {
			// ((ArPanel) panel).setTrans(translate);
			// ((ArPanel) panel).createSceneGraph();
		}
	}

	public void setModel(BranchGroup model) {
		if (ar) {
			((ArPanel) panel).setModel(model);
		}
	}

	public void removeModel() {
		if (ar) {
			((ArPanel) panel).removeModel();
		}
	}

	public void setImage(Image image) {
		fpsCounter++;
		panel.setImage(image);
		((Component) panel).repaint();
	}

	public void setFPS() {
		fpsLabel.setText(fpsCounter + " FPS");
		fpsCounter = 0;
	}

	public void setFPS(int value) {
		fpsLabel.setText("Decoded: " + value);
	}
}