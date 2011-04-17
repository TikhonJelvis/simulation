package simulation.gui;

/**
 * This listener is notified when the camera is zoomed or moved.
 * 
 * @author Tikhon Jelvis
 *
 */
public interface CameraListener extends java.util.EventListener {

	public void zoomChanged(CameraEvent e);
	
	public void locationChanged(CameraEvent e);
}
