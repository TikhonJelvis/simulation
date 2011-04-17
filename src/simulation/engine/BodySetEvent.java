package simulation.engine;

import java.util.EventObject;

/**
 * Represents the change in the set of bodies in a <code>Simulation</code>. The
 * event encapsulates both the <code>Simulation</code> that fired it and the
 * <code>Body</code> that was either added or removed.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class BodySetEvent extends EventObject {

	private final Body body;

	/**
	 * Creates a <code>BodySetEvent</code> that represents a change in the set
	 * of bodies in a <code>Simulation</code>. The event object encapsulates its
	 * source as well as the body in question.
	 * 
	 * @param source
	 *            - the <code> Simulation </code> that fired this event.
	 * @param body
	 *            - the body that was either added or removed.
	 */
	public BodySetEvent(Simulation source, Body body) {
		super(source);

		this.body = body;
	}

	/**
	 * Returns the body that was either removed or added to cause this event.
	 * 
	 * @return the body that was either removed or added to cause this event.
	 */
	public Body getBody() {
		return body;
	}

}
