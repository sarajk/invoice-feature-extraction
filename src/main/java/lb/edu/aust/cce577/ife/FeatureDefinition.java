package lb.edu.aust.cce577.ife;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created on 7/18/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class FeatureDefinition implements Serializable, Comparable<FeatureDefinition> {

	private int id;
	private String key;
	private boolean hasMany;

	public FeatureDefinition() {

	}

	public FeatureDefinition(int id, String key, boolean hasMany) {

		this.id = id;
		this.key = key;
		this.hasMany = hasMany;
	}

	public int getId() {

		return id;
	}

	public void setId(int id) {

		this.id = id;
	}

	public String getKey() {

		return key;
	}

	public void setKey(String key) {

		this.key = key;
	}

	public boolean isHasMany() {

		return hasMany;
	}

	public void setHasMany(boolean hasMany) {

		this.hasMany = hasMany;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (!(o instanceof FeatureDefinition)) return false;
		FeatureDefinition that = (FeatureDefinition) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}

	@Override
	public int compareTo(FeatureDefinition o) {

		return Integer.compare(id, o.id);
	}

	@Override
	public String toString() {

		return "lb.edu.aust.cce570.ife.FeatureDefinition{" +
				"id=" + id +
				", key='" + key + '\'' +
				", hasMany=" + hasMany +
				'}';
	}
}
