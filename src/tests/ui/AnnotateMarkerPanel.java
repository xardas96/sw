package tests.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class AnnotateMarkerPanel extends JPanel {
	private static final long serialVersionUID = -3343419621024202163L;
	private File[] images;
	private int currentImage;
	private ImagePanel panel = new ImagePanel();
	private final JList<Point> list = new JList<>();
	private DefaultListModel<Point> listModel = new DefaultListModel<>();
	private final JLabel lblNewLabel = new JLabel("New label");

	public AnnotateMarkerPanel() {
		list.setModel(listModel);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] {0, 0, 0};
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0 };
		setLayout(gridBagLayout);

		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				panel.addPoint(event.getPoint());
				listModel.addElement(event.getPoint());
				panel.repaint();
			}
		});
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.weightx = 0.1;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.weighty = 0.1;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);

		JButton btnNewButton = new JButton("Magic");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (panel.getImage() != null) {
					File file = new File(panel.getImagePath());
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(file));
						for (int i = 0; i < listModel.size(); i++) {
							Point p = listModel.elementAt(i);
							bw.write(p.x + "," + p.y);
							bw.newLine();
						}
						bw.flush();
						bw.close();
						listModel.removeAllElements();
						currentImage++;
						lblNewLabel.setText(currentImage + "/" + images.length);
						panel.setImage(ImageIO.read(images[currentImage]));
						panel.setImagePath(images[currentImage].getAbsolutePath() + ".corners");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						lblNewLabel.setText(currentImage + "/" + images.length);
						panel.setImage(ImageIO.read(images[currentImage]));
						panel.setImagePath(images[currentImage].getAbsolutePath() + ".corners");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				panel.repaint();
			}
		});

		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.insets = new Insets(0, 0, 5, 0);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.weightx = 0.1;
		gbc_list.gridx = 1;
		gbc_list.gridy = 0;
		add(list, gbc_list);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(5, 5, 5, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 1;
		add(btnNewButton, gbc_btnNewButton);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		add(lblNewLabel, gbc_lblNewLabel);
	}

	public void setImage(BufferedImage image) {
		panel.setImage(image);
	}

	public void setImages(File[] images) {
		this.images = images;
	}

}
