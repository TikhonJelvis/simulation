package simulation.engine;

import java.util.EventObject;

/**
 * This represents an event in the System, which models the simulation. The
 * event encapsulates little information -- it is more or less a renamed
 * <code> EventObject </code> -- but it adheres to standards and leaves a clear
 * path towards certain optimizations of the simulation, like redrawing only the
 * affected area each time through a rectangle in the event.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class PhysicsEvent extends EventObject {

	/**
	 * Creates the event with the given <code>System</code> as the source.
	 * 
	 * @param source
	 *            - the <code>System</code> which fired the event.
	 */
	public PhysicsEvent(Simulation source) {
		super(source);
	}

}
