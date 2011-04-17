package simulation.gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A 2D shape collision simulation with a Swing GUI; it presents a large amount
 * of options to the user, letting the user specify circles, rectangles or any
 * custom irregular polygon.
 * 
 * @version 1.0.3
 * @author Jacob Taylor
 * @author Tikhon Jelvis
 * 
 */
public class Main {

	/**
	 * Starts the program.
	 * 
	 * @param args
	 *            - these arguments are completely ignored.
	 */
	public static void main(String[] args) {
		/*
		 * Set the java look and feel; not only are other look and feels not
		 * standard at all, but relatively ugly as well. Additionally, having a
		 * distinctive look and feel makes the program a little more likely to
		 * stand out and be remembered.
		 */

		setCrossPlatformLookAndFeel();

		new MainWindow();
	}

	public static void setCrossPlatformLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// Do nothing!
		} catch (InstantiationException e) {
			// Do nothing!
		} catch (IllegalAccessException e) {
			// Do nothing!
		} catch (UnsupportedLookAndFeelException e) {
			// Do nothing!
		}
	}

}
