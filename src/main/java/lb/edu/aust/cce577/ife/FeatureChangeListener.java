package lb.edu.aust.cce577.ife;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public interface FeatureChangeListener {

	void onActivated(FeatureComponent component);

	void onBoundingBoxDeleted(FeatureComponent component, Feature.BoundingBox boundingBox);
}
