package lb.edu.aust.cce577.ife;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class Preferences {

	private static final String PREFERENCES_FILE_NAME = "preferences.properties";
	private final Properties properties;
	private final Gson gson;

	public Preferences() {

		this.properties = new Properties();
		this.gson = new Gson();

		this.load();
	}

	public boolean isMoveFileAfterSave() {

		return Boolean.parseBoolean(this.properties.getProperty("moveFileAfterSave", "true"));
	}

	public void setMoveFileAfterSave(boolean move) throws Exception {

		this.properties.setProperty("moveFileAfterSave", String.valueOf(move));
		save();
	}

	public File getDataDirectory() {

		return new File(this.properties.getProperty("dataDir", FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "invoice-feature-extraction"));
	}

	public void setDataDirectory(File dataDirectory) throws Exception {

		this.properties.setProperty("dataDir", dataDirectory.getAbsolutePath());

		save();
	}


	public List<FeatureDefinition> getFeatureDefinitions() {

		final String featureDefinitionsStr = properties.getProperty("featureDefinitions", "[]");
		return gson.fromJson(featureDefinitionsStr, new TypeToken<List<FeatureDefinition>>() {

		}.getType());
	}

	public void saveFeatureDefinitions(List<FeatureDefinition> definitions) throws Exception {

		final String json = gson.toJson(definitions);
		properties.setProperty("featureDefinitions", json);
		save();
	}


	private void load() {

		File propertiesFile = new File(PREFERENCES_FILE_NAME);

		if (propertiesFile.exists()) {

			try {
				try (BufferedReader bufferedReader = new BufferedReader(new FileReader(propertiesFile))) {
					properties.load(bufferedReader);
				}
			} catch (Exception ignored) {

			}
		}
	}

	private void save() throws Exception {

		synchronized (Preferences.class) {

			File propertiesFile = new File(PREFERENCES_FILE_NAME);
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(propertiesFile))) {
				properties.store(bufferedWriter, "Modified on " + new Date());
			}
		}
	}

}
