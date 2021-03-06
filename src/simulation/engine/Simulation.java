package simulation.engine;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A system of objects.
 * 
 * @author Jacob Taylor
 * 
 */
public class Simulation {

    // Constants:
    public static final double DEFAULT_GRAVITY = 0;

    private List<Body> bodies;// Always use this one.

    private ArrayList<Body> bodiesArrayList;// Do not use! Ever!

    private List<Spring> springs;// Always use this one.

    private ArrayList<Spring> springsArrayList;// Do not even think of this one!

    // Some options:
    private double gravity;

    private Color wallColor = Color.black;

    // Event listeners:
    private ArrayList<PhysicsListener> physicsListeners = new ArrayList<PhysicsListener>();

    private ArrayList<BodySetListener> bodySetListeners = new ArrayList<BodySetListener>();

    // Is the simulation playing?
    private boolean playing;

    // Provides the lock for synchronizing the shape dragging.
    private Object grabLock = new Object();

    private int mouseX, mouseY;

    private BodyConnection grabbed;

    /**
     * Creates a new simulation with the default settings and nothing in it.
     */
    public Simulation() {
        bodiesArrayList = new ArrayList<Body>();
        bodies = Collections.synchronizedList(bodiesArrayList);
        springsArrayList = new ArrayList<Spring>();
        springs = Collections.synchronizedList(springsArrayList);

        setGravity(DEFAULT_GRAVITY);
    }

    public void moveMouse(int mousex, int mousey) {
        mouseX = mousex;
        mouseY = mousey;
    }

    public void pressMouse(int mousex, int mousey) {
        moveMouse(mousex, mousey);
        Vector mouse = new Vector(mousex, mousey);
        Body body = bodyAt(mouse);
        if (body != null) {
            synchronized (grabLock) {
                grabbed = new BodyConnection(body, mouse);
            }
        }
    }

    public void pressMouse(Point point) {
        pressMouse(point.x, point.y);
    }

    public void releaseMouse() {
        grabbed = null;
    }

    /**
     * Get the number of bodies.
     * 
     * @return the number of bodies
     */
    public int numBodies() {
        return bodies.size();
    }

    /**
     * Get the body at a certain index.
     * 
     * @param i
     *            the index
     * @return the body at that index
     */
    public Body getBody(int i) {
        return bodies.get(i);
    }

    /**
     * Get the bodies as an array.
     * 
     * @return the bodies
     */
    public Body[] getBodies() {
        synchronized (bodies) {
            return bodies.toArray(new Body[0]);
        }
    }

    /**
     * Get the number of springs in the system.
     * 
     * @return the number of springs
     */
    public int numSprings() {
        return springs.size();
    }

    /**
     * Get the spring at a certain index
     * 
     * @param i
     *            the index
     * @return the spring
     */
    public Spring getSpring(int i) {
        return springs.get(i);
    }

    /**
     * Get the springs as an array.
     * 
     * @return the springs
     */
    public Spring[] getSprings() {
        return springs.toArray(new Spring[0]);
    }

    /**
     * Step the system for a certain amount of time.
     * 
     * @param amount
     *            the number of time units to step the system
     */
    protected void step(double amount) {
        // collide every body with every other body
        // but don't do reverse collisions
        // e.g. don't do both b.collide(a) and a.collide(b)
        synchronized (bodies) {
            for (int i = 0; i < bodies.size(); ++i) {
                Body b1 = bodies.get(i);
                for (int j = i + 1; j < bodies.size(); ++j) {
                    Body b2 = bodies.get(j);
                    b1.collide(b2);
                }
            }
        }

        synchronized (bodies) {
            // step all bodies
            for (Body b : bodies) {
                b.step(amount);
                // gravity
                b.addImpulse(new Vector(0, 1 * amount * b.mass() * gravity));
            }
        }

        synchronized (springs) {
            // step all springs
            for (Spring s : springs) {
                s.step(amount);
            }
        }

        synchronized (grabLock) {
            if (grabbed != null) {
                Vector mouse = new Vector(mouseX, mouseY);
                Vector diff = mouse.subtract(grabbed.connectedPoint());
                Vector result = diff.multiply(10 * amount);
                grabbed.addImpulse(result);
                grabbed
                        .addImpulse(grabbed.getVelocity()
                                .multiply(-10 * amount));
            }
        }
    }

    /**
     * Steps the simulation <code>times</code> times for the specified amount of
     * time each time. All this does is call <code>step(amount)</code> more than
     * once with only one event (at the end).
     * 
     * @param amount
     *            - the number of time units for each step.
     * @param times
     *            - the number of times to step.
     */
    public void step(double amount, int times) {
        for (int i = 0; i < times; i++) {
            step(amount / times);
        }

        // Notify all the listeners that something has happened:
        fireStateChanged();
    }

