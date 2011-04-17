package simulation.engine;

import java.util.EventListener;

/**
 * This listener is notified when the set of bodies in the simulation changes,
 * whether a body is added or taken away. The two methods each correspond to the
 * two possible cases : addition or removal. This should be fired once per body.
 * 
 * @author Tikhon Jelvis
 * 
 */
public interface BodySetListener extends EventListener {

	/**
	 * Invoked when a body is added to the simulation.
	 */
	void bodyAdded(BodySetEvent e);

	/**
	 * Invoked when a body is removed from the simulation.
	 */
	void bodyRemoved(BodySetEvent e);
}
