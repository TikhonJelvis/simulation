package simulation.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * A circle that has a center and radius.
 * 
 * @author Jacob Taylor
 * 
 */
public final class CollisionCircle extends CollisionShape {
	private double radius;
	private Vector center;
	private double area;
	// how much it is rotated. This is for drawing and spring-related purposes.
	private double rotation;
	private double momentOfInertia;

	/**
	 * Create a circle with a given radius and center.
	 * 
	 * @param radius
	 *            the radius
	 * @param center
	 *            the center
	 */
	public CollisionCircle(double radius, Vector center) {
		this.radius = radius;
		this.center = center;
		this.area = Math.PI * radius * radius;
		// integral from 0 to r of 2*pi*r^3
		// 2*pi*r^3 is the circumference of the circle at that radius times r^2
		this.momentOfInertia = Math.PI * .5 * Math.pow(radius, 4);
	}

	/**
	 * Get the circle's radius
	 * 
	 * @return the radius
	 */
	public double radius() {
		return radius;
	}

	@Override
	public Vector center() {
		return center;
	}

	@Override
	public double rotation() {
		return rotation;
	}

	@Override
	public void rotate(double amount) {
		rotation = rotation + amount % (2 * Math.PI);
	}

	@Override
	public void move(Vector movement) {
		center = center.add(movement);
	}

	@Override
	public double area() {
		return area;
	}

	@Override
	public double momentOfInertia() {
		return momentOfInertia;
	}

	@Override
	public boolean contains(Vector point) {
		return point.subtract(center).magnitude() < radius;
	}

	@Override
	public CollisionResult collideCircle(CollisionCircle other) {
		Vector change = center.subtract(other.center);
		double dist = change.magnitude();
		double radii = radius + other.radius;
		// how much must they be move apart?
		double mag = radii - dist;
		if (mag <= 0) {
			return null;
		}
		Vector trans = change.withMagnitude(mag);
		// the contact point is the point in the middle of the overlap
		Vector relContact = change
				.withMagnitude((other.radius + dist - radius) / 2);
		return new CollisionResult(other.center.add(relContact), trans);
	}

	@Override
	public CollisionResult collidePolygon(CollisionPolygon other) {
		// collide other with this, then invert if there is a collision
		CollisionResult trans = other.collideCircle(this);
		if (trans == null)
			return null;
		return trans.invert();
	}

	@Override
	public CollisionShape clone() {
		return new CollisionCircle(radius, center);
	}

	@Override
	public String toString() {
		return "(center " + center + ", radius " + radius + ")";
	}

	@Override
	public void fill(Graphics g) {
		int diam = (int) (2 * radius);
		// first draw the circle
		g.fillOval((int) (center.getX() - radius), (int) (center.getY() - radius),
				diam, diam);
		g.setColor(Color.green);
		// now draw a green line from the center to the edge based on rotation
		g.drawLine((int) center.getX(), (int) center.getY(), (int) (center.getX() + Math
				.cos(rotation)
				* radius), (int) (center.getY() + Math.sin(rotation) * radius));
	}

	@Override
	public Shape toShape() {
		double diam = radius * 2;
		return new Ellipse2D.Double(center.getX() - radius, center.getY() - radius,
				diam, diam);
	}

	/*
	 * @Override public String toArchiveString() { String archive = "<";
	 * 
	 * archive += "c/"; archive += rotation + "/"; archive += momentOfInertia +
	 * "/"; archive += center.x() + "/"; archive += center.y() + "/"; archive +=
	 * radius;
	 * 
	 * archive += ">";
	 * 
	 * return archive; }
	 */
}
