package lb.edu.aust.cce577.ife;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * Created on 7/17/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class InvoiceFeatureExtractionFrame extends JFrame implements InvoiceEditorInteractionListener, FeatureChangeListener {

	private final Preferences preferences;

	private final List<FeatureComponent> featureComponents;
	private final JMenuBar menubar;
	private final JPanel featureComponentsPanel;
	private final JScrollPane featureScrollPane;
	private final JPanel editingPanel;
	private final InvoiceEditorPanel invoiceEditorPanel;
	private final JPanel featuresPanel;
	private final JPanel actionsPanel;

	private final Deque<File> loadedFiles;
	private final JLabel loadedDirectoryLabel;
	private final JButton loadButton;
	private final JButton skipButton;
	private final JButton saveButton;
	private final JLabel remainingLabel;
	private File loadedDirectory;

	public InvoiceFeatureExtractionFrame() {

		super("Image Feature Linker");

		this.preferences = new Preferences();
		this.featureComponents = new ArrayList<>();
		this.loadedFiles = new LinkedBlockingDeque<>();

		this.setLayout(new BorderLayout());

		this.setPreferredSize(new Dimension(1200, 900));
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.featuresPanel = new JPanel();
		this.featuresPanel.setBackground(Color.GREEN);
		this.featuresPanel.setLayout(new BorderLayout());

		this.editingPanel = new JPanel();
		this.editingPanel.setLayout(new BorderLayout());

		this.invoiceEditorPanel = new InvoiceEditorPanel();
		this.invoiceEditorPanel.setBackground(Color.BLACK);
		this.editingPanel.add(this.invoiceEditorPanel, BorderLayout.CENTER);


		this.add(this.featuresPanel, BorderLayout.EAST);


		this.featureComponentsPanel = new JPanel();
		featureComponentsPanel.setLayout(new BoxLayout(featureComponentsPanel, BoxLayout.Y_AXIS));

		featureComponents.addAll(createFeatureComponents(toFeatures(preferences.getFeatureDefinitions())));
		loadFeatureComponents(featureComponents);


		this.add(this.editingPanel, BorderLayout.CENTER);


		//<editor-fold desc="Actions Panel">
		this.actionsPanel = new JPanel();
		this.actionsPanel.setLayout(new FlowLayout());

		this.loadedDirectoryLabel = new JLabel("No directory loaded yet...");
		this.loadButton = new JButton("Load Directory Content");
		this.skipButton = new JButton("Skip");
		this.saveButton = new JButton("Save & Next");

		this.saveButton.setEnabled(false);
		this.skipButton.setEnabled(false);

		this.remainingLabel = new JLabel();

		this.saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					invoiceEditorPanel.save();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});

		this.skipButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				loadNextFileIntoEditor();
			}
		});

		this.loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				final int result = fileChooser.showDialog(InvoiceFeatureExtractionFrame.this, "Load");

				if (result == JFileChooser.APPROVE_OPTION) {

					loadDirectoryContent(fileChooser.getSelectedFile());
				}
			}
		});

		this.actionsPanel.add(loadedDirectoryLabel);
		this.actionsPanel.add(loadButton);
		this.actionsPanel.add(skipButton);
		this.actionsPanel.add(saveButton);
		this.actionsPanel.add(remainingLabel);
		this.add(this.actionsPanel, BorderLayout.SOUTH);
		//</editor-fold>


		pack();

		featureScrollPane = new JScrollPane(featureComponentsPanel) {

			@Override
			public Dimension getPreferredSize() {

				setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				Dimension dim = new Dimension(super.getPreferredSize().width + getVerticalScrollBar().getSize().width,
						featuresPanel.getHeight());
				setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				return dim;
			}
		};
		featureScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final JScrollBar verticalScrollBar = featureScrollPane.getVerticalScrollBar();
		verticalScrollBar.setUnitIncrement(16);
		featuresPanel.add(featureScrollPane);

		invoiceEditorPanel.setBoundingBoxChangeListener(this);


		menubar = new JMenuBar();
		configureMenuBar(menubar);

		pack();

	}

	private void loadDirectoryContent(File selectedFile) {

		if (selectedFile.isDirectory()) {

			this.loadedDirectory = selectedFile;
			loadedDirectoryLabel.setText(selectedFile.getAbsolutePath());

			final File[] files = selectedFile.listFiles();

			if (files != null) {
				this.loadedFiles.clear();
				this.loadedFiles.addAll(Arrays.stream(files)
						.filter(file -> file.exists() && file.isFile())
						.sorted()
						.collect(Collectors.toList()));

				loadNextFileIntoEditor();
			}
		}
	}

	private void loadNextFileIntoEditor() {

		File file = null;
		while ((file = this.loadedFiles.poll()) != null) {

			if (this.invoiceEditorPanel.testLoad(file)) {
				this.loadFileIntoEditor(file);
				break;
			}
		}

		remainingLabel.setText("Remaining files: " + this.loadedFiles.size());

		if (file == null) {
			JOptionPane.showMessageDialog(InvoiceFeatureExtractionFrame.this,
					"There are no files left in the queue!", "Reached the end", JOptionPane.INFORMATION_MESSAGE);
			resetEditor();
		}
	}

	private void resetEditor() {

		this.resetFeatures();

		this.invoiceEditorPanel.reset();
		this.saveButton.setEnabled(false);
		this.skipButton.setEnabled(false);
	}

	private void loadFileIntoEditor(File file) {

		try {

			this.resetFeatures();

			this.invoiceEditorPanel.load(file);
			this.saveButton.setEnabled(true);
			this.skipButton.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(InvoiceFeatureExtractionFrame.this, e.getMessage(),
					"Failed to load file", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void resetFeatures() {

		for (FeatureComponent featureComponent : featureComponents) {
			final Feature feature = featureComponent.getFeature();
			feature.reset();
			featureComponent.refresh();
		}

		focusFeatureComponent(0);
	}


	private void configureMenuBar(JMenuBar menubar) {

		//<editor-fold desc="File">
		JMenu fileMenu = new JMenu("File");
		//<editor-fold desc="Configure Features">
		final JMenuItem configureFeaturesMenuItem = new JMenuItem("Configure Features");
		configureFeaturesMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				ConfigureFeaturesFrame configureFeaturesFrame =
						new ConfigureFeaturesFrame(preferences.getFeatureDefinitions());
				configureFeaturesFrame.setInteractionListener(new ConfigureFeaturesInteractionListener() {

					@Override
					public void onSave(List<FeatureDefinition> newDefinitionList) {

						try {
							preferences.saveFeatureDefinitions(newDefinitionList);
							featureComponents.clear();
							featureComponents.addAll(createFeatureComponents(toFeatures(newDefinitionList)));
							loadFeatureComponents(featureComponents);

							invoiceEditorPanel.removeAllBoundingBoxes();

						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			}
		});

		fileMenu.add(configureFeaturesMenuItem);
		//</editor-fold>

		//<editor-fold desc="Configure Features">
		final JMenuItem settingsMenuItem = new JMenuItem("Settings");
		settingsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SettingsFrame settingsFrame = new SettingsFrame(preferences);
				settingsFrame.setInteractionListener(new SettingsInteractionListener() {

					@Override
					public void onDataDirectoryChanged(File dataDir) {

						try {
							preferences.setDataDirectory(dataDir);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					@Override
					public void onMoveFileAfterSaveChanged(boolean moveFileAfterSave) {

						try {
							preferences.setMoveFileAfterSave(moveFileAfterSave);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				});

			}
		});

		fileMenu.add(settingsMenuItem);
		//</editor-fold>
		menubar.add(fileMenu);
		//</editor-fold>
		this.setJMenuBar(menubar);
	}


	@Override
	public void onBoundingBoxDrawn(Feature.BoundingBox boundingBox) {

		final FeatureComponent focusedFeatureComponent = getFocusedFeatureComponent();

		if (focusedFeatureComponent == null) {
			return;
		}

		final int i = featureComponents.indexOf(focusedFeatureComponent);

		final Feature feature = focusedFeatureComponent.getFeature();

		if (feature.getBoundingBox() == null) {
			feature.setBoundingBox(boundingBox);
		} else if (!feature.isHasMany() && feature.getFirstValueBoundingBox() == null) {
			feature.addValueBoundingBox(boundingBox);
			focusFeatureComponent(i + 1);
		} else if (feature.isHasMany()) {
			feature.addValueBoundingBox(boundingBox);
		}
		focusedFeatureComponent.refresh();
	}

	@Override
	public void onSave(File originalFile, BufferedImage editedImage, List<Feature.BoundingBox> boundingBoxes) {

		try {
			final String baseName = FilenameUtils.getBaseName(originalFile.getName());

			final File dataDirectory = new File(preferences.getDataDirectory(), baseName);
			if (!dataDirectory.exists()) {
				if (!dataDirectory.mkdirs()) {
					JOptionPane.showMessageDialog(this, "Failed to create directory " +
							dataDirectory.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			final String extension = FilenameUtils.getExtension(originalFile.getName());

			final File newFile = new File(dataDirectory, baseName + "-processed." + extension);

			ImageIO.write(editedImage, extension, newFile);

			FileUtils.copyFile(originalFile, new File(dataDirectory, originalFile.getName()), StandardCopyOption.REPLACE_EXISTING);
			if (preferences.isMoveFileAfterSave()) {
				FileUtils.delete(originalFile);
			}

			List<Feature> features = featureComponents.stream().map(FeatureComponent::getFeature).collect(Collectors.toList());

			final Gson gson = new GsonBuilder().setPrettyPrinting().create();
			final String featuresJson = gson.toJson(features);
			final File featuresFile = new File(dataDirectory, "features.json");
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(featuresFile))) {
				bufferedWriter.write(featuresJson);
				bufferedWriter.flush();
			}

			final File trainingFile = new File(dataDirectory, "training.csv");


			List<String[]> trainingRecords = new LinkedList<>();

			trainingRecords.add(new String[]{"id", "key", "x", "y", "width", "height",
					"value.id", "value.key", "value.x",
					"value.y", "value.width", "value.height", "match"});

			trainingRecords.addAll(TrainingDataSetGenerator.generateTrainingRecords(features));

			try (CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(trainingFile)))) {

				writer.writeAll(trainingRecords);
				writer.flush();
			}

			loadNextFileIntoEditor();
		} catch (
				Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(InvoiceFeatureExtractionFrame.this, ex.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void onActivated(FeatureComponent component) {

		final int i = featureComponents.indexOf(component);
		focusFeatureComponent(i);
	}

	@Override
	public void onBoundingBoxDeleted(FeatureComponent component, Feature.BoundingBox boundingBox) {

		invoiceEditorPanel.removeBoundingBox(boundingBox);
	}

	private List<FeatureComponent> createFeatureComponents(List<Feature> featureList) {

		return featureList.stream().map(FeatureComponent::new).collect(Collectors.toList());
	}

	private void focusFeatureComponent(int index) {

		for (int i = 0, featureComponentsSize = featureComponents.size(); i < featureComponentsSize; i++) {
			FeatureComponent featureComponent = featureComponents.get(i);
			featureComponent.setFocused(i == index);

			if (i == index) {

				final Rectangle visibleRect = featureComponent.getVisibleRect();
				if (!visibleRect.getSize().equals(featureComponent.getSize()) || visibleRect.isEmpty()) {
					featureComponent.scrollRectToVisible(featureComponent.getBounds());

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

							if (featureScrollPane != null) {
								final JScrollBar verticalScrollBar = featureScrollPane.getVerticalScrollBar();
								verticalScrollBar.setValue(verticalScrollBar.getValue() - 10);
							}
						}
					});
				}
			}
		}
	}

	private FeatureComponent getFocusedFeatureComponent() {

		return featureComponents.stream().filter(FeatureComponent::isFocused).findFirst().orElse(null);
	}

	private List<Feature> toFeatures(List<FeatureDefinition> definitions) {

		List<Feature> features = new ArrayList<>();
		for (FeatureDefinition definition : definitions) {
			features.add(new Feature(definition.getId(), definition.getKey(), definition.isHasMany()));
		}

		return features;
	}

	private void loadFeatureComponents(List<FeatureComponent> featureComponents) {

		featureComponentsPanel.removeAll();

		for (FeatureComponent featureComponent : featureComponents) {
			featureComponent.setFeatureChangeListener(this);
			featureComponentsPanel.add(featureComponent);
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				featureComponentsPanel.revalidate();
				featureComponentsPanel.repaint();
			}
		});

		if (!featureComponents.isEmpty()) {
			focusFeatureComponent(0);
		}
	}

}
