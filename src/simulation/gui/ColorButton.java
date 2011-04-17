package simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

/**
 * This is a button that shows a color in a square. The color can be changed.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class ColorButton extends JButton {

	private Color color;

	/**
	 * Creates a new <code>ColorButton </code> with the square being of the
	 * specified color. The created button has no text and is 25 by 25 pixels in
	 * size.
	 * 
	 * @param color
	 *            - the color for the button to be first.
	 */
	public ColorButton(Color color) {
		this.color = color;
		if (color == null) {
			this.color = getBackground();
		}
		setPreferredSize(new Dimension(20, 20));
	}

	/**
	 * Changes the color of the button. This changes both the color of the
	 * square on the button and what is returned by <code>getColor()</code>.
	 * 
	 * @param color
	 *            - the new color for the button.
	 */
	public void setColor(Color color) {
		this.color = color;
		repaint();
	}

	/**
	 * Returns the color of the button. This is the color of the square on the
	 * button.
	 * 
	 * @return the color of the button.
	 */
	public Color getColor() {
		return color;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(color);
		g2d.fillRect(2, 2, 16, 16);
	}

	/**
	 * Returns the name of the color of this button. The name of a color is a
	 * string starting with a "#" and followed by six hex digits that
	 * correspond to the red, green and blue values of the color, two digits
	 * per channel.
	 * 
	 * @return a string corresponding to this button's color.
	 */
	public String getColorName() {
		return getColorName(this.color);
	}

	/**
	 * Returns the name of the given color. The name of a color is a
	 * string starting with a "#" and followed by six hex digits that
	 * correspond to the red, green and blue values of the color, two digits
	 * per channel.
	 * 
	 * @param color - the color whose name it will return
	 * @return the name of the given color.
	 */
	public static String getColorName(Color color) {
		String colorName = "#";
		String part;
		part = Integer.toHexString(color.getRed());
		if (part.length() < 2) {
			part = "0" + part;
		}
		colorName += part;
		part = Integer.toHexString(color.getGreen());
		if (part.length() < 2) {
			part = "0" + part;
		}
		colorName += part;
		part = Integer.toHexString(color.getBlue());
		if (part.length() < 2) {
			part = "0" + part;
		}
		colorName += part;

		colorName = colorName.toUpperCase();

		return colorName;
	}

}
