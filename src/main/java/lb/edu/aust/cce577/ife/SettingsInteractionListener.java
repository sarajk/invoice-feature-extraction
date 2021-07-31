package lb.edu.aust.cce577.ife;

import java.io.File;

/**
 * Created on 7/19/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public interface SettingsInteractionListener {

	void onDataDirectoryChanged(File dataDir);

	void onMoveFileAfterSaveChanged(boolean moveFileAfterSave);
}
