package simulation.engine;

import java.awt.Point;

/**
 * A 2-dimensional vector containing an x and y coordinate.
 * 
 * @author Jacob Taylor
 * 
 */
public final class Vector {
	private double x, y;

	/**
	 * Creates a vector with the x and y coordinates of the given point.
	 * 
	 * @param p
	 *            - the point that encapsulates the x and y coordinates.
	 */
	public Vector(Point p) {
		this(p.x, p.y);
	}

	/**
	 * Create a vector with the given x and y coordinates.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a vector with both x and y equal to 0.
	 */
	public Vector() {
	}

	/**
	 * Constructs a new <code>Vector</code> based on the supplied <code>String
	 * </code>. The
	 * <code>String</code> should be one created by the
	 * <code> toArchiveString()</code>; if it isn't erratic behavior and
	 * possible exceptions could result.
	 * 
	 * @param string
	 *            - the archive string from which to create the new object.
	 */
	public Vector(String string) {
		String[] parts = string.split("\\D");

		this.x = Double.valueOf(parts[0]);
		this.y = Double.valueOf(parts[1]);
	}

	/**
	 * Create a vector from an angle and magnitude.
	 * 
	 * @param angle
	 *            the vector's angle
	 * @param mag
	 *            the vector's magnitude; if negative, vector is reversed
	 * @return the resulting vector
	 */
	public static Vector fromAngle(double angle, double mag) {
		return new Vector(mag * Math.cos(angle), mag * Math.sin(angle));
	}

	/**
	 * Create a unit vector from an angle.
	 * 
	 * @param angle
	 *            the vector's angle
	 * @return the resulting unit vector
	 */
	public static Vector fromAngle(double angle) {
		return fromAngle(angle, 1);
	}

	/**
	 * Get the vector's x coordinate.
	 * 
	 * @return the x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get the vector's y coordinate.
	 * 
	 * @return the y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Get the vector's angle in radians.
	 * 
	 * @return the angle in radians
	 */
	public double angle() {
		return Math.atan2(y, x);
	}

	/**
	 * Get the vector's slope.
	 * 
	 * @return the slope
	 */
	public double slope() {
		return y / x;
	}

	/**
	 * Get the vector's magnitude.
	 * 
	 * @return the magnitude
	 */
	public double magnitude() {
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Add the vector to another vector. This can be used to translate the
	 * vector by a certain amount.
	 * 
	 * @param other
	 *            the vector to add
	 * @return the sum of the vectors
	 */
	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}

	/**
	 * Subtract another vector from this. This can be used to get the vector
	 * from the other to this.
	 * 
	 * @param other
	 *            the vector to subtract
	 * @return the difference of the vectors
	 */
	public Vector subtract(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}

	/**
	 * Get the dot product of the vectors. This can be used to find how much the
	 * vector is going in a certain direction.
	 * 
	 * @param other
	 *            the vector to multiply by
	 * @return the dot product
	 */
	public double dotProduct(Vector other) {
		return x * other.x + y * other.y;
	}

	/**
	 * Get the dot product of this and a unit vector with the given angle.
	 * 
	 * @param angle
	 *            the angle of the unit vector
	 * @return the dot product
	 */
	public double dotProductUnit(double angle) {
		return x * Math.cos(angle) + y * Math.sin(angle);
	}

	/**
	 * Multiply the vector by a ratio.
	 * 
	 * @param ratio
	 *            what to multiply it by
	 * @return the magnified vector
	 */
	public Vector multiply(double ratio) {
		return new Vector(ratio * x, ratio * y);
	}

	/**
	 * Invert the vector so its coordinates are negated.
	 * 
	 * @return the inverted vector
	 */
	public Vector invert() {
		return new Vector(-x, -y);
	}

	/**
	 * Divide the vector by a ratio
	 * 
	 * @param ratio
	 *            what to divide it by
	 * @return the reduced vector
	 */
	public Vector divide(double ratio) {
		return new Vector(x / ratio, y / ratio);
	}

	/**
	 * Get a vector with the same angle as this with a certain magnitude. If the
	 * magnitude is negative, the vector will have an inverted angle and have a
	 * magnitude equal to mag's absolute value.
	 * 
	 * @param mag
	 *            the new magnitude
	 * @return the resulting vector
	 */
	public Vector withMagnitude(double mag) {
		double current = magnitude();
		if (current == 0) {
			return new Vector(mag, 0);
		} else {
			return multiply(mag / current);
		}
	}

	/**
	 * Get a unit vector with the same angle as this.
	 * 
	 * @return a unit vector
	 */
	public Vector unit() {
		return divide(magnitude());
	}

	/**
	 * Get a vector with the same magnitude as this with a certain angle.
	 * 
	 * @param angle
	 *            the angle of the resulting vector
	 * @return the resulting vector
	 */
	public Vector withAngle(double angle) {
		return fromAngle(angle, magnitude());
	}

	/**
	 * Rotate the vector by a certain angle.
	 * 
	 * @param angle
	 *            how much to rotate the vector
	 * @return the resulting vector
	 */
	public Vector rotate(double angle) {
		return fromAngle(angle + angle(), magnitude());
	}

	/**
	 * Rotate the vector pi/2 radians clockwise.
	 * 
	 * @return the resulting vector
	 */
	public Vector quarterClockwise() {
		return new Vector(y, -x);
	}

	/**
	 * Rotate the vector pi/2 radians counterclockwise.
	 * 
	 * @return the resulting vector
	 */
	public Vector quarterCounter() {
		return new Vector(-y, x);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * Determine if two vectors have equal coordinates.
	 * 
	 * @param other
	 *            the other vector
	 * @return whether or not the coordinates are equal
	 */
	public boolean equals(Vector other) {
		return x == other.x && y == other.y;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Vector && equals((Vector) other);
	}

	@Override
	public int hashCode() {
		return (new Double(x).hashCode() << 1) + new Double(y).hashCode();
	}
}
