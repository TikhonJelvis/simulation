package simulation.engine;

import java.awt.Graphics;

/**
 * A spring that connects to objects.
 * 
 * @author Jacob Taylor
 * 
 */
public final class Spring {
	// natural length
	private double naturalLength;
	// k value
	private double strength;
	// connections
	private BodyConnection connection1, connection2;

	/**
	 * Create a spring with the given properties.
	 * 
	 * @param len
	 *            the natural length
	 * @param str
	 *            the strength (k value)
	 * @param b1
	 *            the first body
	 * @param b2
	 *            the second body
	 * @param connect1
	 *            where it connects with the first body
	 * @param connect2
	 *            where it connects with the second body
	 */
	public Spring(double len, double str, Body b1, Body b2, Vector connect1,
			Vector connect2) {
		naturalLength = len;
		strength = str;
		connection1 = new BodyConnection(b1, connect1);
		connection2 = new BodyConnection(b2, connect2);
	}

	/**
	 * Create a spring with the given properties.
	 * 
	 * @param str
	 *            the strength (k value)
	 * @param b1
	 *            the first body
	 * @param b2
	 *            the second body
	 * @param connect1
	 *            where it connects with the first body
	 * @param connect2
	 *            where it connects with the second body
	 */
	public Spring(double str, Body b1, Body b2, Vector connect1, Vector connect2) {
		// the length is the distance between the points
		this(connect2.subtract(connect1).magnitude(), str, b1, b2, connect1,
				connect2);
	}

	/**
	 * Get the natural length of the spring.
	 * 
	 * @return natural length
	 */
	public double naturalLength() {
		return naturalLength;
	}

	/**
	 * Get the spring's strength.
	 * 
	 * @return the strength
	 */
	public double strength() {
		return strength;
	}

	/**
	 * Get the first body the spring is connected to.
	 * 
	 * @return the first body it is connected to
	 */
	public Body connectedBody1() {
		return connection1.connectedBody();
	}

	/**
	 * Get the second body the spring is connected to.
	 * 
	 * @return the second body it is connected to
	 */
	public Body connectedBody2() {
		return connection2.connectedBody();
	}

	/**
	 * Get the point where the spring is connected to the first body.
	 * 
	 * @return the point where the spring is connected to the first body
	 */
	public Vector vertex1() {
		return connection1.connectedPoint();
	}

	/**
	 * Get the point where the spring is connected to the second body.
	 * 
	 * @return the point where the spring is connected to the second body
	 */
	public Vector vertex2() {
		return connection2.connectedPoint();
	}

	/**
	 * Have the spring apply force to its connections a certain amount.
	 * 
	 * @param amount
	 *            the number of time units that have passed
	 */
	public void step(double amount) {
		Vector v1 = vertex1(), v2 = vertex2();
		double dist = v1.subtract(v2).magnitude();
		// calculate impulse to pull them together with
		// if this is negative, they will be pushed together instead
		double pull = amount * strength * (dist - naturalLength);
		// impulse vector to apply to body1
		Vector pullv1v2 = v2.subtract(v1).withMagnitude(pull);
		connectedBody1().addImpulse(pullv1v2, v1);
		connectedBody2().addImpulse(pullv1v2.invert(), v2);
	}

	/**
	 * Draw the spring on a graphics object.
	 * 
	 * @param g
	 *            graphics object
	 */
	public void draw(Graphics g) {
		// draw line between vertices
		Vector v1 = vertex1(), v2 = vertex2();
		g.drawLine((int) v1.getX(), (int) v1.getY(), (int) v2.getX(), (int) v2.getY());
	}
}
