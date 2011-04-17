package simulation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;

/**
 * This is the menu on the side of the simulation; it contains various options
 * and buttons, particularly the play/step/pause commands.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class SimulationMenu extends JPanel {

    // Image locations:
    public static final String PLAY_IMAGE_LOCATION = MainWindow.RESOURCE_PATH
            + "play.png";

    public static final String STEP_IMAGE_LOCATION = MainWindow.RESOURCE_PATH
            + "step.png";

    public static final String STOP_IMAGE_LOCATION = MainWindow.RESOURCE_PATH
            + "stop.png";

    private Simulation model;

    private MainWindow mainWindow;

    private double speed = 50;// How fast to play the simulation.

    private double accuracy = 0.1;

    private boolean going;

    // The step / play / pause menu:
    protected JPanel playMenuHolder;

    protected JPanel playMenu;

    protected JButton step;

    protected JButton play;

    protected JButton stop;

    // Components:
    protected JTabbedPane menuPane;

    // Options menu:
    protected JPanel optionsMenu;

    protected JPanel speedMenu;

    protected JSlider speedSlider;

    protected JPanel accuracyMenu;

    protected JSlider accuracySlider;

    protected JPanel gravityMenu;

    protected JSlider gravitySlider;

    // Camera menu:
    protected JPanel cameraMenu;

    protected JPanel mapPanel;

    protected CameraMap map;

    protected JPanel zoomPanel;

    protected JSlider zoomSlider;

    // Images for buttons:
    private ImageIcon stepIcon;

    private ImageIcon playIcon;

    private ImageIcon stopIcon;

    // Animation:
    protected Timer animationTimer = new Timer();

    protected TimerTask animationTask;

    /**
     * Creates the menu. This constructor handles everything including all the
     * buttons and options.
     */
    public SimulationMenu(final Simulation model, final MainWindow mainWindow) {
        super(new BorderLayout());

        setOpaque(false);

        this.model = model;// Assigns the model that this menu will control.
        this.mainWindow = mainWindow;// Assigns the window this is related to.

        // Get the resources (pictures for the buttons):
        stepIcon = new ImageIcon(getClass().getResource(STEP_IMAGE_LOCATION));
        playIcon = new ImageIcon(getClass().getResource(PLAY_IMAGE_LOCATION));
        stopIcon = new ImageIcon(getClass().getResource(STOP_IMAGE_LOCATION));

        // The step / play / pause menu:
        playMenuHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));

        playMenu = new JPanel(null);
        playMenu.setPreferredSize(new Dimension(125, 25));
        playMenu.setBorder(BorderFactory.createTitledBorder(""));
        playMenu.setOpaque(false);

        step = new JButton(stepIcon);
        step.setBounds(25, 3, 20, 20);
        step.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepModel();
            }
        });
        playMenu.add(step);

        play = new JButton(playIcon) {
            {
                setIcon(playIcon);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (SimulationMenu.this.model.isPlaying()) {
                    Graphics2D g2d = (Graphics2D) g;
                    Rectangle toPaint = new Rectangle(2, 2, getWidth() - 4,
                            getHeight() - 4);
                    g2d.setColor(new Color(0x33, 0x66, 0x99, 0x66));
                    g2d.fill(toPaint);
                }
            }
        };
        play.setBounds(50, 3, 20, 20);
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
        playMenu.add(play);

        stop = new JButton(stopIcon);
        stop.setBounds(75, 3, 20, 20);
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        playMenu.add(stop);

        playMenuHolder.add(playMenu);
        add(playMenuHolder, BorderLayout.NORTH);

        // The tabbed pane and everything in it:

        menuPane = new JTabbedPane();
        menuPane.setPreferredSize(new Dimension(150, 500));

        optionsMenu = new WatermarkedPanel(new FlowLayout(FlowLayout.CENTER, 0,
                10));

        // The speed control:
        speedMenu = new JPanel();
        speedMenu.setPreferredSize(new Dimension(125, 50));
        speedMenu.setBorder(BorderFactory.createTitledBorder("Framerate"));
        speedMenu.setOpaque(false);

        speedSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100,
                (int) speed);
        speedSlider.setPreferredSize(new Dimension(100, 20));
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setSpeed(speedSlider.getValue());
            }
        });
        speedSlider.setOpaque(false);
        speedMenu.add(speedSlider);

        // The accuracy control:
        accuracyMenu = new JPanel();
        accuracyMenu.setPreferredSize(new Dimension(125, 50));
        accuracyMenu.setBorder(BorderFactory.createTitledBorder("Accuracy"));
        accuracyMenu.setOpaque(false);

        accuracySlider = new JSlider(SwingConstants.HORIZONTAL, 0, 10,
                getAccuracy());
        accuracySlider.setPreferredSize(new Dimension(100, 20));
        accuracySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setAccuracy(accuracySlider.getValue());
            }
        });
        accuracySlider.setOpaque(false);
        accuracyMenu.add(accuracySlider);

        gravityMenu = new JPanel();
        gravityMenu.setPreferredSize(new Dimension(125, 60));
        gravityMenu.setBorder(BorderFactory.createTitledBorder("Gravity"));
        gravityMenu.setOpaque(false);

        gravitySlider = new JSlider(SwingConstants.HORIZONTAL, -5, 5,
                (int) model.getGravity());
        gravitySlider.setPreferredSize(new Dimension(100, 30));
        gravitySlider.setPaintTicks(true);
        gravitySlider.setMajorTickSpacing(5);
        gravitySlider.setMinorTickSpacing(1);
        gravitySlider.setSnapToTicks(true);
        gravitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                model.setGravity(gravitySlider.getValue());
            }
        });
        gravitySlider.setOpaque(false);
        gravityMenu.add(gravitySlider);

        // Update the gravitySlider properly:
        model.addPhysicsListener(new PhysicsListener() {
            @Override
            public void stateChanged(PhysicsEvent e) {
                if (model.getGravity() != gravitySlider.getValue()) {
                    if (model.getGravity() < gravitySlider.getMinimum()) {
                        model.setGravity(gravitySlider.getMinimum());
                        gravitySlider.setValue(gravitySlider.getMinimum());
                    } else if (model.getGravity() > gravitySlider.getMaximum()) {
                        model.setGravity(gravitySlider.getMaximum());
                        gravitySlider.setValue(gravitySlider.getMaximum());
                    } else {
                        gravitySlider.setValue((int) model.getGravity());
                    }
                }
                setGoing(false);
            }
        });

        optionsMenu.add(speedMenu);
        optionsMenu.add(accuracyMenu);
        optionsMenu.add(gravityMenu);

        menuPane.addTab("Options", optionsMenu);

        cameraMenu = new WatermarkedPanel();

        mapPanel = new JPanel(new BorderLayout());
        mapPanel.setOpaque(false);
        mapPanel.setPreferredSize(new Dimension(120, 120));
        mapPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        map = new CameraMap(model, this.mainWindow.modelView);
        mapPanel.add(map, BorderLayout.CENTER);

        cameraMenu.add(mapPanel);

        zoomPanel = new JPanel();
        zoomPanel.setPreferredSize(new Dimension(125, 50));
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomPanel.setOpaque(false);

        zoomSlider = new JSlider(-1000, 1000, 0);
        zoomSlider.setPreferredSize(new Dimension(100, 20));
        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double zoomFactor = 1.0;

                double value;

                if (zoomSlider.getValue() >= 0) {
                    value = ((double) zoomSlider.getValue()) / 1000;
                    value *= 2;
                } else {
                    value = ((double) zoomSlider.getValue()) / 1200;
                }
                zoomFactor = 1.01 + value;

                mainWindow.modelView.setZoomFactor(zoomFactor);
            }
        });
        zoomSlider.setOpaque(false);
        zoomPanel.add(zoomSlider);

        cameraMenu.add(zoomPanel);

        menuPane.addTab("Camera", cameraMenu);

        add(menuPane);

        setPreferredSize(new Dimension(160, 650));

    }

    // Steps the model by 1 second
    protected void stepModel() {
        setGoing(true);
        model.step(accuracy, 20);
    }

    // Starts the simulation playing, if it isn't already:
    protected void play() {
        if (!model.isPlaying()) {
            model.setPlaying(true);
            animationTask = new AnimationTask(this);
            animationTimer.scheduleAtFixedRate(animationTask, 0,
                    (long) (1000d / speed));

            play.repaint();
        }
    }

    // Stops the animation if it is playing:
    protected void stop() {
        if (model.isPlaying() && animationTask != null) {
            animationTask.cancel();
            animationTask = null;
            model.setPlaying(false);

            play.repaint();
        }
    }

    protected void setPlaying(boolean playing) {
        if (playing) {
            play();
        } else {
            stop();
        }
    }

    /**
     * Sets the speed of animation in frames per second. The speed has to be
     * between 0 and 100; other values will lead to nothing happening. Calling
     * this method while the animation is playing will cause the <code>
	 * AnimationTask </code>
     * playing to be recreated and rescheduled with the new speed being used to
     * determine the interval.
     * 
     * @param speed
     *            - the new speed in frames per second. The speed can only be
     *            between 0 and 100; anything else will be ignored. A speed of 0
     *            stops the animation.
     */
    public void setSpeed(double speed) {
        if (speed >= 0 && speed <= 100) {
            this.speed = speed;

            if (model.isPlaying() && animationTask != null) {
                animationTask.cancel();
                animationTask = new AnimationTask(this);

                if (speed > 0) {
                    animationTimer.scheduleAtFixedRate(animationTask, 0,
                            (long) (1000d / speed));
                }
            }
        }
    }

    /**
     * Returns the speed at which the simulation is or would be playing.
     * 
     * @return speed - a value that represents the speed the simulation is or
     *         would be playing at.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets whether the animation is currently computing a step.
     * 
     * @param going
     *            - whether the simulation is computing a step.
     */
    public void setGoing(boolean going) {
        this.going = going;
    }

    /**
     * Returns whether the simulation is currently computing a step. If it is,
     * no action to it should be done for fear of <code> 
	 * ConcurrentModificationExceptions </code>
     * or undefined, unpredictable behavior.
     * 
     * @return whether the simulation is currently computing a step.
     */
    public boolean isGoing() {
        return going;
    }

    /**
     * Sets the accuracy of the simulation. The bigger the number, the more
     * accurate the simulation is. The numbers have no units; they are more-or-
     * less arbitrary.
     * 
     * @param accuracy
     *            - the accuracy of the simulation.
     */
    public void setAccuracy(int accuracy) {
        this.accuracy = 0.5 - (accuracy / (10 / 0.45));
    }

    /**
     * Returns an <code>int</code> that corresponds to the accuracy of the
     * simulation. The higher the <code>int</code>, the higher the accuracy.
     * 
     * @return the accuracy of the simulation as an <code>int</code>.
     */
    public int getAccuracy() {
        return (int) ((10 / 0.45) * (0.5 - accuracy));
    }
}
