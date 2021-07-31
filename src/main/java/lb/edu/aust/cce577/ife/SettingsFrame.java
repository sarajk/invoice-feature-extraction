package lb.edu.aust.cce577.ife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class SettingsFrame extends JFrame {

	private final JPanel mainPanel;
	private final JPanel footerPanel;
	private final JButton cancelButton;
	private final JButton saveButton;
	private final JPanel settingsPanel;
	private Preferences preferences;
	private SettingsInteractionListener interactionListener;
	private JTextField dataDirectoryTextField;
	private JCheckBox moveFilesAfterSaveCheckBox;

	public SettingsFrame(Preferences preferences) {

		super("Settings");
		this.preferences = preferences;

		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setAlwaysOnTop(true);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());

		this.settingsPanel = new JPanel();
		this.settingsPanel.setLayout(new FlowLayout());

		createSettingsComponents(this.preferences);

		this.mainPanel.add(this.settingsPanel, BorderLayout.CENTER);

		this.footerPanel = new JPanel();
		this.footerPanel.setLayout(new FlowLayout());

		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				dispose();
			}
		});

		this.saveButton = new JButton("Save");
		this.saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (interactionListener != null) {

					if (dataDirectoryTextField != null) {
						interactionListener.onDataDirectoryChanged(new File(dataDirectoryTextField.getText()));
						interactionListener.onMoveFileAfterSaveChanged(moveFilesAfterSaveCheckBox.isSelected());
					}
					dispose();
				}
			}
		});

		this.footerPanel.add(this.cancelButton);
		this.footerPanel.add(this.saveButton);


		this.add(mainPanel, BorderLayout.CENTER);
		this.add(footerPanel, BorderLayout.SOUTH);


		pack();
	}


	private void createSettingsComponents(Preferences preferences) {

		//<editor-fold desc="Data Directory">
		final File dataDirectory = preferences.getDataDirectory();
		final JPanel dataDirectoryPanel = new JPanel();
		dataDirectoryPanel.setLayout(new FlowLayout());

		final JLabel dataDirectoryLabel = new JLabel("Data Directory Path");
		dataDirectoryTextField = new JTextField(dataDirectory.getAbsolutePath(), 30);
		dataDirectoryTextField.setEditable(false);
		final JButton dataDirectoryChangeButton = new JButton("Change");
		dataDirectoryChangeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				final int option = fileChooser.showDialog(SettingsFrame.this, "Select");

				if (option == JFileChooser.APPROVE_OPTION) {

					try {
						dataDirectoryTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		});


		dataDirectoryPanel.add(dataDirectoryLabel);
		dataDirectoryPanel.add(dataDirectoryTextField);
		dataDirectoryPanel.add(dataDirectoryChangeButton);


		this.settingsPanel.add(dataDirectoryPanel);
		//</editor-fold>

		moveFilesAfterSaveCheckBox = new JCheckBox("Move File After Save", preferences.isMoveFileAfterSave());


		this.settingsPanel.add(moveFilesAfterSaveCheckBox);
	}

	public void setInteractionListener(SettingsInteractionListener interactionListener) {

		this.interactionListener = interactionListener;
	}
}
