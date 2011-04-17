package simulation.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class represents a rigid body. It collides with other bodies.
 * 
 * @author Jacob Taylor
 */
public final class Body {

	// Default values:
	private static double defaultBounciness = 0.3;
	private static double defaultFriction = 0;
	private static double defaultDensity = 0.025;

	// Shape types:
	/**
	 * Type constant that corresponds to any shape that does not fit into the
	 * other categories.
	 */
	public static final int DEFAULT_SHAPE = 0;
	/**
	 * Type constant that corresponds to a polygon; currently this is the same
	 * as <code>DEFAULT_SHAPE</code>.
	 */
	public static final int POLYGON = DEFAULT_SHAPE;
	/**
	 * Type constant that corresponds to a rectangle, particularly to all
	 * rectangles created by the add rectangle tool. A polygon created by some
	 * other method may actually be a rectangle, but will not necessarily be
	 * recognized as such.
	 */
	public static final int RECTANGLE = 1;
	/**
	 * Type constant that corresponds to a circle.
	 */
	public static final int CIRCLE = 2;
	/**
	 * Type constant that corresponds to a wall.
	 */
	public static final int WALL = 3;

	// The actual body's fields:
	private CollisionShape shape;
	private Vector velocity;

	// The type of shape this body represents:
	private int type = 0;

	// Angular velocity in radians / time unit
	private double angularVelocity;
	private double mass;
	private double density;
	private double momentOfInertia;

	// The product of two bodies' bouncinesses determines how much they
	// repel each other in a collision. If the product is 0, the
	// collision is completely inelastic; if it is 1, it is completely elastic.
	private double bounciness;

	// The product of two bodies' frictions is the coefficient of friction.
	// There is no difference between static and kinetic friction.
	private double friction;

	// If this is true, then the object will not move.
	private boolean fixed;

	private Color color;// The body's color.

	// Event management:
	private ArrayList<ChangeListener> changeListenersArrayList = new ArrayList<ChangeListener>();
	private List<ChangeListener> changeListeners = Collections
			.synchronizedList(changeListenersArrayList);

	/**
	 * Creates a body with a given shape, mass, type and color.
	 * 
	 * @param shape
	 *            - the body's shape
	 * @param mass
	 *            - the body's mass
	 * @param color
	 *            - the color this body will be.
	 * @param type
	 *            - the type of shape this is. The default is 0, specifying a
	 *            number that is not defined is the same as specifying 0.
	 */
	public Body(CollisionShape shape, double mass, Color color, int type) {
		this.shape = shape;
		this.mass = mass;
		this.type = type;

		velocity = new Vector();
		density = mass / shape.area();
		momentOfInertia = density * shape.momentOfInertia();

		bounciness = getDefaultBounciness();
		friction = getDefaultFriction();

		setColor(color);
	}

	/**
	 * Creates a body with the given shape and mass, with a random color. The
	 * only guarantee about the color is that it will not be translucent or
	 * transparent. The shape will have the specified type.
	 * 
	 * @param shape
	 *            - the shape of the body.
	 * @param mass
	 *            - the mass of the body.
	 * @param type
	 *            - the type of shape this is. The default is 0, specifying a
	 *            number that is not defined is the same as specifying 0.
	 */
	public Body(CollisionShape shape, double mass, int type) {
		this(shape, mass, new Color((int) (Math.random() * 256), (int) (Math
				.random() * 256), (int) (Math.random() * 256)), type);
	}

	/**
	 * Creates a body with the specified shape, mass, color as well as a the
	 * default type of 0, meaning that this is a polygon.
	 * 
	 * @param shape
	 *            - the shape of the body.
	 * @param mass
	 *            - the mass of the body.
	 * @param color
	 *            - the color of the body.
	 */
	public Body(CollisionShape shape, double mass, Color color) {
		this(shape, mass, color, 0);
	}

	/**
	 * Creates a body with the given shape and mass, with a random color. The
	 * only guarantee about the color is that it will not be translucent or
	 * transparent. This body will have the default type.
	 * 
	 * @param shape
	 *            - the shape of this body.
	 * @param mass
	 *            the mass of this body.
	 */
	public Body(CollisionShape shape, double mass) {
		this(shape, mass, new Color((int) (Math.random() * 256), (int) (Math
				.random() * 256), (int) (Math.random() * 256)), 0);
	}

