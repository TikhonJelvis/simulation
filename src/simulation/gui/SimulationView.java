package simulation.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;

import simulation.engine.Body;
import simulation.engine.CollisionCircle;
import simulation.engine.CollisionPolygon;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;
import simulation.engine.Vector;

/**
 * This is the main view which shows a visual representation of the simulation.
 * This serves as both a view and a controller -- it shows the user the
 * simulation as well as letting the user interact with the objects.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class SimulationView extends ViewPanel implements PhysicsListener {

	// Tool constants:
	public static final int NO_TOOL = 0;
	public static final int ADD_RECTANGLE_TOOL = 1;
	public static final int ADD_CIRCLE_TOOL = 2;
	public static final int ADD_POLYGON_TOOL = 3;

	// Tool resource locations:
	public static final String RECTANGLE_IMAGE_LOCATION = MainWindow.RESOURCE_PATH
			+ "rectangleTool.png";
	public static final String CIRCLE_IMAGE_LOCATION = MainWindow.RESOURCE_PATH
			+ "circleTool.png";
	public static final String POLYGON_IMAGE_LOCATION = MainWindow.RESOURCE_PATH
			+ "polygonTool.png";
	public static final String DEFAULT_CURSOR_LOCATION = MainWindow.RESOURCE_PATH
			+ "defaultCursor.png";
	public static final String TRACK_ICON_LOCATION = MainWindow.RESOURCE_PATH
			+ "track.png";
	public static final String FOLDER_ICON_LOCATION = MainWindow.RESOURCE_PATH
			+ "folder.png";
	private static final String DELETE_ICON_LOCATION = MainWindow.RESOURCE_PATH
			+ "delete.png";

	private MainWindow mainWindow;
	private Simulation model;

	protected JPopupMenu popupMenu;
	protected JMenuItem track;
	protected JMenuItem explore;
	protected JMenuItem delete;

	// Random number generator:
	protected Random random = new Random();

	// Tool information (selections, active tools...):
	private Body selectedBody;
	private int activeTool;

	// Tool resources (images, cursors):

	// Default (drag) tool:
	private ImageIcon defaultToolIcon;
	private Cursor defaultToolCursor;

	// Rectangle tool:
	private ImageIcon rectangleToolIcon;
	private Cursor rectangleToolCursor;

	// Circle tool:
	private ImageIcon circleToolIcon;
	private Cursor circleToolCursor;

	// Polygon tool:
	private ImageIcon polygonToolIcon;
	private Cursor polygonToolCursor;

	// The menu icons:
	private ImageIcon trackIcon;
	private ImageIcon folderIcon;
	private ImageIcon deleteIcon;

	// Drawing information:
	// Shapes defined by two points:
	private boolean drawGhost;
	private Shape ghost;// The shape being created; not always used.
	private Point ghostStart, ghostEnd;// The shape's start and end points.
	private Color ghostColor;

	// Zooming and moving the view:
	private double zoomFactor = 1.0;
	private int xOffset = 0;
	private int yOffset = 0;

	// Camera state events:
	private ArrayList<CameraListener> cameraListeners = new ArrayList<CameraListener>();

	/**
	 * Creates a view of the specified simulation.
	 * 
	 * @param model
	 *            - the model which this instance shows.
	 * @param mainWindow
	 *            - this model's parent main window.
	 */
	public SimulationView(final Simulation model, MainWindow mainWindow) {
		super(null);// No layout manager.

		setPreferredSize(getSize());// Helps with some layout managers.

		setOpaque(false);// Lets me create a custom background.
		setFocusable(true);// Lets this panel get KeyEvents.

		this.model = model;
		model.addPhysicsListener(this);

		this.mainWindow = mainWindow;

		// Tools:
		// The tool resources:

		// Default (drag) tool:
		defaultToolIcon = new ImageIcon(getClass().getResource(
				DEFAULT_CURSOR_LOCATION));
		defaultToolCursor = getToolkit().createCustomCursor(
				defaultToolIcon.getImage(), new Point(0, 0),
				"Default Tool Cursor");
		setCursor(defaultToolCursor);

		// Rectangle tool:
		rectangleToolIcon = new ImageIcon(getClass().getResource(
				RECTANGLE_IMAGE_LOCATION));
		rectangleToolCursor = getToolkit().createCustomCursor(
				rectangleToolIcon.getImage(), new Point(0, 0),
				"Rectangle Tool Cursor");

		// Circle tool:
		circleToolIcon = new ImageIcon(getClass().getResource(
				CIRCLE_IMAGE_LOCATION));
		circleToolCursor = getToolkit().createCustomCursor(
				circleToolIcon.getImage(), new Point(0, 0),
				"Circle Tool Cursor");

		// Polygon tool:
		polygonToolIcon = new ImageIcon(getClass().getResource(
				POLYGON_IMAGE_LOCATION));
		polygonToolCursor = getToolkit().createCustomCursor(
				polygonToolIcon.getImage(), new Point(0, 0),
				"Polygon Tool Cursor");

		// The pop-up menu:
		popupMenu = new JPopupMenu();

		trackIcon = new ImageIcon(getClass().getResource(TRACK_ICON_LOCATION));
		folderIcon = new ImageIcon(getClass().getResource(FOLDER_ICON_LOCATION));
		deleteIcon = new ImageIcon(getClass().getResource(DELETE_ICON_LOCATION));

		track = new JMenuItem("Track", trackIcon);
		track.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TrackWindow(model, selectedBody);
			}
		});
		popupMenu.add(track);

		explore = new JMenuItem("Explore", folderIcon);
		explore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimulationView.this.mainWindow.explorer.setVisible(true);
				SimulationView.this.mainWindow.explorer.goToBody(selectedBody);
			}
		});
		popupMenu.add(explore);

		popupMenu.addSeparator();

		delete = new JMenuItem("Delete", deleteIcon);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedBody.getType() != Body.WALL) {
					model.removeBody(selectedBody);
				} else {
					Polygon shape = (Polygon) selectedBody.getShape().toShape();
					int[] x = shape.xpoints;
					int[] y = shape.ypoints;
					if (x[0] == -20 && x[3] == -20 && y[0] == -23) {
						SimulationView.this.mainWindow.setNorthWall(false);
					} else if (x[0] == -20 && x[3] == -20) {
						SimulationView.this.mainWindow.setSouthWall(false);
					} else if (y[0] == -20 && y[1] == -20 && x[0] == -23) {
						SimulationView.this.mainWindow.setWestWall(false);
					} else {
						SimulationView.this.mainWindow.setEastWall(false);
					}
				}
			}
		});
		popupMenu.add(delete);

		// Keeping track of the mouse for tools:
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// Get the focus, if possible.:
				requestFocus();

				Point zoomed = e.getPoint();
				zoomed.x += getXOffset();
				zoomed.y += getYOffset();
				zoomed.x = (int) ((double) zoomed.x / zoomFactor);
				zoomed.y = (int) ((double) zoomed.y / zoomFactor);

				if (e.getButton() == MouseEvent.BUTTON1 && !e.isControlDown()) {
					switch (activeTool) {
					case ADD_RECTANGLE_TOOL:
						ghostStart = zoomed;
						ghostColor = new Color(random.nextInt(256), random
								.nextInt(256), random.nextInt(256), 0x66);
						setDrawGhost(true);
						break;

					case ADD_CIRCLE_TOOL:
						ghostStart = zoomed;
						ghostColor = new Color(random.nextInt(256), random
								.nextInt(256), random.nextInt(256), 0x66);
						setDrawGhost(true);
						break;

					case ADD_POLYGON_TOOL:
						ghostStart = zoomed;
						ghostColor = new Color(random.nextInt(256), random
								.nextInt(256), random.nextInt(256), 0x66);
						setDrawGhost(true);
						break;

					case NO_TOOL:
					default:
						model.pressMouse(zoomed);
					}
				}

				// Right click, with ugly-Mac support.
				else if ((e.getButton() == MouseEvent.BUTTON3)
						|| (e.getButton() == MouseEvent.BUTTON1 && e
								.isControlDown())) {
					Body body = model.bodyAt(new Vector(zoomed));

					selectedBody = body;
					if (selectedBody != null) {
						try {
							popupMenu
									.show(e.getComponent(), e.getX(), e.getY());
						} catch (IllegalComponentStateException ex) {
							popupMenu.show(e.getComponent(), 0, 0);
						}
					}
				}

				else {
					model.pressMouse(zoomed);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				model.releaseMouse();

				Point zoomed = e.getPoint();
				zoomed.x += getXOffset();
				zoomed.y += getYOffset();
				zoomed.x = (int) ((double) zoomed.x / zoomFactor);
				zoomed.y = (int) ((double) zoomed.y / zoomFactor);

				if (drawGhost) {
					ghostEnd = zoomed;
					addDrawnShape();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// If the window is focused:
				if (getRootPane().getParent().hasFocus()) {
					// Get the focus so that KeyEvents register properly.
					requestFocus();
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point zoomed = e.getPoint();
				zoomed.x += getXOffset();
				zoomed.y += getYOffset();
				zoomed.x = (int) ((double) zoomed.x / zoomFactor);
				zoomed.y = (int) ((double) zoomed.y / zoomFactor);

				model.moveMouse(zoomed.x, zoomed.y);

				if (drawGhost) {
					switch (getActiveTool()) {

					case ADD_RECTANGLE_TOOL:
						int[] xInts = { ghostStart.x, ghostStart.x, zoomed.x,
								zoomed.x };
						int[] yInts = { ghostStart.y, zoomed.y, zoomed.y,
								ghostStart.y };

						ghost = new Polygon(xInts, yInts, 4);
						break;

					case ADD_CIRCLE_TOOL:
						double x = ghostStart.x <= zoomed.x ? ghostStart.x
								: zoomed.x;
						double y = ghostStart.y <= zoomed.y ? ghostStart.y
								: zoomed.y;
						double width = Math.abs(ghostStart.x - zoomed.x);
						double height = Math.abs(ghostStart.y - zoomed.y);
						// Make both dimensions equal to the smaller one:
						width = width < height ? width : height;
						height = height < width ? height : width;

						ghost = new Ellipse2D.Double(x, y, width, height);
						break;

					case ADD_POLYGON_TOOL:
						/*
						 * Here we first find the ratios for x and the ratios
						 * for y between the two bounding boxes (one gotten via
						 * polygon.getBounds(), the other from the two points)
						 * and use that information to create a scaled version
						 * of the template polygon, by multiplying every single
						 * point in the original by the ratio.
						 */
						Polygon polygon = SimulationView.this.mainWindow.polygonPad
								.getPolygon();
						Rectangle bounds = polygon.getBounds();

						// px1 -- polygon x-coordinate 1
						double px1 = bounds.x;
						double px2 = bounds.x + bounds.width;
						// Abs not really needed, but can't hurt (too much).
						double pdx = Math.abs(px2 - px1);

						double py1 = bounds.y;
						double py2 = bounds.y + bounds.height;
						double pdy = Math.abs(py2 - py1);

						// Now for the new points and lengths:
						// nx1 -- new x-coordinate 1
						double nx1 = ghostStart.x;
						double nx2 = zoomed.x;
						double ndx = Math.abs(nx2 - nx1);

						double ny1 = ghostStart.y;
						double ny2 = zoomed.y;
						double ndy = Math.abs(ny2 - ny1);

						// Ratios:
						double xRatio = ndx / pdx;
						double yRatio = ndy / pdy;

						int[] newX = new int[polygon.npoints];
						int[] newY = new int[polygon.npoints];
						// Now we use the ratios:
						for (int i = 0; i < polygon.npoints; i++) {
							newX[i] = (int) (polygon.xpoints[i] * xRatio);
							newY[i] = (int) (polygon.ypoints[i] * yRatio);
						}

						Polygon newPolygon = new Polygon(newX, newY,
								polygon.npoints);

						// Now we have to translate the polygon:
						double goalX = x = ghostStart.x <= zoomed.x ? ghostStart.x
								: zoomed.x;
						double goalY = x = ghostStart.y <= zoomed.y ? ghostStart.y
								: zoomed.y;

						double currX = newPolygon.getBounds().x;
						double currY = newPolygon.getBounds().y;

						// These CAN be negative! (tdx = translate distance x).
						double tdx = goalX - currX;
						double tdy = goalY - currY;

						for (int i = 0; i < polygon.npoints; i++) {
							newX[i] += tdx;
							newY[i] += tdy;
						}

						ghost = new Polygon(newX, newY, polygon.npoints);

						break;

					case NO_TOOL:
					default:
						// Do nothing
					}
					repaint();
				}
			}
		});

		// Keeping track of the keyboard for tool functions (like esc to
		// cancel):
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyCode() + " " + KeyEvent.VK_ESCAPE);
				switch (e.getKeyCode()) {

				case KeyEvent.VK_ESCAPE:
					setActiveTool(NO_TOOL);
					break;

				default:
					// Do nothing, this probably isn't a real key anyway.
				}
			}
		});

		// Using the mouse wheel to zoom:
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// TODO mouse wheel support!
			}
		});

	}

	/**
	 * Lets the user add a rectangle to the simulation by clicking and dragging
	 * The add circle tool remains active until another tool is chosen or escape
	 * is pressed.
	 */
	public void addRectangle() {
		setActiveTool(ADD_RECTANGLE_TOOL);
	}

	/**
	 * Lets the user add a circle to the simulation by clicking and dragging.
	 * The add circle tool remains active until another tool is chosen or escape
	 * is pressed.
	 */
	public void addCircle() {
		setActiveTool(ADD_CIRCLE_TOOL);
	}

	/**
	 * Lets the user add a polygon to the simulation by clicking repeatedly. The
	 * add polygon tool remains active until another tool is chosen or escape is
	 * pressed. The polygon is created when enter is pressed.
	 */
	public void addPolygon() {
		if (mainWindow.polygonPad.getPolygon() == null) {
			mainWindow.polygonPad.setVisible(true);
		}

		else {
			setActiveTool(ADD_POLYGON_TOOL);
		}
	}

	private void addDrawnShape() {
		switch (getActiveTool()) {
		case ADD_RECTANGLE_TOOL:
			int[] x = { ghostStart.x, ghostStart.x, ghostEnd.x, ghostEnd.x };
			int[] y = { ghostStart.y, ghostEnd.y, ghostEnd.y, ghostStart.y };
			Polygon polygon = new Polygon(x, y, 4);
			CollisionPolygon toAdd = new CollisionPolygon(polygon);
			ghostColor = new Color(ghostColor.getRed(), ghostColor.getGreen(),
					ghostColor.getBlue());
			Body bodyToAdd = new Body(toAdd, ghostColor, Body.RECTANGLE);
			model.addBody(bodyToAdd);

			// Reset the ghost properties:
			setDrawGhost(false);
			break;

		case ADD_CIRCLE_TOOL:
			// Get the radius:
			double dx = Math.abs(ghostStart.x - ghostEnd.x);
			double dy = Math.abs(ghostStart.y - ghostEnd.y);
			// Make both distances equal to the smaller distance:
			dx = dx <= dy ? dx : dy;
			dy = dx <= dy ? dx : dy;

			double radius = Math.sqrt(dx * dx + dy * dy) / 2;

			double xCenter = dx / 2;
			// Add the smaller distance to it:
			xCenter += ghostStart.x < ghostEnd.x ? ghostStart.x : ghostEnd.x;
			double yCenter = dy / 2;
			yCenter += ghostStart.y < ghostEnd.y ? ghostStart.y : ghostEnd.y;

			Vector center = new Vector(xCenter, yCenter);
			CollisionCircle circle = new CollisionCircle(radius, center);

			ghostColor = new Color(ghostColor.getRed(), ghostColor.getGreen(),
					ghostColor.getBlue());

			bodyToAdd = new Body(circle, ghostColor, Body.CIRCLE);

			model.addBody(bodyToAdd);

			// Reset the ghost properties:
			setDrawGhost(false);
			break;

		case ADD_POLYGON_TOOL:
			if (ghost instanceof Polygon) {
				CollisionPolygon shape = new CollisionPolygon((Polygon) ghost);

				ghostColor = new Color(ghostColor.getRed(), ghostColor
						.getGreen(), ghostColor.getBlue());

				bodyToAdd = new Body(shape, ghostColor, Body.POLYGON);

				model.addBody(bodyToAdd);

				// Reset the ghost properties:
				setDrawGhost(false);
			}
			break;

		case NO_TOOL:
		default:
			// Do nothing.
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.translate(-getXOffset(), -getYOffset());
		g2d.scale(zoomFactor, zoomFactor);

		// Draw all the bodies:
		for (Body body : model.getBodies()) {
			g2d.setColor(body.getColor());
			Shape toDraw = body.getShape().toShape();
			g2d.fill(toDraw);
		}

		// If a ghost of a shape is to be drawn:
		if (drawGhost && ghost != null) {
			g2d.setColor(ghostColor);
			g2d.fill(ghost);
		}

		g2d.scale(1 / zoomFactor, 1 / zoomFactor);
		g2d.translate(getXOffset(), getYOffset());
	}

	@Override
	public void stateChanged(PhysicsEvent e) {
		repaint();

		boolean repaintParent = false;

		if (model.getWidth() != getWidth()) {
			setPreferredSize(new Dimension(model.getWidth(), getHeight()));
			repaintParent = true;
		}

		if (model.getHeight() != getHeight()) {
			setPreferredSize(new Dimension(getPreferredSize().width, model
					.getHeight()));
			repaintParent = true;
		}

		// Makes sure the scroll-bars reflect the correct size.
		if (getParent() != null) {
			synchronized (getParent()) {
				if (repaintParent) {
					if (getParent() instanceof JViewport) {
						((JViewport) getParent()).setView(this);
					}
				}
			}
		}
	}

	/**
	 * Sets which tool is active. The active tool dictates what happens when the
	 * mouse is clicked. Supplying any number that doesn't correspond to a tool
	 * is the same as supplying <code>NO_TOOL</code>.
	 * 
	 * @param activeTool
	 *            - the tool that is now active.
	 */
	public void setActiveTool(int activeTool) {
		this.activeTool = activeTool;

		// Change the cursor, if applicable:
		switch (activeTool) {
		case ADD_RECTANGLE_TOOL:
			setCursor(rectangleToolCursor);
			break;

		case ADD_CIRCLE_TOOL:
			setCursor(circleToolCursor);
			break;

		case ADD_POLYGON_TOOL:
			setCursor(polygonToolCursor);
			break;

		case NO_TOOL:
		default:
			setDrawGhost(false);
			setCursor(defaultToolCursor);
			break;
		}
	}

	// Gets rid of the ghost...
	private void setDrawGhost(boolean drawGhost) {
		if (drawGhost) {
			this.drawGhost = true;
		}

		else {
			this.drawGhost = false;
			ghost = null;
			ghostStart = null;
			ghostEnd = null;
			ghostColor = null;
			repaint();
		}
	}

	/**
	 * Returns the number that corresponds to the active tool. The active tool
	 * dictates what happens when the mouse is clicked.
	 * 
	 * @return the number corresponding to the active tool.
	 */
	public int getActiveTool() {
		return activeTool;
	}

	/**
	 * Changes the zoom factor of this view. The zoom factor describes how to
	 * scale the view relative to the simulation; a zoom factor of 1.0 means
	 * that it is identical in size, while a zoom factor of 2 means everything
	 * is twice as big and a zoom factor of 0.5 makes the view show twice as
	 * much.
	 * 
	 * @param zoomFactor
	 *            - the new zoom factor.
	 */
	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;

		fireZoomChanged();
		repaint();
	}

	/**
	 * Returns this view's zoom factor. The zoom factor describes how to scale
	 * the view relative to the simulation; a zoom factor of 1.0 means that it
	 * is identical in size, while a zoom factor of 2 means everything is twice
	 * as big and a zoom factor of 0.5 makes the view show twice as much.
	 * 
	 * @return this view's zoom factor.
	 */
	public double getZoomFactor() {
		return zoomFactor;
	}

	/**
	 * Changes the view's x-offset.
	 * 
	 * @param xOffset
	 *            - the new x-offset.
	 */
	public void setXOffset(int xOffset) {
		int dx = xOffset - getXOffset();

		if (dx > 0) {
			if (getXOffset() + getWidth() / getZoomFactor() <= model.getXMax()) {
				this.xOffset = xOffset;
			}
		} else {
			if (getXOffset() >= model.getXMin()) {
				this.xOffset = xOffset;
			}
		}

		repaint();
		fireLocationChanged();
	}

	/**
	 * Returns the view's x-offset.
	 * 
	 * @return the x-offset.
	 */
	public int getXOffset() {
		return xOffset;
	}

	/**
	 * Sets the view's y-offset.
	 * 
	 * @param yOffset
	 *            - the new y-offset.
	 */
	public void setYOffset(int yOffset) {
		int dy = yOffset - getYOffset();

		if (dy > 0) {
			if (getYOffset() + getHeight() / getZoomFactor() <= model.getYMax()) {
				this.yOffset = yOffset;
			}
		} else {
			if (getYOffset() >= model.getYMin()) {
				this.yOffset = yOffset;
			}
		}

		repaint();
		fireLocationChanged();
	}

	/**
	 * Returns the view's y-offset.
	 * 
	 * @return the y-offset
	 */
	public int getYOffset() {
		return yOffset;
	}

	/**
	 * Registers the specified listener with this view.
	 * 
	 * @param listener
	 *            - the listener to add.
	 */
	public synchronized void addCameraListener(CameraListener listener) {
		cameraListeners.add(listener);
	}

	/**
	 * Removes the specified listener. If it was registered more than once, only
	 * removes the first instance.
	 * 
	 * @param listener
	 *            - the listener to remove.
	 */
	public synchronized void removeCameraListener(CameraListener listener) {
		cameraListeners.remove(listener);
	}

	/**
	 * Returns an array of all the <code>CameraListener</code>s registered to
	 * this view.
	 * 
	 * @return an array of all the <code>CameraListener</code>s registered to
	 *         this view.
	 */
	public synchronized CameraListener[] getCameraListeners() {
		return (CameraListener[]) cameraListeners.toArray();
	}

	protected void fireZoomChanged() {
		CameraEvent e = new CameraEvent(this);
		for (CameraListener listener : cameraListeners) {
			listener.zoomChanged(e);
		}
	}

	protected void fireLocationChanged() {
		CameraEvent e = new CameraEvent(this);
		for (CameraListener listener : cameraListeners) {
			listener.locationChanged(e);
		}
	}

}