    /**
     * Get a body at a certain point, or null if no body is there.
     * 
     * @param point
     *            the point to get the body at
     * @return the body at the point, or null if there are none there
     */
    public Body bodyAt(Vector point) {
        synchronized (bodies) {
            // find the first body that contains the point
            for (Body b : bodies) {
                if (b.getShape().contains(point)) {
                    return b;
                }
            }
        }

        return null;
    }

    /**
     * Adds a body to the simulation.
     * 
     * @param body
     *            - the body to add.
     */
    public void addBody(Body body) {
        if (body.getMass() > 0) {
            bodies.add(body);

            // Notify the listeners; something has changed:
            fireStateChanged();

            // Notify the BodySetListeners; the set of bodies has been added to:
            fireBodyAdded(body);
        }
    }

    /**
     * Removes the specified body. If more than one instance of the specified
     * body exists in the simulation, it only removes the first one.
     * 
     * @param body
     *            - the body to remove.
     */
    public void removeBody(Body body) {
        bodies.remove(body);

        // Notify the PhysicsListeners; something has changed:
        fireStateChanged();

        // Notify the BodySetListeners; the set of bodies has been removed from:
        fireBodyRemoved(body);
    }

    /**
     * Add a spring to the system.
     * 
     * @param s
     *            the spring to add
     */
    public void addSpring(Spring s) {
        springs.add(s);

        // Notify the listeners; something has changed:
        fireStateChanged();
    }

    // Event handling:
    /**
     * Registers a new <code>PhysicsListener</code> for this model. This
     * listener will be notified when the model's state changes. While no
     * guarantee of order strictly exists, the current implementation does
     * maintain a set order of listeners.
     * 
     * @param listener
     *            - the listener to register.
     */
    public synchronized void addPhysicsListener(PhysicsListener listener) {
        physicsListeners.add(listener);
    }

    /**
     * Returns an array of all the physics listeners currently registered to
     * this model.
     * 
     * @return an array of <code>PhysicsListener</code>s.
     */
    public synchronized PhysicsListener[] getListeners() {
        return (PhysicsListener[]) physicsListeners.toArray();
    }

    /**
     * Removes the specified listener. If the listener is not registered to this
     * model, nothing happens. If the listener is registered twice, then only
     * one instance is removed and the listener will still be notified at least
     * once.
     * 
     * @param listener
     *            - the <code>PhysicsListener</code> to remove.
     */
    public synchronized void removePhysicsListener(PhysicsListener listener) {
        physicsListeners.remove(listener);
    }

    /**
     * Calls <code>stateChanged</code> in every registered listener on this
     * model. The event passed contains <code>this</code> as the source.
     */
    protected synchronized void fireStateChanged() {
        PhysicsEvent e = new PhysicsEvent(this);
        for (PhysicsListener listener : physicsListeners) {
            listener.stateChanged(e);
        }
    }

    /**
     * Registers a new <code>BodySetListener</code> for this simulation. This
     * listener will be notified when a <code>Body</code> is either added or
     * removed from the simulation. While no guarantee of order strictly exists,
     * the current implementation does maintain a set order of listeners.
     * 
     * @param listener
     *            - the listener to register.
     */
    public synchronized void addBodySetListener(BodySetListener listener) {
        bodySetListeners.add(listener);
    }

    /**
     * Returns an array of all the <code>BodySetListener</code>s currently
     * registered to this <code>Simulation</code>.
     * 
     * @return an array of all the <code>BodySetListener</code>s currently
     *         registered to this <code>Simulation</code>.
     */
    public synchronized BodySetListener[] getBodySetListeners() {
        return (BodySetListener[]) bodySetListeners.toArray();
    }

    /**
     * Removes the specified listener. If the listener is not registered to this
     * <code>Simulation</code>, nothing happens. If the listeners is registered
     * twice, it is only removed once and will still continue to be notified at
     * least once.
     * 
     * @param listener
     *            - the listener to remove.
     */
    public synchronized void removeBodySetListener(BodySetListener listener) {
        bodySetListeners.remove(listener);
    }

    /**
     * Invokes the <code>bodyAdded</code> method of every registered listener,
     * with the specified <code>Body</code> specified to the event along with
     * <code>this</code> specified as the source.
     * 
     * @param body
     *            - the body specified to the <code>BodySetEvent</code>.
     */
    protected synchronized void fireBodyAdded(Body body) {
        BodySetEvent e = new BodySetEvent(this, body);

        for (BodySetListener listener : bodySetListeners) {
            listener.bodyAdded(e);
        }
    }

    /**
     * Invokes the <code>bodyRemoved</code> method of every registered listener,
     * with the specified <code>Body</code> specified to the event along with
     * <code>this</code> specified as the source.
     * 
     * @param body
     *            - the body specified to the <code>BodySetEvent</code>.
     */
    protected synchronized void fireBodyRemoved(Body body) {
        BodySetEvent e = new BodySetEvent(this, body);

        for (BodySetListener listener : bodySetListeners) {
            listener.bodyRemoved(e);
        }
    }

