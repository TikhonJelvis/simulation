package simulation.gui;

import java.util.EventObject;

/**
 * Represents the change in the camera -- either in the zoom or position
 * (offset) of the view.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class CameraEvent extends EventObject {

	private final double zoomFactor;
	private final int xOffset;
	private final int yOffset;

	/**
	 * Creates a new event witht he specified view as the source. The zoomFactor
	 * and offsets are taken from the source at creation and cannot be changed
	 * afterwards.
	 * 
	 * @param source
	 */
	public CameraEvent(SimulationView source) {
		super(source);

		zoomFactor = source.getZoomFactor();
		xOffset = source.getXOffset();
		yOffset = source.getYOffset();
	}

	/**
	 * Returns the changed zoom factor of the view.
	 * 
	 * @return the zoom factor
	 */
	public double getZoomFactor() {
		return zoomFactor;
	}

	/**
	 * Returns the x-offset of the view.
	 * 
	 * @return the x-offset
	 */
	public int getXOffset() {
		return xOffset;
	}

	/**
	 * Returns the y-offset of the view.
	 * 
	 * @return the y-offset
	 */
	public int getYOffset() {
		return yOffset;
	}

}
