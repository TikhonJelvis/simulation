package simulation.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import simulation.engine.Body;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;

@SuppressWarnings("serial")
public class TrackWindow extends JFrame {
	
	protected ShapeTracker tracker;
	
	public TrackWindow(final Simulation model, final Body body) {
		super(body.toString());
		
		tracker = new ShapeTracker(model, body);
		add(tracker);
		
		setSize(200, 200);
		setLocationRelativeTo(null);

		setIconImage(createImageFromBody(body));
		model.addPhysicsListener(new PhysicsListener() {
			@Override
			public void stateChanged(PhysicsEvent e) {
				setIconImage(createImageFromBody(body));
			}
		});
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	private BufferedImage createImageFromBody(Body body) {
		Shape shape = body.getShape().toShape();
		Rectangle bounds = shape.getBounds();

		BufferedImage image = new BufferedImage(19, 19,
				BufferedImage.TYPE_INT_ARGB);

		double maxSide = bounds.height > bounds.width ? bounds.height
				: bounds.width;
		double ratio = 19 / maxSide;

		Graphics2D ig = image.createGraphics();
		ig.setColor(body.getColor());
		ig.scale(ratio, ratio);
		ig.translate(-bounds.x, -bounds.y);
		ig.fill(shape);
		ig.dispose();

		return image;
	}
}
