package simulation.gui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import simulation.engine.Body;

/**
 * This class renders the <code>ShapeNode</code>s that make up the list part of
 * the <code>ShapeExplorer</code>. This renderer adds proper icons to the nodes.
 * 
 * @see ShapeNode
 * @see ShapeExplorer
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class ShapeNodeRenderer extends DefaultTreeCellRenderer {

	// Resource paths:
	public static final String CIRCLE_PATH = MainWindow.RESOURCE_PATH
			+ "circleIcon.png";
	public static final String RECTANGLE_PATH = MainWindow.RESOURCE_PATH
			+ "rectangleIcon.png";
	public static final String POLYGON_PATH = MainWindow.RESOURCE_PATH
			+ "polygonIcon.png";
	public static final String WALL_PATH = MainWindow.RESOURCE_PATH
			+ "wallIcon.png";

	public static final String FOLDER_PATH = MainWindow.RESOURCE_PATH
			+ "folder.png";
	public static final String LEAF_PATH = MainWindow.RESOURCE_PATH
			+ "leaf.png";

	private ImageIcon folderIcon;
	private ImageIcon leafIcon;

	/**
	 * Creates a new renderer, loading the needed icons.
	 */
	public ShapeNodeRenderer() {

		// Resources:
		folderIcon = new ImageIcon(getClass().getResource(FOLDER_PATH));
		leafIcon = new ImageIcon(getClass().getResource(LEAF_PATH));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		if (value instanceof ShapeNode) {
			Body body = ((ShapeNode) value).getBody();

			setIcon(createIconFromBody(body));
		}

		else if (leaf) {
			setIcon(leafIcon);
		}

		else {
			setIcon(folderIcon);
		}

		return this;
	}

	private ImageIcon createIconFromBody(Body body) {
		Shape shape = body.getShape().toShape();
		Rectangle bounds = shape.getBounds();

		BufferedImage initialImage = new BufferedImage(19, 19,
				BufferedImage.TYPE_INT_ARGB);

		double maxSide = bounds.height > bounds.width ? bounds.height
				: bounds.width;
		double ratio = 19 / maxSide;

		Graphics2D ig = initialImage.createGraphics();
		ig.setColor(body.getColor());
		ig.scale(ratio, ratio);
		ig.translate(-bounds.x, -bounds.y);
		ig.fill(shape);
		ig.dispose();

		ImageIcon icon = new ImageIcon(initialImage);

		return icon;
	}
}
