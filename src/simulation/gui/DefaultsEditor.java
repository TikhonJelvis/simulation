package simulation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import simulation.engine.Body;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;

/**
 * This is a window that lets users edit the defaults of shape creation like the
 * default density which dictates the created object's mass, the default
 * friction and the default bounciness.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class DefaultsEditor extends JFrame {

	protected JTabbedPane pane;

	protected JPanel shapeCreation;

	protected JPanel density;
	protected JLabel densityLabel;
	protected JSpinner densitySpinner;

	protected JPanel bounciness;
	protected JLabel bouncinessLabel;
	protected JSpinner bouncinessSpinner;

	protected JPanel friction;
	protected JLabel frictionLabel;
	protected JSpinner frictionSpinner;

	protected JPanel walls;

	protected JPanel wallColor;
	protected ColorButton wallColorButton;
	protected JLabel wallColorLabel;

	private Simulation model;

	private ImageIcon icon;

	public DefaultsEditor(Simulation model) {
		super("Edit Defaults");

		this.model = model;

		icon = new ImageIcon(getClass().getResource(
				MainWindow.RESOURCE_PATH + "leaf.png"));
		setIconImage(icon.getImage());

		setSize(225, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		pane = new JTabbedPane();

		shapeCreation = new JPanel(new FlowLayout(FlowLayout.CENTER));
		shapeCreation.setPreferredSize(new Dimension(getWidth() - 10,
				getHeight() - 10));
		shapeCreation.setBorder(BorderFactory
				.createTitledBorder("Shape Creation Defaults"));

		density = new JPanel(new BorderLayout());
		density.setPreferredSize(new Dimension(getWidth() - 60, 50));
		density.setBorder(BorderFactory.createTitledBorder("Density"));

		densityLabel = new JLabel("Density : ");
		density.add(densityLabel, BorderLayout.WEST);

		densitySpinner = new JSpinner();
		SpinnerModel spinnerModel = new SpinnerNumberModel(Body
				.getDefaultDensity(), 0.001, 4, 0.001);
		densitySpinner.setModel(spinnerModel);
		densitySpinner.setPreferredSize(new Dimension(60, 20));
		densitySpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Body.setDefaultDensity((Double) densitySpinner.getValue());
			}
		});
		density.add(densitySpinner, BorderLayout.EAST);

		bounciness = new JPanel(new BorderLayout());
		bounciness.setPreferredSize(new Dimension(getWidth() - 60, 50));
		bounciness.setBorder(BorderFactory.createTitledBorder("Bounciness"));

		bouncinessLabel = new JLabel("Bounciness : ");
		bounciness.add(bouncinessLabel, BorderLayout.WEST);

		bouncinessSpinner = new JSpinner();
		spinnerModel = new SpinnerNumberModel(Body.getDefaultBounciness(), 0,
				1, 0.1);
		bouncinessSpinner.setModel(spinnerModel);
		bouncinessSpinner.setPreferredSize(new Dimension(60, 20));
		bouncinessSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Body
						.setDefaultBounciness((Double) bouncinessSpinner
								.getValue());
			}
		});
		bounciness.add(bouncinessSpinner, BorderLayout.EAST);

		friction = new JPanel(new BorderLayout());
		friction.setPreferredSize(new Dimension(getWidth() - 60, 50));
		friction.setBorder(BorderFactory.createTitledBorder("Friction"));

		frictionLabel = new JLabel("Friction : ");
		friction.add(frictionLabel, BorderLayout.WEST);

		frictionSpinner = new JSpinner();
		spinnerModel = new SpinnerNumberModel(Body.getDefaultFriction(), 0, 1,
				0.1);
		frictionSpinner.setModel(spinnerModel);
		frictionSpinner.setPreferredSize(new Dimension(60, 20));
		frictionSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Body.setDefaultFriction((Double) frictionSpinner.getValue());
			}
		});
		friction.add(frictionSpinner, BorderLayout.EAST);

		shapeCreation.add(density);
		shapeCreation.add(bounciness);
		shapeCreation.add(friction);

		pane.addTab("Shapes", shapeCreation);

		walls = new JPanel(new FlowLayout(FlowLayout.CENTER));
		walls.setBorder(BorderFactory.createTitledBorder("Wall Defaults"));

		wallColor = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		wallColor.setBorder(BorderFactory.createTitledBorder("Color"));
		wallColor.setPreferredSize(new Dimension(getWidth() - 60, 50));

		wallColorButton = new ColorButton(Color.black);
		wallColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(DefaultsEditor.this,
						"Choose wall color", wallColorButton.getColor());
				if (newColor != null) {
					wallColorButton.setColor(newColor);
					wallColorLabel.setText(wallColorButton.getColorName());
					DefaultsEditor.this.model.setWallColor(newColor);
				}
			}
		});
		wallColor.add(wallColorButton);

		wallColorLabel = new JLabel(wallColorButton.getColorName());
		wallColor.add(wallColorLabel);

		walls.add(wallColor);

		pane.addTab("Walls", walls);

		add(pane);

		this.model.addPhysicsListener(new PhysicsListener() {
			@Override
			public void stateChanged(PhysicsEvent e) {
				bouncinessSpinner.setValue(Body.getDefaultBounciness());
				densitySpinner.setValue(Body.getDefaultDensity());
				frictionSpinner.setValue(Body.getDefaultFriction());

				repaint();
			}
		});
	}

}
