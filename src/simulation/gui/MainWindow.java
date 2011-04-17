package simulation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import simulation.engine.Body;
import simulation.engine.CollisionCircle;
import simulation.engine.CollisionPolygon;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;
import simulation.engine.Vector;

/**
 * This is the main window of the simulation; it contains the main display, and
 * while not necessarily encompassing everything -- there can be some
 * independent frames -- it is the window that is opened first. It is also the
 * class the constructs the model; all the other classes refer to and use this
 * model and do not construct it themselves.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	/**
	 * The string that corresponds to the version of the program. This version
	 * is incorporated into the title of the window.
	 */
	public static final String VERSION = "Version 1.0.3";

	/**
	 * This is the relative file path to the resource folder to use with the
	 * <code>Class.getResource()</code> method.
	 */
	public static final String RESOURCE_PATH = "/simulation/resources/";

	// The toolkit for basic system operations like getting the screen size.
	private Toolkit toolkit = getToolkit();

	/**
	 * This is the model which represents the objects being simulated.
	 */
	protected Simulation model;

	// Walls:
	private Color wallColor = Color.black;
	private Body northWall;
	private Body southWall;
	private Body westWall;
	private Body eastWall;

	/**
	 * The pseudorandom number generator used to generate all the needed random
	 * numbers for the window of the simulation. The random numbers are used
	 * primarily by the presets.
	 */
	protected Random random = new Random();

	protected ShapeExplorer explorer;

	protected DefaultsEditor defaultsEditor;

	protected AboutWindow about;

	protected JFrame polygonPadFrame;
	protected PolygonPad polygonPad;

	protected SimulationView modelView;

	protected JScrollPane menuHolder;
	protected SimulationMenu menu;

	protected SimulationStatusBar statusBar;

	// Icons for tools and other things:
	private ArrayList<BufferedImage> iconImages;

	private ImageIcon dragIcon;
	private ImageIcon rectangleToolIcon;
	private ImageIcon circleToolIcon;
	private ImageIcon polygonToolIcon;
	private ImageIcon polygonPadIcon;
	private ImageIcon wallToolIcon;

	private ImageIcon folderIcon;
	private ImageIcon pageIcon;

	// The split pane which structures the window:
	protected JSplitPane holder;

	// The menu bar and menus:
	protected JMenuBar bar;

	protected JMenu file;
	protected JMenuItem file_reset;
	protected JMenuItem file_exit;

	protected JMenu tools;
	protected JMenuItem tools_selection;
	protected JMenuItem tools_explore;
	protected JMenuItem tools_defaults;
	protected JMenuItem tools_addRectangle;
	protected JMenuItem tools_addCircle;
	protected JMenuItem tools_addPolygon;
	protected JMenuItem tools_changePolygon;

	protected JMenu tools_walls;
	protected JMenuItem tools_walls_all;
	protected JMenuItem tools_walls_north;
	protected JMenuItem tools_walls_south;
	protected JMenuItem tools_walls_east;
	protected JMenuItem tools_walls_west;

	protected JMenu presets;
	protected JMenuItem presets_fallingCirlces;
	protected JMenuItem presets_rectCollision;
	protected JMenuItem presets_shootingCircle;

	protected JMenu help;
	protected JMenuItem help_about;

	protected SimulationToolBar toolBar;

	protected Timer presetsTimer = new Timer();

	/**
	 * Creates a new instance of the simulation.
	 */
	public MainWindow() {
		super("2D Shape Collision Simulation");

		// Some preliminary settings:
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = toolkit.getScreenSize();
		setSize((int) (screenSize.width * 0.9), (int) (screenSize.height * 0.9));
		setLocationRelativeTo(null);// Centers the window.

		dragIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "dragIcon.png"));
		rectangleToolIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "rectangleIcon.png"));
		circleToolIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "circleIcon.png"));
		polygonToolIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "polygonIcon.png"));
		polygonPadIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "polygonPadIcon.png"));
		wallToolIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "wallIcon.png"));

		folderIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "folder.png"));
		pageIcon = new ImageIcon(getClass().getResource(
				RESOURCE_PATH + "leaf.png"));

		// Get the icons:
		String iconPaths[] = { RESOURCE_PATH + "logoSmall.png",
				RESOURCE_PATH + "logo.png", RESOURCE_PATH + "logoMedium.png",
				RESOURCE_PATH + "logoLarge.png" };

		iconImages = new ArrayList<BufferedImage>();

		for (String path : iconPaths) {
			try {
				File location = new File(getClass().getResource(path).toURI());
				iconImages.add(ImageIO.read(location));
			} catch (URISyntaxException e) {
				setIconImage(rectangleToolIcon.getImage());
			} catch (IOException e) {
				setIconImage(rectangleToolIcon.getImage());
			}
			if (iconImages.size() > 0) {
				setIconImages(iconImages);
			}
		}

		// The about window:
		about = new AboutWindow((ArrayList<Image>) getIconImages());

		// The menu bar:
		bar = new JMenuBar();
		setJMenuBar(bar);

		file = new JMenu("File");
		file.setMnemonic('F');
		bar.add(file);

		file_reset = new JMenuItem("Reset");
		file_reset.setMnemonic('R');
		file_reset.setAccelerator(KeyStroke.getKeyStroke("control R"));
		file_reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		file.add(file_reset);

		file_exit = new JMenuItem("Exit");
		file_exit.setMnemonic('E');
		file_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		file.add(file_exit);

		tools = new JMenu("Tools");
		tools.setMnemonic('T');
		bar.add(tools);

		tools_selection = new JMenuItem("Drag Shapes", dragIcon);
		tools_selection.setMnemonic('D');
		tools_selection.setAccelerator(KeyStroke.getKeyStroke("control D"));
		tools_selection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drag();
			}
		});
		tools.add(tools_selection);

		tools.addSeparator();

		tools_explore = new JMenuItem("Explore shapes", folderIcon);
		tools_explore.setMnemonic('E');
		tools_explore.setAccelerator(KeyStroke.getKeyStroke("control E"));
		tools_explore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				explorer.setVisible(true);
			}
		});
		tools.add(tools_explore);

		tools.addSeparator();

		tools_defaults = new JMenuItem("Change Defaults", pageIcon);
		tools_defaults.setMnemonic('C');
		tools_defaults.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				defaultsEditor.setVisible(true);
			}
		});
		tools.add(tools_defaults);

		tools_addRectangle = new JMenuItem("Add Rectangle", rectangleToolIcon);
		tools_addRectangle.setMnemonic('R');
		tools_addRectangle.setAccelerator(KeyStroke.getKeyStroke("control R"));
		tools_addRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRectangle();
			}
		});
		tools.add(tools_addRectangle);

		tools_addCircle = new JMenuItem("Add Circle", circleToolIcon);
		tools_addCircle.setMnemonic('C');
		tools_addCircle.setAccelerator(KeyStroke.getKeyStroke("control C"));
		tools_addCircle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addCircle();
			}
		});
		tools.add(tools_addCircle);

		tools_addPolygon = new JMenuItem("Add Polygon", polygonToolIcon);
		tools_addPolygon.setMnemonic('P');
		tools_addPolygon.setAccelerator(KeyStroke.getKeyStroke("control P"));
		tools_addPolygon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addPolygon();
			}
		});
		tools.add(tools_addPolygon);

		tools_changePolygon = new JMenuItem("Change Polygon", polygonPadIcon);
		tools_changePolygon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changePolygon();
			}
		});
		tools.add(tools_changePolygon);

		tools.addSeparator();

		tools_walls = new JMenu("Walls");
		tools_walls.setIcon(wallToolIcon);
		tools_walls.setMnemonic('W');

		tools_walls_all = new JCheckBoxMenuItem("All");
		tools_walls_all.setMnemonic('A');
		tools_walls_all.setAccelerator(KeyStroke.getKeyStroke("control A"));
		tools_walls_all.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetWalls();
			}
		});
		tools_walls.add(tools_walls_all);

		tools_walls.addSeparator();

		tools_walls_north = new JCheckBoxMenuItem("Top", northWall != null);
		tools_walls_north.setMnemonic('T');
		tools_walls_north.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setNorthWall(tools_walls_north.isSelected());
			}
		});
		tools_walls.add(tools_walls_north);

		tools_walls_south = new JCheckBoxMenuItem("Bottom", southWall != null);
		tools_walls_south.setMnemonic('B');
		tools_walls_south.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSouthWall(tools_walls_south.isSelected());
			}
		});
		tools_walls.add(tools_walls_south);

		tools_walls_east = new JCheckBoxMenuItem("Right", eastWall != null);
		tools_walls_east.setMnemonic('R');
		tools_walls_east.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEastWall(tools_walls_east.isSelected());
			}
		});
		tools_walls.add(tools_walls_east);

		tools_walls_west = new JCheckBoxMenuItem("Left", westWall != null);
		tools_walls_west.setMnemonic('L');
		tools_walls_west.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setWestWall(tools_walls_west.isSelected());
			}
		});
		tools_walls.add(tools_walls_west);

		tools.add(tools_walls);

		presets = new JMenu("Presets");
		presets.setMnemonic('P');
		bar.add(presets);

		presets_fallingCirlces = new JMenuItem("Falling Circles");
		presets_fallingCirlces.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startFallingCircles();
			}
		});
		presets.add(presets_fallingCirlces);

		presets_rectCollision = new JMenuItem("Rectangle Collisions");
		presets_rectCollision.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startRectCollision();
			}
		});
		presets.add(presets_rectCollision);

		presets_shootingCircle = new JMenuItem("Shooting Circle");
		presets_shootingCircle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startShootingCircles();
			}
		});
		presets.add(presets_shootingCircle);

		help = new JMenu("Help");
		help.setMnemonic('H');
		bar.add(help);

		help_about = new JMenuItem("About");
		help_about.setMnemonic('A');
		help_about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				about.setVisible(true);
			}
		});
		help.add(help_about);

		toolBar = new SimulationToolBar(this);
		add(toolBar, BorderLayout.WEST);

		// Create the simulation:
		model = new Simulation();// This is the model!

		// The defaults editor:
		defaultsEditor = new DefaultsEditor(model);

		// The shape explorer:
		explorer = new ShapeExplorer(model);

		// Set the walls up:
		setNorthWall(true);
		setSouthWall(true);
		setEastWall(true);
		setWestWall(true);

		// Create the main model view:
		modelView = new SimulationView(getModel(), this);

		// Polygon pad:
		polygonPad = new PolygonPad(modelView);

		// Creates the menu:
		menu = new SimulationMenu(getModel(), this);
		menuHolder = new JScrollPane(menu);

		// Create the separator that will structure the window:
		holder = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, modelView,
				menuHolder);
		holder.setOneTouchExpandable(true);
		holder.setDividerLocation(getWidth() - 225);
		holder.setResizeWeight(1.0);
		add(holder);

		model.addPhysicsListener(new PhysicsListener() {
			@Override
			public void stateChanged(PhysicsEvent e) {
				if (!model.getWallColor().equals(getWallColor())) {
					setWallColor(model.getWallColor());
				}
			}
		});

		statusBar = new SimulationStatusBar(model, this);
		add(statusBar, BorderLayout.SOUTH);

		setVisible(true);// Shows the previously-hidden window.
	}

	/**
	 * Resets the model and the timer used by the presets. This does not change
	 * the play status -- if the simulation was playing before the call to this
	 * method, it will continue playing.
	 */
	protected void reset() {
		// The presets' timer:
		presetsTimer.cancel();
		presetsTimer = new Timer();

		// The walls:
		setNorthWall(false);
		setSouthWall(false);
		setEastWall(false);
		setWestWall(false);

		modelView.setXOffset(0);
		modelView.setYOffset(0);

		menu.stop();

		// The model:
		model.reset();
	}

	protected void addCircle() {
		modelView.addCircle();
	}

	protected void addRectangle() {
		modelView.addRectangle();
	}

	protected void addPolygon() {
		modelView.addPolygon();
	}

	protected void changePolygon() {
		polygonPad.setVisible(true);
	}

	protected void drag() {
		modelView.setActiveTool(SimulationView.NO_TOOL);
	}

	/**
	 * Starts the preset simulation "Falling Circles". The preset consists of
	 * randomly created circles above the top of the view falling down onto the
	 * walls and floor. All the circles are close in color. This method also
	 * resets the simulation.
	 */
	protected void startFallingCircles() {
		reset();
		model.setGravity(1);
		setSouthWall(true);
		setEastWall(true);
		setWestWall(true);

		final Color baseColor = new Color(random.nextInt(176) + 80, random
				.nextInt(176) + 80, random.nextInt(176) + 80);

		TimerTask createCircle = new TimerTask() {
			@Override
			public void run() {
				double radius = Math.random() * 100;
				Vector center = new Vector(Math.random()
						* (modelView.getWidth() - 200) + 100, -2 * radius);
				CollisionCircle circle = new CollisionCircle(radius, center);

				Color circleColor = new Color(baseColor.getRed()
						+ random.nextInt(40) - 40, baseColor.getGreen()
						+ random.nextInt(40) - 40, baseColor.getBlue()
						+ random.nextInt(40) - 40);

				Body toAdd = new Body(circle, Math.random() * 50, circleColor,
						Body.CIRCLE);

				model.addBody(toAdd);
			}
		};

		menu.play();

		presetsTimer.scheduleAtFixedRate(createCircle, 0, 1000);
	}

	private CollisionPolygon makeRectangle(double x, double y, double width,
			double height, double rotation) {
		double cs = Math.cos(rotation);
		double sn = Math.sin(rotation);
		Vector p1 = new Vector(x, y);
		Vector p2 = new Vector(x + cs * width, y - sn * width);
		Vector p3 = new Vector(x + cs * width + sn * height, y + cs * height
				- sn * width);
		Vector p4 = new Vector(x + sn * height, y + cs * height);
		return new CollisionPolygon(new Vector[] { p1, p2, p3, p4 });
	}

	protected void startRectCollision() {
		reset();
		model.setGravity(0);
		for (int i = 0; i < 3; ++i) {
			double height = 30;
			double width = 50;
			double y = 50 + 150 * i;
			Body rect1 = new Body(makeRectangle(10, y, width, height, 0), 10,
					Body.RECTANGLE);
			Body rect2 = new Body(makeRectangle(150, y + 10 * (i + 1), width,
					height, i * Math.PI / 4), 10, Body.RECTANGLE);
			rect1.setVelocity(new Vector(10, 0));
			model.addBody(rect1);
			model.addBody(rect2);
		}
		menu.play();
	}

	protected void startShootingCircles() {
		reset();
		model.setGravity(1);
		setSouthWall(true);
		setWestWall(true);
		setEastWall(true);

		CollisionCircle[] circles = {
				new CollisionCircle(10, new Vector(30, 100)),
				new CollisionCircle(8, new Vector(30, 82)),
				new CollisionCircle(6, new Vector(30, 68)),
				new CollisionCircle(4, new Vector(30, 58)) };

		for (CollisionCircle circle : circles) {
			model.addBody(new Body(circle));
		}

		menu.play();
	}

	/**
	 * Returns the simulation that this window shows.
	 * 
	 * @return this window's simulation.
	 */
	public Simulation getModel() {
		return model;
	}

	/**
	 * Sets all the walls to either exist or not; ignores the walls' current
	 * states.
	 * 
	 * @param allWalls
	 *            - whether the walls should exist or not.
	 */
	public void setAllWalls(boolean allWalls) {
		setNorthWall(allWalls);
		setSouthWall(allWalls);
		setEastWall(allWalls);
		setWestWall(allWalls);
	}

	/**
	 * Sets whether the north wall (ceiling) is enabled. If it already is, this
	 * method does nothing. To reset the north wall, one must first set it to
	 * <code>false</code>.
	 * 
	 * @param north
	 *            - whether the north wall should be enabled.
	 */
	public void setNorthWall(boolean north) {
		if (modelView != null) {

			if (north && northWall == null) {
				int[] x = { -20, modelView.getWidth() + 20,
						modelView.getWidth() + 20, -20 };
				int[] y = { -23, -23, 2, 2 };

				CollisionPolygon polygon = new CollisionPolygon(x, y, 4);

				northWall = new Body(polygon, 1000, wallColor, Body.WALL);
				northWall.setFixed(true);

				model.addBody(northWall);

				tools_walls_north.setSelected(true);
			}

			else if (!north) {
				model.removeBody(northWall);
				northWall = null;

				tools_walls_north.setSelected(false);
			}

			if (allWallsExist()) {
				tools_walls_all.setSelected(true);
			} else {
				tools_walls_all.setSelected(false);
			}
		}
	}

	/**
	 * Sets whether the south wall (floor) is enabled. If it already is, this
	 * method does nothing. To reset the south wall, one must first set it to
	 * <code>false</code>.
	 * 
	 * @param south
	 *            - whether the south wall should be enabled.
	 */
	public void setSouthWall(boolean south) {
		if (modelView != null) {

			if (south && southWall == null) {
				int[] x = { -20, modelView.getWidth() + 20,
						modelView.getWidth() + 20, -20 };
				int[] y = { modelView.getHeight() - 2,
						modelView.getHeight() - 2, modelView.getHeight() + 23,
						modelView.getHeight() + 23 };

				CollisionPolygon polygon = new CollisionPolygon(x, y, 4);

				southWall = new Body(polygon, 1000, wallColor, Body.WALL);
				southWall.setFixed(true);

				model.addBody(southWall);

				tools_walls_south.setSelected(true);
			}

			else if (!south) {
				model.removeBody(southWall);
				southWall = null;

				tools_walls_south.setSelected(false);
			}

			if (allWallsExist()) {
				tools_walls_all.setSelected(true);
			} else {
				tools_walls_all.setSelected(false);
			}
		}
	}

	/**
	 * Sets whether the west wall (right) is enabled. If it already is, this
	 * method does nothing. To reset the west wall, one must first set it to
	 * <code>false</code>.
	 * 
	 * @param west
	 *            - whether the west wall should be enabled.
	 */
	public void setWestWall(boolean west) {
		if (modelView != null) {

			if (west && westWall == null) {
				int[] x = { -23, 2, 2, -23 };
				int[] y = { -20, -20, modelView.getHeight() + 20,
						modelView.getHeight() + 20 };

				CollisionPolygon polygon = new CollisionPolygon(x, y, 4);

				westWall = new Body(polygon, 1000, wallColor, Body.WALL);
				westWall.setFixed(true);

				model.addBody(westWall);

				tools_walls_west.setSelected(true);
			}

			else if (!west) {
				model.removeBody(westWall);
				westWall = null;

				tools_walls_west.setSelected(false);
			}

			if (allWallsExist()) {
				tools_walls_all.setSelected(true);
			} else {
				tools_walls_all.setSelected(false);
			}
		}
	}

	/**
	 * Sets whether the east wall (right) is enabled. If it already is, this
	 * method does nothing. To reset the east wall, one must first set it to
	 * <code>false</code>.
	 * 
	 * @param east
	 *            - whether the east wall should be enabled.
	 */
	public void setEastWall(boolean east) {
		if (modelView != null) {

			if (east && eastWall == null) {

				int[] x = { modelView.getWidth() - 2,
						modelView.getWidth() + 23, modelView.getWidth() + 23,
						modelView.getWidth() - 2 };
				int[] y = { -20, -20, modelView.getHeight() + 20,
						modelView.getHeight() + 20 };

				CollisionPolygon polygon = new CollisionPolygon(x, y, 4);

				eastWall = new Body(polygon, 1000, wallColor, Body.WALL);
				eastWall.setFixed(true);

				model.addBody(eastWall);

				tools_walls_east.setSelected(true);
			}

			else if (!east) {
				model.removeBody(eastWall);
				eastWall = null;

				tools_walls_east.setSelected(false);
			}

			if (allWallsExist()) {
				tools_walls_all.setSelected(true);
			} else {
				tools_walls_all.setSelected(false);
			}
		}
	}

	/**
	 * Resets the walls -- if not all the walls exist, it creates all the walls
	 * and if all of the walls do exist, it gets rid of all the walls.
	 */
	public void resetWalls() {
		if (allWallsExist()) {
			setAllWalls(false);
		} else {
			setAllWalls(true);
		}
	}

	/**
	 * Returns whether all the walls exist, or more formally <code>
	 * (northWall != null && southWall != null && 
	 * eastWall != null && westWall != null) </code>.
	 * 
	 * @return whether all the walls exist.
	 */
	public boolean allWallsExist() {
		return northWall != null && southWall != null && eastWall != null
				&& westWall != null;
	}

	/**
	 * Sets a new {@link Color} for the walls. The walls immediately turn this
	 * color, and any new walls are going to be created in this color.
	 * 
	 * @param wallColor
	 *            - the walls' new color.
	 */
	public void setWallColor(Color wallColor) {
		this.wallColor = wallColor;
		model.setWallColor(wallColor);

		if (northWall != null) {
			northWall.setColor(wallColor);
		}
		if (southWall != null) {
			southWall.setColor(wallColor);
		}
		if (eastWall != null) {
			eastWall.setColor(wallColor);
		}
		if (westWall != null) {
			westWall.setColor(wallColor);
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

	// TODO finish!
	public void export(File location) throws IOException {
		FileWriter out = new FileWriter(location);
		out.write(model.toArchiveString());
		out.close();
	}
}
