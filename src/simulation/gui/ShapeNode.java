package simulation.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import simulation.engine.Body;

/**
 * This is a specialized tree node that represents a body in the simulation.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class ShapeNode extends DefaultMutableTreeNode {

	private Body body;

	/**
	 * Creates a shape node with the supplied title and body. The title will be
	 * displayed to the user; the body determines what will happened when the
	 * node is selected, where the node will go and what picture the node will
	 * have.
	 * 
	 * @param body
	 *            - the body this node represents.
	 */
	public ShapeNode(Body body) {
		super(body);
		
		this.body = body;
	}

	/**
	 * Returns the <code>Body</code> that this node represents.
	 * 
	 * @return the <code>Body</code> that this node represents.
	 */
	public Body getBody() {
		return body;
	}

}
