package simulation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AboutWindow extends JFrame {

	private ImageIcon logo;

	protected JPanel content;
	protected JLabel image;
	protected JPanel text;

	public AboutWindow(ArrayList<Image> icons) {
		super("About the 2D Collision Simulation");

		setSize(350, 250);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		if (icons != null) {
			setIconImages(icons);
		}

		logo = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "logo.png"));

		content = new JPanel(new BorderLayout()) {
			{
				setOpaque(false);
			}

			@Override
			protected void paintComponent(Graphics g) {
				int height = getHeight();
				int width = getWidth();
				Color start = new Color(0x12, 0x6E, 0xA2);
				Color end = getBackground();
				GradientPaint background = new GradientPaint(0, 0, start,
						width - 25, 0, end);

				Graphics2D g2d = (Graphics2D) g;
				g2d.setPaint(background);
				g2d.fillRect(0, 0, width, height);

				super.paintComponent(g);
			}
		};
		add(content);

		image = new JLabel(logo);
		content.add(image, BorderLayout.WEST);

		text = new JPanel() {
			{
				setPreferredSize(new Dimension(170, getHeight()));
				setOpaque(false);
				setLayout(null);

				Dimension size = new Dimension(170, 25);

				JLabel version = new JLabel(" " + MainWindow.VERSION);
				version.setLocation(29, 65);
				version.setSize(size);
				add(version);

				JLabel contributors = new JLabel("Contributors: ");
				contributors.setLocation(30, 90);
				contributors.setSize(size);
				add(contributors);

				JLabel ken = new JLabel("Ken Dopp,");
				ken.setLocation(50, 110);
				ken.setSize(size);
				add(ken);

				JLabel tikhon = new JLabel("Tikhon Jelvis,");
				tikhon.setLocation(50, 125);
				tikhon.setSize(size);
				add(tikhon);

				JLabel greg = new JLabel("Greg Nisbet,");
				greg.setLocation(50, 140);
				greg.setSize(size);
				add(greg);
				
				JLabel jacob = new JLabel("Jacob Taylor");
				jacob.setLocation(50, 155);
				jacob.setSize(size);
				add(jacob);
			}
		};
		content.add(text, BorderLayout.EAST);
	}
}
