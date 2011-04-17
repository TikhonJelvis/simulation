package simulation.engine;

import java.awt.Graphics;
import java.awt.Shape;

/**
 * A shape that can be tested to see if it collides with another shape. There
 * are only circles and polygons.
 * 
 * @author Jacob Taylor
 * 
 */
public abstract class CollisionShape {
    /**
     * Tests if the shape contains a point.
     * 
     * @param point
     *            the point to test
     * @return whether or not the point is in the shape
     */
    public abstract boolean contains(Vector point);

    /**
     * Get the result of colliding with a circle.
     * 
     * @param other
     *            the circle
     * @return the result of the collision
     */
    public abstract CollisionResult collideCircle(CollisionCircle other);

    /**
     * Get the result of colliding with a polygon.
     * 
     * @param other
     *            the polygon
     * @return the result of the collision
     */
    public abstract CollisionResult collidePolygon(CollisionPolygon other);

    /**
     * Get the shape's area.
     * 
     * @return the area
     */
    public abstract double area();

    /**
     * Get the shape's center.
     * 
     * @return the center
     */
    public abstract Vector center();

    /**
     * Get the shape's moment of inertia.
     * 
     * @return the moment of inertia
     */
    public abstract double momentOfInertia();

    /**
     * Get the shape's rotation.
     * 
     * @return the rotation
     */
    public abstract double rotation();

    /**
     * Clone the shape.
     * 
     * @return the clone
     */
    @Override
    public abstract CollisionShape clone();

    /**
     * Draw the shape on a certain Graphics object.
     * 
     * @param g
     *            what to draw on
     */
    public void fill(Graphics g) {

    }

    /**
     * Rotate the shape a certain amount.
     * 
     * @param angle
     *            how much to rotate
     */
    public void rotate(double angle) {

    }

    /**
     * Move the shape a certain amount.
     * 
     * @param movement
     *            the displacement vector
     */
    public void move(Vector movement) {

    }

    /**
     * Move and rotate the shape. This may be faster than calling rotate and
     * move sequentially.
     * 
     * @param angle
     *            how much to rotate
     * @param movement
     *            how much to move
     */
    public void moveRotating(double angle, Vector movement) {
        rotate(angle);
        move(movement);
    }

    /**
     * Get the result of colliding the shape with another.
     * 
     * @param other
     *            the other shape
     * @return the result of the collision
     */
    public CollisionResult collide(CollisionShape other) {
        CollisionResult res;
        if (other instanceof CollisionCircle) {
            res = collideCircle((CollisionCircle) other);
        } else if (other instanceof CollisionPolygon) {
            res = collidePolygon((CollisionPolygon) other);
        } else {
            throw new IllegalArgumentException(
                    "Cannot collide with CollisionShape other than CollisionCircle or CollisionPolygon.");
        }
        if (res == null)
            return null;
        Vector trans = res.translation();
        if (trans.getX() == 0 && trans.getY() == 0) {
            // make the angle go away but be off slightly, to prevent shapes
            // sliding through each other
            double angle = center().subtract(other.center()).angle() + .001;
            return new CollisionResult(res.contactPoint(), Vector.fromAngle(
                    angle, .001));
        }
        return res;
    }

    /**
     * Converts to a java.awt.Shape
     * 
     * @return this converted to a shape
     */
    public abstract Shape toShape();
}