	/**
	 * Creates a body with the given shape, color and type and the default
	 * density. The density is maintained by giving the body a mass equal to
	 * <code>defaultDensity * shape.area()</code>, where <code>shape</code> is
	 * the provided <code>CollisionShape</code>.
	 * 
	 * @param shape
	 *            - the shape of the body.
	 * @param color
	 *            - the color of the body.
	 * @param type
	 *            - the type of shape this is. The default is 0, specifying a
	 *            number that is not defined is the same as specifying 0.
	 */
	public Body(CollisionShape shape, Color color, int type) {
		this(shape, defaultDensity * shape.area(), color, type);
	}

	/**
	 * Creates a body with the given shape and type and the default density. The
	 * density is maintained by giving the body a mass equal to <code> 
	 * defaultDensity * shape.area()</code>
	 * , where <code>shape</code> is the provided <code>CollisionShape</code>.
	 * 
	 * @param shape
	 *            - the shape of the body.
	 * @param type
	 *            - the type of shape this is. The default is 0, specifying a
	 *            number that is not defined is the same as specifying 0.
	 */
	public Body(CollisionShape shape, int type) {
		this(shape, defaultDensity * shape.area(), type);
	}

	/**
	 * Creates a body with the given shape and color and the default density.
	 * The density is maintained by giving the body a mass equal to <code> 
	 * defaultDensity * shape.area()</code>
	 * , where <code>shape</code> is the provided <code>CollisionShape</code>.
	 * 
	 * @param shape
	 *            - the shape of this body.
	 * @param color
	 *            - the color of this body.
	 */
	public Body(CollisionShape shape, Color color) {
		this(shape, defaultDensity * shape.area(), color);
	}

	/**
	 * Creates a body with the given shape and the default density. The density
	 * is maintained by giving the body a mass equal to <code> defaultDensity *
	 * shape.area()</code>, where <code>shape</code> is the provided <code>
	 * CollisionShape</code>
	 * .
	 * 
	 * @param shape
	 *            - the shape of this body.
	 */
	public Body(CollisionShape shape) {
		this(shape, defaultDensity * shape.area());
	}

	/**
	 * Creates a new body based on the supplied archive string. The supplied
	 * string should have been originally created by <code> toArchiveString()
	 * </code> or to that exact
	 * specification, otherwise this constructor will not work very well.
	 * 
	 * @param archiveString
	 *            - the string from which to recreate this body.
	 */
	public Body(String archiveString) {
		this.toArchiveString();
		String[] parts = archiveString.split(";");
		int type = Integer.valueOf(parts[0]);
		// CollisionShape shape =
		// CollisionShape.createFromArchiveString(parts[1]);
		double mass = Double.valueOf(parts[2]);
		double bounciness = Double.valueOf(parts[3]);
		double friction = Double.valueOf(parts[4]);
		Vector velocity = new Vector(parts[5]);
		double angularVelocity = Double.valueOf(parts[6]);
		double momentOfInertia = Double.valueOf(parts[7]);
		boolean fixed = !(Integer.valueOf(parts[8]) == 0);
		Color color = new Color(Integer.valueOf(parts[9]), Integer
				.valueOf(parts[10]), Integer.valueOf(parts[11]), Integer
				.valueOf(parts[12]));

		this.type = type;
		// TODO exporting!
		// this.shape = shape;
		this.mass = mass;
		setBounciness(bounciness);
		setFriction(friction);
		setVelocity(velocity);
		setAngularVelocity(angularVelocity);
		this.momentOfInertia = momentOfInertia;
		setFixed(fixed);
		setColor(color);
	}

	/**
	 * Get the body's velocity.
	 * 
	 * @return the velocity vector
	 */
	public Vector velocity() {
		return velocity;
	}

	/**
	 * Get the body's angular velocity.
	 * 
	 * @return the angular velocity in radians per time unit
	 */
	public double angularVelocity() {
		return angularVelocity;
	}

	/**
	 * Set the body's angular velocity.
	 * 
	 * @param v
	 *            the new angular velocity in radians per time unit
	 */
	public void setAngularVelocity(double v) {
		angularVelocity = v;
	}

