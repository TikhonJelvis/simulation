package simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

/**
 * A toolbar for the simulation. This houses buttons for all of the tools.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class SimulationToolBar extends JToolBar {

	private ImageIcon dragTool;
	private ImageIcon circleTool;
	private ImageIcon rectangleTool;
	private ImageIcon polygonTool;
	private ImageIcon polygonPadTool;
	
	//Background gradient:
	private Color start = new Color(0x44, 0x99, 0xCC, 0x00);
	private Color end = new Color(0x44, 0x99, 0xCC, 0xFF);
	private GradientPaint background;
	
	protected JButton dragButton;
	protected JButton circleButton;
	protected JButton rectangleButton;
	protected JButton polygonButton;
	protected JButton polygonPadButton;

	/**
	 * Creates the default toolbar!
	 */
	public SimulationToolBar(final MainWindow parent) {
		super(VERTICAL);
		
		setOpaque(false);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		dragTool = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "dragIcon.png"));
		circleTool = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "circleIcon.png"));
		rectangleTool = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "rectangleIcon.png"));
		polygonTool = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "polygonIcon.png"));
		polygonPadTool = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "polygonPadIcon.png"));
		
		dragButton = new JButton(dragTool);
		dragButton.setPreferredSize(new Dimension(25, 25));
		dragButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.drag();
			}
		});
		dragButton.setOpaque(false);
		add(dragButton);
		
		circleButton = new JButton(circleTool);
		circleButton.setPreferredSize(new Dimension(25, 25));
		circleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.addCircle();
			}
		});
		circleButton.setOpaque(false);
		add(circleButton);
		
		rectangleButton = new JButton(rectangleTool);
		rectangleButton.setPreferredSize(new Dimension(25, 25));
		rectangleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.addRectangle();
			}
		});
		rectangleButton.setOpaque(false);
		add(rectangleButton);
		
		polygonButton = new JButton(polygonTool);
		polygonButton.setPreferredSize(new Dimension(25, 25));
		polygonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.addPolygon();
			}
		});
		polygonButton.setOpaque(false);
		add(polygonButton);
		
		polygonPadButton = new JButton(polygonPadTool);
		polygonPadButton.setPreferredSize(new Dimension(25, 25));
		polygonPadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.changePolygon();
			}
		});
		polygonPadButton.setOpaque(false);
		add(polygonPadButton);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Point2D.Double start = new Point2D.Double(0, 0);
		Point2D.Double end = new Point2D.Double(0, getHeight());
		
		background = new GradientPaint(start, this.start, end, this.end);
		
		g2d.setPaint(background);
		g2d.fill(getBounds());
	}
}
