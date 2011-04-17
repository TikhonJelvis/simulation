package simulation.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import simulation.engine.Body;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;

@SuppressWarnings("serial")
public class CameraMap extends JPanel {

	private Simulation model;
	private SimulationView modelView;

	private double ratio;

	private Rectangle scaledView;

	private Point dragStart;

	public CameraMap(final Simulation model, final SimulationView modelView) {
		this.model = model;
		this.modelView = modelView;

		setOpaque(false);
		setPreferredSize(getPreferredSize());
		setDoubleBuffered(true);

		model.addPhysicsListener(new PhysicsListener() {
			@Override
			public void stateChanged(PhysicsEvent e) {
				repaint();
			}
		});

		modelView.addCameraListener(new CameraListener() {
			@Override
			public void locationChanged(CameraEvent e) {
				repaint();

			}

			@Override
			public void zoomChanged(CameraEvent e) {
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (scaledView != null && scaledView.contains(e.getPoint())) {
					dragStart = e.getPoint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dragStart = null;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (dragStart != null) {
					double dx = e.getX() - dragStart.x;
					double dy = e.getY() - dragStart.y;

					dx /= getRatio();
					dy /= getRatio();

					modelView.setXOffset(modelView.getXOffset() + (int) dx);
					modelView.setYOffset(modelView.getYOffset() + (int) dy);

					dragStart = e.getPoint();
				}
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		double xMin = model.getXMin() < modelView.getXOffset() ? model
				.getXMin() : modelView.getXOffset();
		double yMin = model.getYMin() < modelView.getYOffset() ? model
				.getYMin() : modelView.getYOffset();
		xMin = xMin < 0 ? xMin : 0;
		yMin = yMin < 0 ? yMin : 0;

		g2d.scale(getRatio(), getRatio());
		g2d.translate(-model.getXMin(), -model.getYMin());

		for (Body body : model.getBodies()) {
			g2d.setColor(body.getColor());
			Shape toDraw = body.getShape().toShape();
			g2d.fill(toDraw);
		}

		g2d.translate(model.getXMin(), model.getYMin());
		g2d.scale(1 / getRatio(), 1 / getRatio());

		scaledView = getView();
		scaledView.x += model.getXMin() < 0 ? -model.getXMin() : 0;
		scaledView.x *= getRatio();
		scaledView.y += model.getYMin() < 0 ? -model.getYMin() : 0;
		scaledView.y *= getRatio();
		scaledView.width /= modelView.getZoomFactor();
		scaledView.height /= modelView.getZoomFactor();
		scaledView.width *= getRatio();
		scaledView.height *= getRatio();

		g2d.setColor(new Color(0x33, 0x66, 0xFF, 0x66));
		g2d.fill(scaledView);
		g2d.setColor(new Color(0x33, 0x66, 0xFF));
		g2d.draw(scaledView);
	}

	private double getRatio() {
		double viewWidth = modelView.getWidth() / modelView.getZoomFactor();
		double viewHeight = modelView.getHeight() / modelView.getZoomFactor();
		int viewXMax = (int) (viewWidth + modelView.getXOffset());
		int viewYMax = (int) (viewHeight + modelView.getYOffset());
		int viewXMin = modelView.getXOffset();
		int viewYMin = modelView.getYOffset();

		int xMin = model.getXMin();
		int xMax = model.getXMax();
		int yMin = model.getYMin();
		int yMax = model.getYMax();

		//Gets the more extreme value in each case.
		xMin = xMin < viewXMin ? xMin : viewXMin;
		yMin = yMin < viewYMin ? yMin : viewYMin;
		xMax = xMax > viewXMax ? xMax : viewXMax;
		yMax = yMax > viewYMax ? xMax : viewYMax;

		int width = xMax - xMin;
		int height = yMax - yMin;

		width = (int) (width > viewWidth ? width : viewWidth);
		height = (int) (height > viewHeight ? height : viewWidth);

		if (width > height) {
			ratio = ((double) getWidth()) / width;
		} else {
			ratio = ((double) getHeight()) / height;
		}

		return ratio;
	}

	private Rectangle getView() {
		Rectangle view = modelView.getBounds();
		view.x = modelView.getXOffset();
		view.y = modelView.getYOffset();
		return view;
	}
}
