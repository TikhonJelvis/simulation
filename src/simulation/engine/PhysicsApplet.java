package simulation.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JApplet;

public class PhysicsApplet extends JApplet implements Runnable {
    private static final long serialVersionUID = 1L;

    private Thread runner;

    private Profiler profiler;

    private Simulation system;

    private int width, height;

    public PhysicsApplet() {
    }

    @Override
    public void init() {
        width = 300;
        height = 300;
        this.setSize(width + 200, height + 200);
        runner = new Thread(this);
        runner.start();
        profiler = new Profiler(runner);
        profiler.start();

    }

    public void run() {
        Graphics g = getGraphics();
        g.setClip(new Rectangle(width, height));
        // BufferedImage img = new BufferedImage(width, height,
        // BufferedImage.TYPE_3BYTE_BGR);
        system = new Simulation();
        Body ball1 = new Body(new CollisionCircle(20, new Vector(100, 100)), 10);

        ball1.setVelocity(new Vector(6, -10));
        ball1.setAngularVelocity(.5);
        // system.addBody(ball1);
        /*
         * Vector[] pseudoBallVs = new Vector[1000]; for(int i = 0; i <
         * pseudoBallVs.length; ++i) { pseudoBallVs[i] = Vector.fromAngle(
         * Math.PI 2 i / pseudoBallVs.length, 20).add(new Vector(200, 200)); }
         * Body pseudoBall = new Body(new CollisionPolygon(pseudoBallVs), 10);
         * system.addBody(pseudoBall); double ball1mi = ball1.momentOfInertia();
         * double pseudomi = pseudoBall.momentOfInertia();
         */
        final Body ball2 = new Body(
                new CollisionCircle(40, new Vector(150, 50)), 40);
        ball2.setVelocity(new Vector(0, 0));
        ball2.setAngularVelocity(.1);
        system.addBody(ball2);
        Body square = new Body(new CollisionPolygon(new Vector[] {
                new Vector(150, 150), new Vector(200, 150),
                new Vector(225, 175), new Vector(200, 200),
                new Vector(150, 200) }), 20);
        square.setVelocity(new Vector(0, 0));
        square.setAngularVelocity(0);
        system.addBody(square);
        system.addSpring(new Spring(1, ball2, square, ball2.getShape().center()
                .add(new Vector(30, 0)), square.getShape().center().add(
                new Vector(30, 0))));
        system.addSpring(new Spring(1, ball2, square, ball2.getShape().center()
                .add(new Vector(-30, 0)), square.getShape().center().add(
                new Vector(-20, 0))));
        this.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyChar() == ' ') {
                    // ball2.addImpulse(new Vector(0, -1000));
                    ball2.setAngularVelocity(ball2.angularVelocity() + .1);
                }
            }

            public void keyTyped(KeyEvent evt) {
            }

            public void keyReleased(KeyEvent evt) {
            }
        });
        Body rect = new Body(new CollisionPolygon(new Vector[] {
                new Vector(100, 100), new Vector(200, 100),
                new Vector(200, 200), new Vector(100, 200) }), 10);
        rect.setVelocity(new Vector(0, 0));
        // rect.setAngularVelocity(-10);
        // system.addBody(rect);
        int wallSize = 20;
        Body box = new Body(new CollisionPolygon(
                new Vector[] { new Vector(0, 0), new Vector(width, 0),
                        new Vector(width, height), new Vector(0, height),
                        new Vector(0, wallSize),
                        new Vector(wallSize, wallSize),
                        new Vector(wallSize, height - wallSize),
                        new Vector(width - wallSize, height - wallSize),
                        new Vector(width - wallSize, wallSize),
                        new Vector(0, wallSize) }), 100000000);
        // box.setVelocity(new Vector(-.5, -.5));
        box.setFixed(true);
        system.addBody(box);
        for (;;) {
            /*
             * for(int y = 0; y < height; ++y) { for(int x = 0; x < width; ++x)
             * { Color color; Body colliding = system.bodyAt(new Vector(x, y));
             * if(colliding != null) { color = Color.blue; } else { color =
             * Color.yellow; } img.setRGB(x, y, color.getRGB()); } }
             */

            // g.drawImage(img, 0, 0, this);
            int numb = system.numBodies();
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            for (int i = 0; i < numb; ++i) {
                Body b = system.getBody(i);
                g.setColor(Color.blue);
                b.getShape().fill(g);
            }
            /*
             * for(int i = 0; i < numb; ++i) { Body b = system.getBody(i);
             * CollisionResult res = b.prevRes(); Vector orig =
             * res.contactPoint(); g.setColor(Color.yellow); g.drawOval( (int)
             * (orig.x()-5), (int) (orig.y()-5), 10, 10); Vector trans =
             * orig.add(res.translation().withMagnitude(20)); g.drawLine( (int)
             * orig.x(), (int) orig.y(), (int) trans.x(), (int) trans.y()); }
             */
            double ek = 0;
            for (int i = 0; i < numb; ++i) {
                Body body = system.getBody(i);
                if (!body.fixed()) {
                    ek += .5 * Math.pow(body.velocity().magnitude(), 2)
                            * body.mass();
                    ek += .5 * Math.pow(body.angularVelocity(), 2)
                            * body.momentOfInertia();

                }
            }
            for (Spring s : system.getSprings()) {
                g.setColor(Color.magenta);
                s.draw(g);
            }
            g.setColor(Color.white);
            g.drawString("" + ek, 30, 30);
            for (int i = 0; i < 5000; ++i) {
                system.step(.0001);
                // try{Thread.sleep(100);}catch(Exception ex){}
            }

        }
    }

    @Override
    public void destroy() {
        // Uncomment this to make the profiler save a log file when closing.
        // Set the file to wherever it should be saved.

        /*
         * profiler.stop(); String profInfo = profiler.toString(); try {
         * FileWriter logFile = new
         * FileWriter("C:/documents and settings/jacob taylor/my documents/log.txt"
         * ); logFile.write(profInfo); logFile.close(); } catch(Exception ex) {
         * 
         * }
         */
    }
}
