package simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import simulation.engine.Body;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;

/**
 * A panel that shows a view of the simulation centered on the specified body.
 * It can be used anywhere a JPanel could, giving it some versatility. The body
 * that the view is tracking can be changed after creation. If the body is
 * <code>null</code>, then the view will show nothing whatsoever.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class ShapeTracker extends ViewPanel {

	private Body body;
	private Simulation model;
	private Rectangle bodyBounds;

	private int viewWidth;
	private int viewHeight;

	double ratio;

	/**
	 * Creates a tracker with no specified body. It will show nothing at all
	 * until a body is specified through the <code>setBody</code> method.
	 * 
	 * @param model
	 *            - the model in which the view will later track some body.
	 */
	public ShapeTracker(Simulation model) {
		this(model, null);
	}

	/**
	 * Creates a tracker tracking the specified body in the specified
	 * simulation. If the simulation does not contain the body, then the view
	 * will simply show the wrong simulation but in the right place. If the
	 * specified body is <code>null</code>, then the view will show nothing
	 * whatsoever.
	 * 
	 * @param body
	 *            - the body that the view will be tracking. If the body is
	 *            null, then the view will show nothing at all.
	 * @param model
	 *            - the model in which the body is tracked. If the body is not
	 *            in the model, the view will still show the model at the right
	 *            location; the body will simply not be there.
	 */
	public ShapeTracker(Simulation model, Body body) {
		this.model = model;
		setBody(body);
		setPreferredSize(new Dimension());
		setOpaque(false);

		model.addPhysicsListener(new PhysicsListener() {
			@Override
			public void stateChanged(PhysicsEvent e) {
				repaint();
			}
		});
	}

	/**
	 * Changes the body the view is tracking. If the specified body is null, the
	 * view will show nothing at all.
	 * 
	 * @param body
	 *            - the new body for this system to track.
	 */
	public void setBody(Body body) {
		this.body = body;
		if (body != null) {
			bodyBounds = body.getShape().toShape().getBounds();
			viewWidth = 3 * bodyBounds.width;
			viewHeight = 3 * bodyBounds.height;
		}

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		if (body != null) {
			Rectangle bounds = body.getShape().toShape().getBounds();
			int shapeXOffset = (int) (((double) (viewWidth - bounds.width)) / 2d);
			int shapeYOffset = (int) (((double) (viewHeight - bounds.height)) / 2d);

			if (viewWidth > viewHeight) {
				ratio = ((double) getWidth()) / ((double) viewWidth);
			} else {
				ratio = ((double) getHeight()) / ((double) viewHeight);
			}

			g2d.scale(ratio, ratio);
			g2d.translate(-(bounds.x - shapeXOffset),
					-(bounds.y - shapeYOffset));

			for (Body body : model.getBodies()) {
				Shape toDraw = body.getShape().toShape();
				
				if (body != this.body) {
					Color color = body.getColor();
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();
					
					float[] hsb = Color.RGBtoHSB(red, green, blue, null);
					
					color = Color.getHSBColor(hsb[0], 0.01f, hsb[2]);
					
					g2d.setColor(color);
					g2d.fill(toDraw);
				} else {
					Color color = body.getColor();
					
					g2d.setColor(color);
					g2d.fill(toDraw);
					
				}
			}
		}
	}
}