	/**
	 * Get the body's mass.
	 * 
	 * @return the mass
	 */
	public double mass() {
		return mass;
	}

	/**
	 * Get the body's density.
	 * 
	 * @return the density
	 */
	public double density() {
		return density;
	}

	/**
	 * Get the body's moment of inertia (the resistance to change in angular
	 * velocity)
	 * 
	 * @return the moment of inertia
	 */
	public double momentOfInertia() {
		return momentOfInertia;
	}

	/**
	 * Get the body's bounciness. This determines how elastic a collision is.
	 * 
	 * @return the bounciness
	 */
	public double bounciness() {
		return bounciness;
	}

	/**
	 * Returns whether the body is fixed. A fixed body cannot move.
	 * 
	 * @return whether or not it is fixed.
	 */
	public boolean fixed() {
		return fixed;
	}

	/**
	 * If true, makes the body fixed; if false, makes it free.
	 * 
	 * @param on
	 *            true if it should be fixed; false if not
	 */
	public void setFixed(boolean on) {
		fixed = on;
	}

	/**
	 * Set the bounciness, which determines how elastic a collision is. The
	 * value should be between 0 and 1 inclusive; if it isn't nothing will
	 * happened and no exception will be thrown.
	 * 
	 * @param bounciness
	 *            - the new bounciness
	 */
	public void setBounciness(double bounciness) {
		if (bounciness >= 0 && bounciness <= 1) {
			this.bounciness = bounciness;
		}
	}

	/**
	 * Get the friction, which helps determine the coefficient of friction.
	 * 
	 * @return the friction
	 */
	public double friction() {
		return friction;
	}

	/**
	 * Set the friction, which helps determine the coefficient of friction. If
	 * the friction supplied is not between 0 and 1 (inclusive) then nothing
	 * will happened and no exception will be thrown.
	 * 
	 * @param friction
	 *            - the new friction; should be between 0 and 1 inclusive to
	 *            take effect.
	 */
	public void setFriction(double friction) {
		if (friction >= 0 && friction <= 1) {
			this.friction = friction;
		}
	}

	/**
	 * Set the body's average velocity.
	 * 
	 * @param v
	 *            the new velocity
	 */
	public void setVelocity(Vector v) {
		velocity = v;
	}

	/**
	 * Get the body's shape.
	 * 
	 * @return the shape
	 */
	public CollisionShape getShape() {
		return shape;
	}

	/**
	 * Step the body as if a certain amount of time has passed. This may move or
	 * rotate the body and perhaps perform other functions.
	 * 
	 * @param amount
	 *            how many time units to pass
	 */
	public void step(double amount) {
		if (!fixed) {
			shape.moveRotating(angularVelocity * amount, velocity
					.multiply(amount));
		} else {
			velocity = new Vector();
			angularVelocity = 0;
		}
	}

	/**
	 * Add an impulse to the object's center of mass.
	 * 
	 * @param imp
	 *            the impulse vector
	 */
	public void addImpulse(Vector imp) {
		velocity = velocity.add(imp.divide(mass));
	}

	/**
	 * Add an impulse to a certain absolute point on the object.
	 * 
	 * @param imp
	 *            the impulse vector
	 * @param origin
	 *            where the impulse originates
	 */
	public void addImpulse(Vector imp, Vector origin) {
		addImpulse(imp);
		Vector relOrigin = origin.subtract(shape.center());
		Vector tangent = relOrigin.quarterCounter();
		double torque = imp.dotProduct(tangent);
		angularVelocity += torque / (momentOfInertia);
	}

	/**
	 * Get the velocity at a certain absolute point on the object.
	 * 
	 * @param pos
	 *            the point to find the velocity at
	 * @return the velocity vector at pos
	 */
	public Vector velocityAt(Vector pos) {
		Vector relPos = pos.subtract(shape.center());
		Vector tanVel = relPos.quarterCounter().multiply(angularVelocity);
		return velocity.add(tanVel);
	}

