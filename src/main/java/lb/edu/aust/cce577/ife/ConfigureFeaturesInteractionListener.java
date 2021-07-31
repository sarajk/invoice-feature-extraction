package lb.edu.aust.cce577.ife;

import java.util.List;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public interface ConfigureFeaturesInteractionListener {

//	void onDispose();

	void onSave(List<FeatureDefinition> newDefinitionList);
}
