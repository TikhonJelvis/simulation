package simulation.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import simulation.engine.Body;
import simulation.engine.CollisionCircle;
import simulation.engine.CollisionPolygon;
import simulation.engine.PhysicsEvent;
import simulation.engine.PhysicsListener;
import simulation.engine.Simulation;
import simulation.engine.Vector;
import simulation.gui.ViewPanel;

@SuppressWarnings("serial")
public class PongPanel extends ViewPanel {
	private Simulation model;

	private Body ball;
	private Body leftPaddle;
	private Body rightPaddle;
	private Body topWall;
	private Body bottomWall;

	private Timer timer = new Timer();
	private AnimationTask task = new AnimationTask();

	private Vector startingVelocity = new Vector(4, 6);

	public PongPanel() {
		model = new Simulation();

		setPreferredSize(new Dimension(0, 0));
		setFocusable(true);

		model.addPhysicsListener(new PhysicsListener() {
			@Override
			public void stateChanged(PhysicsEvent e) {
				repaint();

				if (model.containsBody(ball)) {
					int x = ball.getShape().toShape().getBounds().x;
					int width = ball.getShape().toShape().getBounds().width;
					
					if (x + width < 0 || x > 590) {
						model.removeBody(ball);

						ball = new Body(new CollisionCircle(12, new Vector(290,
								200)), 10, Color.blue, Body.CIRCLE);
						model.addBody(ball);

						if (Math.random() < 0.5) {
							ball.setVelocity(startingVelocity);
						} else {
							ball.setVelocity(startingVelocity.invert());
						}
					} else {
						ball.setVelocity(ball.getVelocity().multiply(1.0001));
					}
				}

				if (leftPaddle != null) {
					leftPaddle.setAngularVelocity(0);
					leftPaddle.setVelocity(new Vector(0, leftPaddle
							.getVelocity().getY()));
				}
				if (rightPaddle != null) {
					rightPaddle.setAngularVelocity(0);
					rightPaddle.setVelocity(new Vector(0, rightPaddle
							.getVelocity().getY()));
				}
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					rightPaddle.setVelocity(new Vector(0, -10));
					break;

				case KeyEvent.VK_DOWN:
					rightPaddle.setVelocity(new Vector(0, 10));
					break;

				case KeyEvent.VK_W:
					leftPaddle.setVelocity(new Vector(0, -10));
					break;

				case KeyEvent.VK_S:
					leftPaddle.setVelocity(new Vector(0, 10));
					break;

				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (rightPaddle.getVelocity().getY() < 0) {
						rightPaddle.setVelocity(new Vector(0, 0));
					}
					break;

				case KeyEvent.VK_DOWN:
					if (rightPaddle.getVelocity().getY() > 0) {
						rightPaddle.setVelocity(new Vector(0, 0));
					}
					break;

				case KeyEvent.VK_W:
					if (leftPaddle.getVelocity().getY() < 0) {
						leftPaddle.setVelocity(new Vector(0, 0));
					}
					break;

				case KeyEvent.VK_S:
					if (leftPaddle.getVelocity().getY() > 0) {
						leftPaddle.setVelocity(new Vector(0, 0));
					}
					break;
				}
			}
		});

		timer.scheduleAtFixedRate(task, 0, 1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		super.paintComponent(g);

		for (Body body : model.getBodies()) {
			g2d.setColor(body.getColor());
			Shape toDraw = body.getShape().toShape();
			g2d.fill(toDraw);
		}
	}

	protected void newGame() {
		model.reset();

		Body.setDefaultBounciness(1);

		int[] x = new int[] { -10, getWidth() + 10, getWidth() + 10, -10 };
		int[] y = new int[] { 0, 0, 10, 10 };
		CollisionPolygon shape = new CollisionPolygon(x, y, 4);
		topWall = new Body(shape, 1000, Color.black, Body.RECTANGLE);
		topWall.setFixed(true);

		y = new int[] { getHeight(), getHeight(), getHeight() - 10,
				getHeight() - 10 };
		shape = new CollisionPolygon(x, y, 4);
		bottomWall = new Body(shape, 1000, Color.black, Body.RECTANGLE);
		bottomWall.setFixed(true);

		leftPaddle = new Body(new CollisionPolygon(
				new int[] { 0, 10, 15, 10, 0 }, new int[] { 129, 129, 179, 229,
						229 }, 4), 100000, Color.red, Body.RECTANGLE);

		rightPaddle = new Body(new CollisionPolygon(new int[] { 590, 580, 580,
				590 }, new int[] { 130, 130, 230, 230 }, 4), 100000,
				Color.orange, Body.RECTANGLE);

		ball = new Body(new CollisionCircle(12, new Vector(290, 200)), 10,
				Color.blue, Body.CIRCLE);

		model.addBody(rightPaddle);
		model.addBody(topWall);
		model.addBody(bottomWall);
		model.addBody(leftPaddle);
		model.addBody(ball);

		ball.setVelocity(startingVelocity);

		timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);
	}

	private void stepModel() {
		model.step(0.05, 20);
	}

	private class AnimationTask extends TimerTask {

		@Override
		public void run() {
			stepModel();
		}

	}

}
