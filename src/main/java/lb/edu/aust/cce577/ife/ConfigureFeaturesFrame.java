package lb.edu.aust.cce577.ife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class ConfigureFeaturesFrame extends JFrame {

	private final JPanel mainPanel;
	private final JPanel footerPanel;
	private final JPanel featuresPanel;
	private final JPanel actionsPanel;
	private final JButton addButton;
	private final JButton cancelButton;
	private final JButton saveButton;
	private final JScrollPane featuresScrollPane;
	private ConfigureFeaturesInteractionListener interactionListener;

	private List<FeatureDefinition> definitionList;

	public ConfigureFeaturesFrame(List<FeatureDefinition> definitionList) {

		super("Configure Features");
		this.definitionList = new ArrayList<>(definitionList);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setAlwaysOnTop(true);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());

		this.featuresPanel = new JPanel();
		this.featuresPanel.setLayout(new BoxLayout(this.featuresPanel, BoxLayout.Y_AXIS));
		this.featuresScrollPane = new JScrollPane(this.featuresPanel);
		this.featuresScrollPane.setPreferredSize(new Dimension(700, 400));
		this.featuresScrollPane.getVerticalScrollBar().setUnitIncrement(16);

		this.actionsPanel = new JPanel();
		this.actionsPanel.setLayout(new FlowLayout());

		this.addButton = new JButton("Add");
		this.addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final FeatureDefinition featureDefinition = new FeatureDefinition();
				featureDefinition.setId(ConfigureFeaturesFrame.this.definitionList.size() + 1);
				ConfigureFeaturesFrame.this.definitionList.add(featureDefinition);

				loadFeatures(ConfigureFeaturesFrame.this.definitionList);

				SwingUtilities.invokeLater(() -> {

					final JScrollBar verticalScrollBar = featuresScrollPane.getVerticalScrollBar();
					verticalScrollBar.setValue(verticalScrollBar.getMaximum());
				});
			}
		});
		this.actionsPanel.add(this.addButton);

		this.mainPanel.add(this.featuresScrollPane, BorderLayout.CENTER);
		this.mainPanel.add(this.actionsPanel, BorderLayout.SOUTH);


		this.footerPanel = new JPanel();
		this.footerPanel.setLayout(new FlowLayout());

		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (ConfigureFeaturesFrame.this.definitionList.size() != definitionList.size()) {
					final int confirm = JOptionPane.showConfirmDialog(ConfigureFeaturesFrame.this, "Are you sure you want to cancel? There are some changes that were made.", "Cancel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (confirm == JOptionPane.YES_OPTION) {
						dispose();
					}
				} else {
					dispose();
				}
			}
		});

		this.saveButton = new JButton("Save");
		this.saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (interactionListener != null) {
					interactionListener.onSave(ConfigureFeaturesFrame.this.definitionList);
					dispose();
				}
			}
		});

		this.footerPanel.add(this.cancelButton);
		this.footerPanel.add(this.saveButton);


		this.add(mainPanel, BorderLayout.CENTER);
		this.add(footerPanel, BorderLayout.SOUTH);


		loadFeatures(this.definitionList);

		pack();
	}

	private void loadFeatures(List<FeatureDefinition> definitionList) {

		this.featuresPanel.removeAll();

		Collections.sort(definitionList);

		for (FeatureDefinition featureDefinition : definitionList) {

			final JPanel featureDefinitionPanel = new JPanel();
			featureDefinitionPanel.setLayout(new FlowLayout());

			//<editor-fold desc="ID">
			JLabel idLabel = new JLabel("ID");
			JTextField idTextField = new JTextField(String.valueOf(featureDefinition.getId()), 10);
			idTextField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {

					super.keyReleased(e);
					try {
						featureDefinition.setId(Integer.parseInt(idTextField.getText()));
					} catch (Exception ex) {

						JOptionPane.showMessageDialog(ConfigureFeaturesFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			idLabel.setLabelFor(idTextField);
			//</editor-fold>
			//<editor-fold desc="Key">
			JLabel keyLabel = new JLabel("Key");
			JTextField keyTextField = new JTextField(featureDefinition.getKey(), 20);
			keyTextField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {

					super.keyReleased(e);
					featureDefinition.setKey(keyTextField.getText());
				}
			});
			keyLabel.setLabelFor(keyTextField);
			//</editor-fold>

			JCheckBox hasManyValuesCheckBox = new JCheckBox("Has Many Values", false);
			hasManyValuesCheckBox.setSelected(featureDefinition.isHasMany());
			hasManyValuesCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					featureDefinition.setHasMany(hasManyValuesCheckBox.isSelected());
				}
			});

			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					ConfigureFeaturesFrame.this.definitionList.removeIf(featureDefinition1 -> featureDefinition1.getId() == featureDefinition.getId());
					loadFeatures(ConfigureFeaturesFrame.this.definitionList);
				}
			});

			featureDefinitionPanel.add(idLabel);
			featureDefinitionPanel.add(idTextField);
			featureDefinitionPanel.add(keyLabel);
			featureDefinitionPanel.add(keyTextField);
			featureDefinitionPanel.add(hasManyValuesCheckBox);
			featureDefinitionPanel.add(removeButton);

			this.featuresPanel.add(featureDefinitionPanel);
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				featuresPanel.revalidate();
				featuresPanel.repaint();
			}
		});
	}

	public void setInteractionListener(ConfigureFeaturesInteractionListener interactionListener) {

		this.interactionListener = interactionListener;
	}
}
