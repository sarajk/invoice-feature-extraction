package lb.edu.aust.cce577.ife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class FeatureComponent extends JPanel {

	private final JLabel keyBoundingBoxLabel;
	private final JTextField keyBoundingBoxTextField;
	private final JList<String> valuesList;
	private final JScrollPane listScroller;
	private final JButton resetButton;
	private Feature feature;
	private boolean focused;
	private JRadioButton radioButton;
	private FeatureChangeListener featureChangeListener;

	public FeatureComponent(Feature feature) {

		this.feature = feature;
		this.setBorder(BorderFactory.createTitledBorder(feature.getKey()));

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//<editor-fold desc="Key Panel">
		JPanel keyContentPanel = new JPanel();
		keyContentPanel.setLayout(new BoxLayout(keyContentPanel, BoxLayout.Y_AXIS));

		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new FlowLayout());

		radioButton = new JRadioButton();
		radioButton.setText("Active");
		radioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (featureChangeListener != null) {
					featureChangeListener.onActivated(FeatureComponent.this);
				}
			}
		});
		radioPanel.add(radioButton);

		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final int confirmation = JOptionPane.showConfirmDialog(FeatureComponent.this.getParent(),
						"Are you sure you want to reset everything linked to feature " + feature.getKey() + "?",
						"Reset " + feature.getKey(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (confirmation == JOptionPane.YES_OPTION) {

					final Feature.BoundingBox boundingBox = feature.getBoundingBox();
					if (featureChangeListener != null) {
						featureChangeListener.onBoundingBoxDeleted(FeatureComponent.this, boundingBox);
						for (Feature.BoundingBox valueBoundingBox : feature.getValueBoundingBoxes()) {
							featureChangeListener.onBoundingBoxDeleted(FeatureComponent.this, valueBoundingBox);
						}

					}

					feature.setBoundingBox(null);
					feature.setValueBoundingBoxes(Collections.emptyList());

					refresh();
				}
			}
		});

		radioPanel.add(resetButton);

		JPanel keyPanel = new JPanel();
		keyPanel.setLayout(new FlowLayout());

		keyBoundingBoxLabel = new JLabel("Key Bounding Box");
		keyBoundingBoxTextField = new JTextField(20);
		keyBoundingBoxTextField.setEditable(false);

		keyPanel.add(keyBoundingBoxLabel);
		keyPanel.add(keyBoundingBoxTextField);

		keyContentPanel.add(radioPanel);
		keyContentPanel.add(keyPanel);

		this.add(keyContentPanel);
		//</editor-fold>


		JPanel valuePanel = new JPanel();
		valuePanel.setLayout(new FlowLayout());

		if(feature.isHasMany()) {
			valuePanel.setBorder(BorderFactory.createTitledBorder("Values"));
		} else {
			valuePanel.setBorder(BorderFactory.createTitledBorder("Value"));
		}


		JPopupMenu listMenu = new JPopupMenu();
		final JMenuItem deleteMenuItem = new JMenuItem("Delete", 1);
		listMenu.add(deleteMenuItem);
		deleteMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final int selectedIndex = valuesList.getSelectedIndex();

				if (selectedIndex < 0) {
					return;
				}

				List<Feature.BoundingBox> valueBoundingBoxes = feature.getValueBoundingBoxes();
				for (int i = 0, valueBoundingBoxesSize = valueBoundingBoxes.size(); i < valueBoundingBoxesSize; i++) {
					Feature.BoundingBox valueBoundingBox = valueBoundingBoxes.get(i);

					if (i == selectedIndex) {
						feature.removeValueBoundingBox(valueBoundingBox.getId());

						if (featureChangeListener != null) {
							featureChangeListener.onBoundingBoxDeleted(FeatureComponent.this, valueBoundingBox);
						}

						break;
					}
				}
				refresh();
			}
		});

		valuesList = new JList<>();
		valuesList.setLayoutOrientation(JList.VERTICAL);
		valuesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		valuesList.setComponentPopupMenu(listMenu);

		int height = 21;
		if (feature.isHasMany()) {
			height = 80;
			valuesList.setVisibleRowCount(5);
		} else {
			valuesList.setVisibleRowCount(1);
		}
		listScroller = new JScrollPane(valuesList);
		listScroller.setPreferredSize(new Dimension(300, height));
		valuePanel.add(listScroller);

		this.add(valuePanel);

		System.out.println(feature.getKey());
		setFocused(focused);
		refresh();
	}

	public void setFeatureChangeListener(FeatureChangeListener featureChangeListener) {

		this.featureChangeListener = featureChangeListener;
	}

	public Feature getFeature() {

		return feature;
	}

	public void setFocused(boolean focused) {

		this.keyBoundingBoxTextField.setEnabled(focused);
		this.radioButton.setSelected(focused);
		this.listScroller.setEnabled(focused);
		this.valuesList.setEnabled(focused);
		this.resetButton.setEnabled(focused);
		this.focused = focused;
	}

	public boolean isFocused() {

		return focused;
	}

	public void refresh() {

		final Feature.BoundingBox boundingBox = feature.getBoundingBox();
		if (boundingBox != null) {
			keyBoundingBoxTextField.setText(boundingBox.toString());
		} else {
			keyBoundingBoxTextField.setText(null);
		}
		valuesList.setListData(feature.getValueBoundingBoxes().stream().map(Feature.BoundingBox::toString).toArray(String[]::new));


		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				JScrollBar vertical = listScroller.getVerticalScrollBar();
				vertical.setValue(vertical.getMaximum());
			}
		});

	}
}