	/**
	 * Get the inertia at a certain absolute point on the object. A body has
	 * less inertia when pushing it far from its center and tangent to it.
	 * 
	 * @param res
	 *            the collision result that represents how to get the inertia
	 * @return the inertia
	 */
	private double inertiaAt(CollisionResult res) {
		Vector relOrigin = res.contactPoint().subtract(shape.center());
		Vector tangent = relOrigin.quarterCounter().unit();
		double rad = relOrigin.magnitude();
		double sinTheta = Math
				.abs(res.translation().unit().dotProduct(tangent));
		double angular = sinTheta * sinTheta * rad * rad / (momentOfInertia);
		return 1 / (1 / mass + angular);
	}

	/**
	 * Perform a collision with another body.
	 * 
	 * @param other
	 *            the other body.
	 */
	public void collide(Body other) {
		CollisionResult res = shape.collide(other.shape);
		if (res != null) { // there is a collision
			Vector trans = res.translation();
			Vector contact = res.contactPoint();
			// unit vector towards the other body
			Vector towards = trans.withMagnitude(-1);
			// unit vector tangent to the collision
			Vector tangent = towards.quarterClockwise();
			// velocity relative to the other
			Vector relVel = velocityAt(contact).subtract(
					other.velocityAt(contact));
			// how much of this velocity is smashing the bodies together
			double vTowards = relVel.dotProduct(towards);
			// the contact point relative to the center
			Vector relContact = contact.subtract(shape.center());
			// the distance from the center to a point such that the line
			// containing
			// the point and the center and the line containing the point and
			// the
			// contact point are perpendicular, AND the line containing the
			// point
			// and the contact point overlaps with the translation vector
			double rad = trans.unit().dotProduct(relContact.quarterCounter());
			// the contact point relative to the other center
			Vector otherRelContact = contact.subtract(other.shape.center());
			// similar to rad, for the other object
			double otherRad = trans.unit().dotProduct(
					otherRelContact.quarterCounter());
			// this determines how much the objects repel each other
			double bouncyCoeff = 1 + bounciness * other.bounciness;
			// the magnitude of the impulse away
			// if bouncyCoeff is 2, this formula keeps Ek constant
			// that is how I derived it.
			double magImpulseAway = Math
					.abs(bouncyCoeff
							* vTowards
							/ (1 / mass + 1 / other.mass + rad * rad
									/ momentOfInertia + otherRad * otherRad
									/ other.momentOfInertia));
			// the impulse vector going away
			Vector impulseAway = trans.withMagnitude(magImpulseAway);
			// I have to add these impulses because they affect how friction is
			// calculated
			addImpulse(impulseAway, contact);
			// add opposite impulse to other object
			other.addImpulse(impulseAway.invert(), contact);
			// this is used to calculate inertia at the contact point
			// the translation is the tangent vector because friction is tangent
			// to the collision
			CollisionResult fricRes = new CollisionResult(contact, tangent);
			// calculate inertias
			double thisInertia = inertiaAt(fricRes);
			double otherInertia = other.inertiaAt(fricRes);
			double totalInertia = thisInertia + otherInertia;
			// The maximum magnitude of velocity of friction.
			double frictionVelocity = friction * other.friction
					* magImpulseAway / thisInertia;
			// I have to recalculate this because it may have changed after
			// bouncing.
			relVel = velocityAt(contact).subtract(other.velocityAt(contact));
			// This is the velocity at which the bodies are sliding against each
			// other
			// in the direction of the tangent unit vector. It is positive if
			// this body
			// is sliding with the tangent vector, negative if sliding against.
			double vTangent = 1 * otherInertia / totalInertia
					* relVel.dotProduct(tangent);
			double speedTangent = Math.abs(vTangent);
			// This test ensures that the tangential velocity difference is no
			// more than
			// canceled out. Without this check, weird things would happen.
			// For example, if an object had a very high friction number and
			// collided
			// nearly directly into the wall, it would shoot diagonally away
			// from the wall
			// and to the side.
			if (frictionVelocity > speedTangent) {
				frictionVelocity = speedTangent;
			}
			// Now I get the magnitude of the friction impulse.
			double magImpulseTan = frictionVelocity * thisInertia;
			// This ensures that the impulse will go opposite to tangential
			// movement.
			if (vTangent > 0)
				magImpulseTan = -magImpulseTan;
			// The impulse goes in the direction of or opposite to the tangent
			// vector.
			Vector impulseTan = tangent.multiply(magImpulseTan);
			// add these impulses...
			addImpulse(impulseTan, contact);
			other.addImpulse(impulseTan.invert(), contact);
			// This shifts the bodies to make sure they are no longer
			// overlapping.
			double totalMass = mass + other.mass;
			if (!fixed)
				shape.move(trans.multiply((totalMass - mass) / totalMass));
			if (!other.fixed)
				other.shape.move(trans.multiply(-mass / totalMass));
		}
	}

