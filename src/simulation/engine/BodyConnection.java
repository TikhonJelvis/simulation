package simulation.engine;

/**
 * A connection to a body. When the body moves or rotates, the connection stays
 * on the same relative point.
 * 
 * @author Jacob Taylor
 * 
 */
public class BodyConnection {
	private Body connectedTo;
	private double angle;
	private double magnitude;

	/**
	 * Creates a connection on a certain body at a certain point
	 * 
	 * @param body
	 *            what body the connection is on
	 * @param point
	 *            where the connection is
	 */
	public BodyConnection(Body body, Vector point) {
		connectedTo = body;
		Vector rel = point.subtract(body.getShape().center());
		angle = rel.angle() - body.getShape().rotation();
		magnitude = rel.magnitude();
	}

	/**
	 * Get the point's current location
	 * 
	 * @return the current location
	 */
	public Vector connectedPoint() {
		return connectedTo.getShape().center().add(
				Vector.fromAngle(angle + connectedTo.getShape().rotation(),
						magnitude));
	}

	/**
	 * Applies a force at this point to the body
	 * 
	 * @param imp
	 *            the impulse to add
	 */
	public void addImpulse(Vector imp) {
		connectedTo.addImpulse(imp, connectedPoint());
	}

	/**
	 * Get the body this is connected to
	 * 
	 * @return the body this is connected to
	 */
	public Body connectedBody() {
		return connectedTo;
	}

	public Vector getVelocity() {
		return connectedTo.velocityAt(connectedPoint());
	}
}
