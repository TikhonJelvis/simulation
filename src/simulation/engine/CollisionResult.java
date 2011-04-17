package simulation.engine;

/**
 * The result of a collision calculation.
 * 
 * @author Jacob Taylor
 * 
 */
public final class CollisionResult {
	// where the collision happened
	private Vector contactPoint;
	// how the first shape must be moved to remove it
	private Vector translation;

	/**
	 * Creates a CollisionResult object describing the result of a collision
	 * calculation.
	 * 
	 * @param contact
	 *            where the collision happened
	 * @param trans
	 *            how much the first shape should be moved
	 */
	public CollisionResult(Vector contact, Vector trans) {
		contactPoint = contact;
		translation = trans;
	}

	/**
	 * Get the location of the contact.
	 * 
	 * @return the contact point
	 */
	public Vector contactPoint() {
		return contactPoint;
	}

	/**
	 * Get the vector that moves the first object out of the collision.
	 * 
	 * @return the vector that moves the first object out of the collision
	 */
	public Vector translation() {
		return translation;
	}

	/**
	 * Get a copy of this with the translation vector inverted. This may be used
	 * to make it describe the other object's translation.
	 * 
	 * @return the modified CollisionResult
	 */
	public CollisionResult invert() {
		return new CollisionResult(contactPoint, translation.invert());
	}
}
