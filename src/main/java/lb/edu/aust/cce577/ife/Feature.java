package lb.edu.aust.cce577.ife;

import java.io.Serializable;
import java.util.*;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class Feature implements Serializable {

	private int id;
	private String key;
	private BoundingBox boundingBox;
	private boolean hasMany;
	private final List<BoundingBox> valueBoundingBoxes;

	public Feature() {

		this.valueBoundingBoxes = new ArrayList<>();
	}

	public Feature(int id, String key, boolean hasMany) {

		this(id);
		this.key = key;
		this.hasMany = hasMany;
	}

	public Feature(int id) {

		this.id = id;
		valueBoundingBoxes = new ArrayList<>();
	}

	public int getId() {

		return id;
	}

	public String getKey() {

		return key;
	}

	public void setKey(String key) {

		this.key = key;
	}

	public BoundingBox getBoundingBox() {

		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {

		if (boundingBox == null) {
			this.boundingBox = null;
			return;
		}
		setBoundingBox(boundingBox.getId(), boundingBox.getX(), boundingBox.getY(), boundingBox.getWidth(), boundingBox.getHeight());
	}

	private void setBoundingBox(UUID id, double x, double y, double width, double height) {

		this.boundingBox = new BoundingBox(id, x, y, width, height);
	}

	public boolean isHasMany() {

		return hasMany;
	}

	public void setHasMany(boolean hasMany) {

		this.hasMany = hasMany;
	}

	public List<BoundingBox> getValueBoundingBoxes() {

		return Collections.unmodifiableList(valueBoundingBoxes);
	}

	public void setValueBoundingBoxes(List<BoundingBox> valueBoundingBoxes) {

		this.valueBoundingBoxes.clear();
		this.valueBoundingBoxes.addAll(valueBoundingBoxes);
	}

	public void addValueBoundingBox(BoundingBox boundingBox) {

		this.valueBoundingBoxes.removeIf(b -> boundingBox.getId().equals(b.getId()));
		this.valueBoundingBoxes.add(boundingBox);
	}

	public void removeValueBoundingBox(UUID id) {

		this.valueBoundingBoxes.removeIf(boundingBox -> boundingBox.getId().equals(id));
	}

	public BoundingBox getFirstValueBoundingBox() {

		final Optional<BoundingBox> first = this.valueBoundingBoxes.stream().findFirst();
		return first.orElse(null);
	}

	public void reset() {

		boundingBox = null;
		valueBoundingBoxes.clear();
	}

	@Override
	public String toString() {

		return "lb.edu.aust.cce570.ife.Feature{" +
				"id=" + id +
				", key='" + key + '\'' +
				", boundingBox=" + boundingBox +
				", hasMany=" + hasMany +
				", valueBoundingBoxes=" + valueBoundingBoxes +
				'}';
	}

	public static class BoundingBox implements Serializable {

		private UUID id;
		private double x;
		private double y;
		private double width;
		private double height;

		public BoundingBox() {

			id = UUID.randomUUID();
		}

		public BoundingBox(UUID id) {

			this.id = id;
		}

		public BoundingBox(UUID id, double x, double y, double width, double height) {

			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		protected void setId(UUID id) {

			this.id = id;
		}

		public UUID getId() {

			return id;
		}

		public double getX() {

			return x;
		}

		protected void setX(double x) {

			this.x = x;
		}

		public double getY() {

			return y;
		}

		protected void setY(double y) {

			this.y = y;
		}

		public double getWidth() {

			return width;
		}

		protected void setWidth(double width) {

			this.width = width;
		}

		public double getHeight() {

			return height;
		}

		protected void setHeight(double height) {

			this.height = height;
		}

		@Override
		public String toString() {

			return String.format("%.2fX, %.2fY, %.2fW, %.2fH", x, y, width, height);
		}

		public double[] getBoundaries() {

			return new double[]{x, y, width, height};
		}
	}

}