	/**
	 * Returns the default bounciness, which is the bounciness assigned to
	 * bodies without an otherwise-specified bounciness.
	 * 
	 * @return the default bounciness.
	 */
	public static double getDefaultBounciness() {
		return defaultBounciness;
	}

	/**
	 * Sets the default bounciness,which is the bounciness assigned to bodies
	 * without an otherwise-specified bounciness.
	 * 
	 * @param defaultBounciness
	 *            - the new default bounciness.
	 */
	public static void setDefaultBounciness(double defaultBounciness) {
		Body.defaultBounciness = defaultBounciness;
	}

	/**
	 * Returns the default friction, which is the friction assigned to bodies
	 * without an otherwise-specified friction.
	 * 
	 * @return the default friction.
	 */
	public static double getDefaultFriction() {
		return defaultFriction;
	}

	/**
	 * Sets the default friction, which is the friction assigned to bodies
	 * without an otherwise-specified friction.
	 * 
	 * @param defaultFriction
	 *            - the new default bounciness.
	 */
	public static void setDefaultFriction(double defaultFriction) {
		Body.defaultFriction = defaultFriction;
	}

	/**
	 * Returns the default density, which is the density assigned to bodies
	 * without an otherwise-specified density.
	 * 
	 * @return the default density.
	 */
	public static double getDefaultDensity() {
		return defaultDensity;
	}

	/**
	 * Sets the default density, which is the density assigned to bodies without
	 * an otherwise-specified density.
	 * 
	 * @param defaultDensity
	 *            - the new default density.
	 */
	public static void setDefaultDensity(double defaultDensity) {
		Body.defaultDensity = defaultDensity;
	}

	/**
	 * Changes this body's color. This does not fire any events; the change will
	 * only be actually visible to the user the next time stateChanged is fired.
	 * 
	 * @param color
	 *            the color to set.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns the color of this shape.
	 * 
	 * @return the color of this shape.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the type of this polygon, which is an <code>int</code> that
	 * corresponds to the body's shape.
	 * 
	 * @return an <code>int</code> corresponding to this body's shape.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the mass of the body.
	 * 
	 * @param mass
	 *            - the new mass of the body.
	 */
	public void setMass(Double mass) {
		this.mass = mass;

		this.density = mass / shape.area();

		// Notify the listeners:
		fireStateChanged();
	}

	/**
	 * Returns the mass of this body. The mass dictates how hard the body
	 * resists acceleration. On bodies created with the tools, the mass is
	 * usually dictated by the density * the area of the body.
	 * 
	 * @return the mass of the object.
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * Sets this body's density to a new value. The density dictates how much
	 * mass the object has depending on its area. Changing the density changes
	 * the mass.
	 * 
	 * @param density
	 *            - the new density to set.
	 */
	public void setDensity(double density) {
		this.density = density;
		setMass(density * shape.area());
	}

	/**
	 * Returns this body's density. The density dictates how much mass the
	 * object has depending on its area. Changing the density changes the mass.
	 * 
	 * @return the body's density.
	 */
	public double getDensity() {
		return density;
	}

	/**
	 * Returns the bounciness of this body. This is a number between 0 and 1
	 * that determines how much energy is lost when the body bounces. A value of
	 * 0 means that the body will not bounce; a value of 1 means the body will
	 * not lose any energy when bouncing and can therefore bounce forever.
	 * 
	 * @return the bounciness of this body.
	 */
	public double getBounciness() {
		return bounciness;
	}

	/**
	 * Returns the friction value of this object. This is a number between 0 and
	 * 1 that dictates how much energy is lost when the body contacts a
	 * different body.
	 * 
	 * @return the friction of this body.
	 */
	public double getFriction() {
		return friction;
	}

