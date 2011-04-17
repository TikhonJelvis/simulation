package simulation.gui;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import simulation.engine.Body;
import simulation.engine.BodySetEvent;
import simulation.engine.BodySetListener;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;

/**
 * This class is a window that lets the user browse through a list of bodies in
 * the simulation and edit some of their properties like mass or color.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class ShapeExplorer extends JFrame {

	private ImageIcon icon;

	private Simulation model; // The actual simulation this will explore.

	protected JSplitPane view; // The pane that holds all this window's content.

	protected JTree shapeList; // The list of all the shapes a user can browse
								// through.
	protected DefaultTreeModel treeModel;// The shapeList's model.

	protected DefaultMutableTreeNode topNode; // The top node of the shape tree.
	protected DefaultMutableTreeNode circles;
	protected DefaultMutableTreeNode rectangles;
	protected DefaultMutableTreeNode polygons;
	protected DefaultMutableTreeNode walls;

	protected JScrollPane shapeListHolder; // The scroll pane that contains
											// shapeList.

	protected ShapeView shapeView; // This will show the selected shape.
	protected JScrollPane shapeViewHolder; // The scroll pane that contains the
											// shapeView.

	/**
	 * Creates a new, hidden shape explorer that the user can use to browse
	 * through and edit the bodies of the given simulation.
	 * 
	 * @param model
	 *            - the simulation that this explorer shows and edits. When this
	 *            model's state is changed, this explorer will be updated.
	 */
	public ShapeExplorer(final Simulation model) {
		super("Shape Expolorer");
		this.model = model;

		icon = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "folder.png"));
		setIconImage(icon.getImage());

		// Create the list of shapes:
		createNodes();

		shapeList = new JTree(treeModel) {
			{
				model.addPhysicsListener(new PhysicsListener() {
					@Override
					public void stateChanged(PhysicsEvent e) {
						repaint();
					}
				});
			}
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		shapeList.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		shapeList.setShowsRootHandles(true);
		shapeListHolder = new JScrollPane(shapeList);

		shapeList.setCellRenderer(new ShapeNodeRenderer());

		shapeView = new ShapeView(model);
		shapeViewHolder = new JScrollPane(shapeView);

		view = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				shapeListHolder, shapeViewHolder);
		view.setOneTouchExpandable(true);
		view.setDividerLocation(150);
		view.setResizeWeight(0.0);
		add(view);

		model.addBodySetListener(new BodySetListener() {
			@Override
			public void bodyAdded(BodySetEvent e) {
				addBodyNode(e.getBody());
			}

			@Override
			public void bodyRemoved(BodySetEvent e) {
				removeBodyNode(e.getBody());
			}
		});

		shapeList.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (shapeList.getLastSelectedPathComponent() instanceof ShapeNode) {
					ShapeNode node = (ShapeNode) shapeList
							.getLastSelectedPathComponent();
					Body body = node.getBody();
					shapeView.setBody(body);
				}

				else {
					shapeView.setBody(null);
				}
			}
		});

		shapeList.setFocusable(true);
		shapeList.addKeyListener(new KeyAdapter() {

			Simulation model = ShapeExplorer.this.model;

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DELETE:
				case KeyEvent.VK_BACK_SPACE:
					if (shapeView.getBody() != null) {
						if (shapeView.getBody().getType() != Body.WALL) {
							this.model.removeBody(shapeView.getBody());
						}
					}
					break;
				}
			}
		});

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setSize(450, 400);
		setLocationRelativeTo(null);
	}

	/**
	 * Creates a node for every shape in the simulation and puts them into the
	 * specified top node. Each shape is put into one of a couple sub-nodes that
	 * are created in this method and correspond to the tool that would have
	 * been used to create the shape, like rectangle or circle.
	 * 
	 * @param top
	 *            - the top node that contains all the shape nodes.
	 */
	protected void createNodes() {
		topNode = new DefaultMutableTreeNode("Shapes");

		treeModel = new DefaultTreeModel(topNode);

		rectangles = new DefaultMutableTreeNode("Rectangles");
		circles = new DefaultMutableTreeNode("Circles");
		walls = new DefaultMutableTreeNode("Walls");
		polygons = new DefaultMutableTreeNode("Polygons");

		topNode.add(circles);
		topNode.add(rectangles);
		topNode.add(polygons);
		topNode.add(walls);

		Body[] bodies = model.getBodies();
		for (Body body : bodies) {
			ShapeNode node;

			switch (body.getType()) {

			case Body.RECTANGLE:
				node = new ShapeNode(body);
				rectangles.add(node);
				break;

			case Body.CIRCLE:
				node = new ShapeNode(body);
				circles.add(node);
				break;

			case Body.WALL:
				node = new ShapeNode(body);
				walls.add(node);
				break;

			default:
				node = new ShapeNode(body);
				polygons.add(node);
				break;
			}
		}
	}

	/**
	 * Adds a new body node to the list. The specified body is used to create
	 * the node; its type determines which node will receive the new node.
	 * 
	 * @param body
	 *            - the body from which to create the new node.
	 */
	protected void addBodyNode(Body body) {
		ShapeNode node;
		
		body.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				shapeList.repaint();
			}
		});

		switch (body.getType()) {

		case Body.RECTANGLE:
			node = new ShapeNode(body);
			treeModel.insertNodeInto(node, rectangles, rectangles
					.getChildCount());
			break;

		case Body.CIRCLE:
			node = new ShapeNode(body);
			treeModel.insertNodeInto(node, circles, circles.getChildCount());
			break;

		case Body.WALL:
			node = new ShapeNode(body);
			treeModel.insertNodeInto(node, walls, walls.getChildCount());
			break;

		default:
			node = new ShapeNode(body);
			treeModel.insertNodeInto(node, polygons, polygons.getChildCount());
			break;
		}

		shapeList.validate();
	}

	protected void removeBodyNode(Body body) {
		ShapeNode node;

		if (body != null) {
			switch (body.getType()) {
			case Body.CIRCLE:
				for (int i = 0; i < circles.getChildCount(); i++) {
					if (circles.getChildAt(i) instanceof ShapeNode) {
						node = (ShapeNode) circles.getChildAt(i);
						if (node.getBody().equals(body)) {
							treeModel.removeNodeFromParent(node);
						}
					}
				}
				break;

			case Body.RECTANGLE:
				for (int i = 0; i < rectangles.getChildCount(); i++) {
					if (rectangles.getChildAt(i) instanceof ShapeNode) {
						node = (ShapeNode) rectangles.getChildAt(i);
						if (node.getBody().equals(body)) {
							treeModel.removeNodeFromParent(node);
						}
					}
				}
				break;

			case Body.POLYGON:
				for (int i = 0; i < polygons.getChildCount(); i++) {
					if (polygons.getChildAt(i) instanceof ShapeNode) {
						node = (ShapeNode) polygons.getChildAt(i);
						if (node.getBody().equals(body)) {
							treeModel.removeNodeFromParent(node);
						}
					}
				}
				break;

			case Body.WALL:
				for (int i = 0; i < walls.getChildCount(); i++) {
					if (walls.getChildAt(i) instanceof ShapeNode) {
						node = (ShapeNode) walls.getChildAt(i);
						if (node.getBody().equals(body)) {
							treeModel.removeNodeFromParent(node);
						}
					}
				}
				break;

			}
		}
	}

	/**
	 * Selects the supplied body if possible. If the body is null or not in the
	 * list, then nothing is selected but whatever body was selected before IS
	 * unselected.
	 * 
	 * @param body
	 *            - the body to select.
	 */
	public void goToBody(Body body) {
		DefaultMutableTreeNode node;
		switch (body.getType()) {
		case Body.CIRCLE:
			node = (ShapeNode) circles.getFirstLeaf();
			while (node != null) {
				if (node instanceof ShapeNode) {
					if (((ShapeNode) node).getBody().equals(body)) {
						shapeList
								.setSelectionPath(new TreePath(node.getPath()));
						break;
					}
				}
				node = node.getNextLeaf();
			}
			break;

		case Body.RECTANGLE:
			node = (ShapeNode) rectangles.getFirstLeaf();
			while (node != null) {
				if (node instanceof ShapeNode) {
					if (((ShapeNode) node).getBody().equals(body)) {
						shapeList
								.setSelectionPath(new TreePath(node.getPath()));
						break;
					}
				}
				node = node.getNextLeaf();
			}
			break;

		case Body.WALL:
			node = (ShapeNode) walls.getFirstLeaf();
			while (node != null) {
				if (node instanceof ShapeNode) {
					if (((ShapeNode) node).getBody().equals(body)) {
						shapeList
								.setSelectionPath(new TreePath(node.getPath()));
						break;
					}
				}
				node = node.getNextLeaf();
			}
			break;

		case Body.POLYGON:
		default:
			node = (ShapeNode) polygons.getFirstLeaf();
			while (node != null) {
				if (node instanceof ShapeNode) {
					if (((ShapeNode) node).getBody().equals(body)) {
						shapeList
								.setSelectionPath(new TreePath(node.getPath()));
						break;
					}
				}
				node = node.getNextLeaf();
			}
			break;
		}
	}
}
