package lb.edu.aust.cce577.ife;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 7/20/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class TrainingDataSetGenerator {

	private TrainingDataSetGenerator() {

	}

	public static List<String[]> generateTrainingRecords(List<Feature> features) {

		List<String[]> trainingRecords = new ArrayList<>();

		for (Feature targetFeature : features) {

			for (Feature testFeature : features) {

				final List<String[]> records = prepareTrainingRecords(targetFeature, testFeature, targetFeature == testFeature);
				trainingRecords.addAll(records);
			}
		}

		return trainingRecords;
	}


	private static List<String[]> prepareTrainingRecords(Feature targetFeature, Feature testFeature, boolean correct) {

		List<String[]> trainingRecords = new LinkedList<>();
		final Feature.BoundingBox boundingBox = targetFeature.getBoundingBox();

		if (boundingBox != null) {
			for (Feature.BoundingBox valueBoundingBox : testFeature.getValueBoundingBoxes()) {

				String targetX = String.format("%.2f", boundingBox.getX());
				String targetY = String.format("%.2f", boundingBox.getY());
				String targetWith = String.format("%.2f", boundingBox.getWidth());
				String targetHeight = String.format("%.2f", boundingBox.getHeight());

				String testX = String.format("%.2f", valueBoundingBox.getX());
				String testY = String.format("%.2f", valueBoundingBox.getY());
				String testWith = String.format("%.2f", valueBoundingBox.getWidth());
				String testHeight = String.format("%.2f", valueBoundingBox.getHeight());


				trainingRecords.add(new String[]{String.valueOf(targetFeature.getId()), targetFeature.getKey(), targetX,
						targetY, targetWith, targetHeight, String.valueOf(testFeature.getId()), testFeature.getKey(),
						testX, testY, testWith, testHeight, String.valueOf(correct)});
			}
		}

		return trainingRecords;
	}
}