    /**
     * Sets whether this simulation is "playing." Playing generally means that
     * the simulation is being stepped repeatedly to animate it; when it is
     * playing, most tools (like shape adding/moving) are probably going to be
     * off.
     * 
     * @param playing
     *            - whether the simulation is "playing."
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;

        // Notify all the listeners:
        fireStateChanged();
    }

    /**
     * Tells whether this simulations is "playing."Playing generally means that
     * the simulation is being stepped repeatedly to animate it; when it is
     * playing, most tools (like shape adding/moving) are probably going to be
     * off.
     * 
     * @return whether the simulation is "playing."
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Sets the gravity force to a new value. This value dictates how hard the
     * gravity pulls on various objects; a value of 0 means to no gravity and a
     * value <0 means to anti-gravity.
     * 
     * @param gravity
     *            - the new gravity to set. If it is <code>null</code> then
     *            there is no gravity.
     */
    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    /**
     * Returns the current strength of the gravity.This value dictates how hard
     * the gravity pulls on various objects; a value of 0 means no gravity and a
     * value <0 means anti-gravity.
     * 
     * @return the gravity
     */
    public double getGravity() {
        return gravity;
    }

    /**
     * Resets the simulation, getting rid of all the bodies and springs. This
     * method fires stateChanged after clearing the two lists.
     */
    public void reset() {

        // Clear bodies and call all the events properly:
        int max = bodies.size();
        synchronized (bodies) {
            for (int i = 0; i < max; i++) {
                fireBodyRemoved(bodies.remove(0));
            }
        }
        springs.clear();

        // Makes sure everything is restarted pretty completely:
        bodiesArrayList = new ArrayList<Body>();
        bodies = Collections.synchronizedList(bodiesArrayList);
        springsArrayList = new ArrayList<Spring>();
        springs = Collections.synchronizedList(springsArrayList);

        fireStateChanged();
    }

    /**
     * Returns the width of this simulation, that is the farthest limit of any
     * shape that represents a body.
     * 
     * @return the "width" of the simulation.
     */
    public int getWidth() {
        return getXMax() - getXMin();
    }

    /**
     * Returns the height of this simulation. The height of the simulation is
     * the distance between the top of the top-most shape and the bottom of the
     * bottom-most shape.
     * 
     * @return the "height" of the simulation.
     */
    public int getHeight() {
        return getYMax() - getYMin();
    }

    /**
     * Returns how far the camera must move to get the left-most shape.
     * 
     * @return the x-offset.
     */
    public int getXMin() {
        synchronized (bodies) {
            int min = 0;

            for (Body body : bodies) {
                Rectangle bounds = body.getShape().toShape().getBounds();

                if (bounds.x < min) {
                    min = bounds.x;
                }
            }

            return min;
        }
    }

    /**
     * Returns how far the camera must move to get the lowest shape.
     * 
     * @return - the y-offset.
     */
    public int getYMin() {
        synchronized (bodies) {
            int min = 0;

            for (Body body : bodies) {
                Rectangle bounds = body.getShape().toShape().getBounds();

                if (bounds.y < min) {
                    min = bounds.y;
                }
            }

            return min;
        }
    }

    public int getXMax() {
        synchronized (bodies) {
            int max = 0;

            for (Body body : bodies) {
                Rectangle bounds = body.getShape().toShape().getBounds();

                if ((bounds.x + bounds.width) > max) {
                    max = bounds.x + bounds.width;
                }
            }

            return max;
        }
    }

    public int getYMax() {
        synchronized (bodies) {
            int max = 0;

            for (Body body : bodies) {
                Rectangle bounds = body.getShape().toShape().getBounds();

                if ((bounds.y + bounds.height) > max) {
                    max = bounds.y + bounds.height;
                }
            }

            return max;
        }
    }

    public String toArchiveString() {
        String archive = "";
        archive += getGravity() + ",";
        synchronized (bodies) {
            for (Body body : bodies) {
                archive += body.toArchiveString();
            }
        }
        return null;
    }

    /**
     * Sets the walls' color. If the color supplied is <code>null</code>, then
     * nothing happens and no exception is thrown.
     * 
     * @param wallColor
     *            - the walls' new color.
     */
    public void setWallColor(Color wallColor) {
        if (wallColor != null) {
            this.wallColor = wallColor;
            fireStateChanged();
        }
    }

    /**
     * Returns the walls' color.
     * 
     * @return the walls' color.
     */
    public Color getWallColor() {
        return wallColor;
    }

    /**
     * Returns whether the specified body is contained within this system.
     * 
     * @param body
     *            - the body to check for.
     * @return whether the specified body exists.
     */
    public boolean containsBody(Body body) {
        return bodies.contains(body);
    }

    /**
     * Returns the number of bodies this simulation currently has.
     * 
     * @return the number of bodies this simulation currently has.
     */
    public int getBodyCount() {
        return bodies.size();
    }

    /**
     * Updates the model -- all this really does is fire an event to make sure
     * everything using the model knows it has been updated.
     */
    public void update() {
        fireStateChanged();
    }
}
