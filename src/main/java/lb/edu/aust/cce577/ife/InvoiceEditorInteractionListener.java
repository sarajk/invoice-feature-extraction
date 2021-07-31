package lb.edu.aust.cce577.ife;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public interface InvoiceEditorInteractionListener {

	void onBoundingBoxDrawn(Feature.BoundingBox boundingBox);

	void onSave(File originalFile, BufferedImage editedImage, List<Feature.BoundingBox> boundingBoxes);
}
