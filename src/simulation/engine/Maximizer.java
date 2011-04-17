package simulation.engine;

/**
 * A utility class to find the maximum of a bunch of values.
 * 
 * @author Jacob Taylor
 * 
 * @param <T>
 *            the type of object to find the maximum of
 */
public abstract class Maximizer<T> {
	// current maximum
	private T value;
	// current measurement of maximum
	private double measure;

	public Maximizer() {
		value = null;
		measure = Double.NEGATIVE_INFINITY;
	}

	/**
	 * Measure a value.
	 * 
	 * @param val
	 *            the value to measure
	 * @return the value's measurement
	 */
	protected abstract double measure(T val);

	/**
	 * Add a value, possibly making it the new maximum.
	 * 
	 * @param val
	 *            the value to add
	 */
	public void add(T val) {
		if (val != null) {
			double m = measure(val);
			if (m >= measure) {
				value = val;
				measure = m;
			}
		}
	}

	/**
	 * Get the current maximum value.
	 * 
	 * @return the current maximum, or null if no values have been added.
	 */
	public T currentBest() {
		return value;
	}
}