	/**
	 * Returns the current velocity of this object. This is a vector that
	 * combines the object's speed and direction.
	 * 
	 * @return the velocity of the object.
	 */
	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * Returns the angular velocity of the object in radians / time unit. This
	 * value dictates how fast an object is spinning.
	 * 
	 * @return the angular velocity of the object.
	 */
	public double getAngularVelocity() {
		return angularVelocity;
	}

	/**
	 * Returns the body's moment of inertia.
	 * 
	 * @return the body's moment of inertia.
	 */
	public double getMomentOfInertia() {
		return momentOfInertia;
	}

	/**
	 * Returns whether the object is fixed. A fixed object cannot move. Walls
	 * are fixed by default.
	 * 
	 * @return whether the object is fixed.
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Returns a <code>String</code> that corresponds to this <code>Body</code>
	 * in every way, and can be used to reconstruct it at a later date. The
	 * returned <code>String</code> is a pipe ("|") delimited set of numbers,
	 * surrounded with square brackets that mark the boundaries of the body's
	 * definition.
	 * 
	 * The order of the information that appears in the <code>String</code>:
	 * <ol>
	 * <li>Type, as defined by <code>getType()</code></li>
	 * <li>Shape -- a set of points that can yield a unique shape</li>
	 * <li>Mass, as defined by <code>getMass()</code></li>
	 * <li>Bounciness -- from <code>getBounciness()</code></li>
	 * <li>Friction -- from <code>getFriction()</code></li>
	 * <li>Velocity -- <code>getVelocity()</code></li>
	 * <li>Angular Velocity -- <code>getAngularVelocity()</code></li>
	 * <li>Moment of Inertia -- <code>getMomentOfInertia()</code></li>
	 * <li>Fixed -- whether the body is fixed. <code>1==true, 0==false</code>.
	 * <li>Color -- the body's color...</li>
	 * </ol>
	 * 
	 * @return the archive <code>String</code>.
	 */
	public String toArchiveString() {
		String archive = "[";

		archive += getType();
		// archive += getShape().toArchiveString();
		archive += getMass() + ";";
		archive += getBounciness() + ";";
		archive += getFriction() + ";";
		archive += getVelocity().getX() + ":" + getVelocity().getY() + ";";
		archive += getAngularVelocity() + ";";
		archive += getMomentOfInertia() + ";";
		archive += isFixed() ? 1 : 0 + ";";

		archive += getColor().getRed() + ";";
		archive += getColor().getGreen() + ";";
		archive += getColor().getBlue() + ";";
		archive += getColor().getAlpha();

		archive += "]";

		return archive;
	}

	/**
	 * Registers a {@link ChangeListener} with this body. The listener is
	 * notified whenever one of the body's properties changes.
	 * 
	 * @param listener
	 *            - the <code>ChangeListener</code> to add.
	 */
	public synchronized void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * Removes the specified {@link ChangeListener} from this body. If the
	 * listener is not registered to this body, nothing happens. If the listener
	 * is registered more than once, only the first instance is removed and the
	 * rest will continue to be notified.
	 * 
	 * @param listener
	 *            - the <code>ChangeListener</code> to remove.
	 */
	public synchronized void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}

	/**
	 * Returns an array of all the {@link ChangeListener}s registered to this
	 * body.
	 * 
	 * @return an array of all the <code>ChangeListener</code>s currently
	 *         registered to this body.
	 */
	public synchronized ChangeListener[] getChangeListeners() {
		return (ChangeListener[]) changeListeners.toArray();
	}

	/**
	 * Notifies all the registered listeners that the state of this <code>Body
	 * </code> has
	 * changed.
	 */
	protected synchronized void fireStateChanged() {
		synchronized (changeListeners) {
			ChangeEvent e = new ChangeEvent(this);
			for (ChangeListener listener : changeListeners) {
				listener.stateChanged(e);
			}
		}
	}
	
	@Override
	public String toString() {
		switch(type){
		case CIRCLE:
			return "Circle";
			
		case RECTANGLE:
			return "Rectangle";
			
		case WALL:
			return "Wall";
			
		case POLYGON:
		default:
			return "Polygon";
		}
	}
}
