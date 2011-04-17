package simulation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import simulation.engine.BodySetEvent;
import simulation.engine.BodySetListener;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;

/**
 * Displays some rudimentary information on the bottom of the screen for the
 * simulation. Can be used to output messages as well.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class SimulationStatusBar extends JPanel {

	private Simulation model;

	protected JPanel shapeCountPanel;
	protected JLabel shapeCountLabel;

	protected JPanel playingPanel;
	protected JLabel playingLabel;

	public SimulationStatusBar(final Simulation model, MainWindow mainWindow) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.model = model;

		setPreferredSize(new Dimension(getWidth(), 20));
		setOpaque(false);

		shapeCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		shapeCountPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
				Color.gray));
		shapeCountLabel = new JLabel("Shape count: " + model.getBodies().length);
		shapeCountPanel.add(shapeCountLabel);
		shapeCountPanel.setOpaque(false);
		add(shapeCountPanel);

		model.addBodySetListener(new BodySetListener() {
			@Override
			public void bodyAdded(BodySetEvent e) {
				shapeCountLabel.setText("Shape count: " + model.getBodyCount());
			}

			@Override
			public void bodyRemoved(BodySetEvent e) {
				shapeCountLabel.setText("Shape count: " + model.getBodyCount());
			}
		});

		playingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		playingPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
				Color.gray));
		playingLabel = new JLabel("Paused");
		playingPanel.add(playingLabel);
		playingPanel.setOpaque(false);
		add(playingPanel);

		model.addPhysicsListener(new PhysicsListener() {
			@Override
			public void stateChanged(PhysicsEvent e) {
				if (model.isPlaying()) {
					playingLabel.setText("Playing");
				} else {
					playingLabel.setText("Paused");
				}
			}
		});
	}

	/**
	 * Returns which model this status bar is attached to. THis is the model
	 * about which the bar displays information.
	 * 
	 * @return the model of this status bar.
	 */
	public Simulation getModel() {
		return model;
	}
}
