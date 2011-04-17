package simulation.pong;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * An example of the versatility of the simulation, this runs a simple game of
 * Pong.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class Pong extends JFrame{

	protected PongPanel pongPanel;
	
	protected JMenuBar bar;
	
	protected JMenu file;
	protected JMenuItem file_new;
	
	public Pong() {
		super("Pong");
		
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		pongPanel = new PongPanel();
		add(pongPanel);
		
		bar = new JMenuBar();
		
		file = new JMenu("File");
		
		file_new = new JMenuItem("New");
		file_new.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pongPanel.newGame();
			}
		});
		file.add(file_new);
		
		bar.add(file);
		
		setJMenuBar(bar);
		
		setVisible(true);
	}
	
	/**
	 * Runs pong.
	 * 
	 * @param args
	 *            - these are ignored.
	 */
	public static void main(String[] args) {
		new Pong();
	}

}
