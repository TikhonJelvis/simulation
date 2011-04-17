package simulation.gui;

import java.util.TimerTask;

/**
 * This class represents each frame of the simulation when it is playing. Every
 * time the <code>run()</code> method is called by a timer, the animation goes
 * forward one frame. The task is recreated each time the speed is changed --
 * this updates the new speed making the animation change immediately rather
 * than the next time play is pressed. This task knows which menu created it,
 * and uses that menu's <code>stepModel()</code> method.
 * 
 * @author Tikhon Jelvis
 * 
 */
public class AnimationTask extends TimerTask {

	private SimulationMenu menu;

	/**
	 * Creates a new task that steps the model using the specified menu's
	 * <code>stepModel()</code> method.
	 * 
	 * @param menu
	 *            - the menu through which the simulation will be played.
	 */
	public AnimationTask(SimulationMenu menu) {
		this.menu = menu;
	}

	@Override
	public void run() {
		menu.stepModel();
	}

}
