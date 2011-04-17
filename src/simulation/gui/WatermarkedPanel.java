package simulation.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A panel that has the logo and its reflection near the middle.
 * 
 * @author Tikhon Jelvis
 *
 */
@SuppressWarnings("serial")
public class WatermarkedPanel extends JPanel {

	//Background gradient:
	private Color start = new Color(0xFF, 0xFF, 0xFF, 0x00);
	private Color end = new Color(0x44, 0x99, 0xCC, 0xFF);
	private GradientPaint background;
	
	// Logo
	private String logoPath = MainWindow.RESOURCE_PATH + "logo.png";
	private BufferedImage logo;
	
	public WatermarkedPanel() {
		this(new FlowLayout());
	}
	
	public WatermarkedPanel(LayoutManager manager) {
		super(manager);

		setOpaque(false);
		
		// Let's get the pretty picture:
		try {
			// The logo:
			File location = new File(getClass().getResource(logoPath).toURI());
			logo = ImageIO.read(location);
		} catch (URISyntaxException e) {
			// Panic and die!
			logo = null;
		} catch (IOException e) {
			// More panicky death:
			logo = null;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Point2D.Double start = new Point2D.Double(0, 0);
		Point2D.Double end = new Point2D.Double(0, getHeight());
		
		background = new GradientPaint(start, this.start, end, this.end);
		
		g2d.setPaint(background);
		g2d.fill(getBounds());
		
		if (logo != null) {
			int y = (int) ((getHeight() / 2) - (logo.getHeight()/ 2));
			int x = getWidth() > logo.getWidth() ? (getWidth() - logo
					.getWidth()) / 2 : 0;
			int width = logo.getWidth();
			int height = logo.getHeight();
			int gap = 2;
			int maxOpacity = 0x77;

			BufferedImage result = new BufferedImage(width, 2 * height + gap,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D rg = result.createGraphics();

			rg.drawRenderedImage(logo, null);

			rg.translate(0, 1.6 * height + gap);
			rg.scale(1, -1); // Flip!

			rg.drawRenderedImage(logo, null);
			rg.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));

			GradientPaint alphaPaint = new GradientPaint(0, 80, new Color(0, 0,
					0, 0), 0, 160, new Color(0, 0, 0, maxOpacity));
			rg.setPaint(alphaPaint);
			rg.fillRect(0, 0, 175, 162);

			rg.setColor(new Color(0, 0, 0, 0x66));
			rg.fillRect(0, 0, 200, 400);
			rg.dispose();

			g2d.translate(x - 15, y + gap);
			g2d.drawRenderedImage(result, null);
			g2d.translate(-x + 15, -y - gap);
		}

		super.paintComponent(g);
	}

}
