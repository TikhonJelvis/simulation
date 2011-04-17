package simulation.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A panel with a light logo in the background. The logo does not generally
 * interfere with any information in the panel.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class ViewPanel extends JPanel {

	// Background:
	private File imageLocation;
	private BufferedImage image;

	/**
	 * Creates a new panel with a {@link FlowLayout}.
	 */
	public ViewPanel() {
		this(new FlowLayout());
	}

	/**
	 * Creates a new panel with the specified layout manager.
	 * 
	 * @param layout - the layout manager to use.
	 */
	public ViewPanel(LayoutManager layout) {
		super(layout);

		setOpaque(false);
		
		// The logo, for the background:
		try {
			imageLocation = new File(getClass().getResource(
					MainWindow.RESOURCE_PATH + "logo.png").toURI());
		} catch (URISyntaxException e1) {
			// Do nothing -- shouldn't happened.
		}

		try {
			image = ImageIO.read(imageLocation);
		} catch (IOException e) {
			// Do nothing...
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		int width = image.getWidth();
		int height = image.getHeight();

		// The background:
		g2d.setColor(new Color(0x00, 0x62, 0xCC, 0x22));
		g2d.fillRect(0, 0, getWidth(), getHeight());

		BufferedImage temp = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = temp.createGraphics();
		ig.drawRenderedImage(image, null);
		ig.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
		ig.setPaint(new Color(0xFF, 0xFF, 0xFF, 0x33));
		ig.fillRect(0, 0, width, height);
		ig.dispose();
		g2d.drawImage(temp, (getWidth() - width) / 2,
				(getHeight() - height) / 2, null);
	}
}
