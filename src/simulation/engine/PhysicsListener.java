package simulation.engine;

import java.util.EventListener;

/**
 * This interface should be implemented by any class that needs to monitor the
 * state of a System. It's primary function is to keep everything updated when
 * the System changes in some way; the use of a full event model rather than
 * some less stringent standard helps make the design more extensible -- it lets
 * multiple views be updated simultaneously with no need to know about each
 * other, as well as keeping the code neater, better-structured and easier to
 * understand.
 * 
 * @author Tikhon Jelvis
 * 
 */
public interface PhysicsListener extends EventListener {

	/**
	 * This method is fired when the simulation's state is changed. In general,
	 * a state change is any change that requires the simulation to be redrawn;
	 * however, other types of changes may appear in the future.
	 * 
	 * @param e
	 *            - the event, representing the change in state.
	 */
	void stateChanged(PhysicsEvent e);

}
