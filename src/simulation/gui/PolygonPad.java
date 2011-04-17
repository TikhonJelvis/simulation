package simulation.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is used to create dialogs for the creation of custom polygons. It
 * can also be used as a {@link JPanel}, letting you put a polygon creating
 * interface anywhere.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class PolygonPad extends JPanel {

    protected JFrame polygonPadFrame;

    private ArrayList<Point> points;

    private Stroke futureLineStroke;

    private Polygon polygon;

    private BufferedImage icon;

    /**
     * Creates a pad with no polygon defined.
     */
    public PolygonPad(final SimulationView modelView) {
        setOpaque(false);
        setFocusable(true);

        futureLineStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 1, new float[] { 3 }, 0);

        points = new ArrayList<Point>();

        polygonPadFrame = new JFrame("Polygon Pad") {
            {
                setSize(300, 300);

                JPanel instructionPanel = new JPanel(new FlowLayout(
                        FlowLayout.LEFT, 5, 2));
                instructionPanel.setPreferredSize(new Dimension(1, 20));
                instructionPanel.setBorder(BorderFactory.createMatteBorder(1,
                        0, 0, 0, Color.gray));

                JLabel instructions = new JLabel("Press escape to clear.");
                instructionPanel.add(instructions);

                add(instructionPanel, BorderLayout.SOUTH);

                add(PolygonPad.this);
            }
        };

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                points.add(e.getPoint());
                repaint();
                if (getPolygon() != null) {
                    modelView.setActiveTool(SimulationView.ADD_POLYGON_TOOL);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    points.clear();
                    repaint();
                    break;
                default:
                    break;
                }
            }
        });

        try {
            File location = new File(getClass().getResource(
                    MainWindow.RESOURCE_PATH + "polygonPadIcon.png").toURI());
            icon = ImageIO.read(location);
            polygonPadFrame.setIconImage(icon);
        } catch (URISyntaxException e) {
            // Do nothing.
        } catch (IOException e) {
            // Do nothing.
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        Color start = new Color(0x00, 0x62, 0xCC, 0x22);
        Color end = new Color(0x00, 0x33, 0x99, 0x55);
        GradientPaint paint = new GradientPaint(0, 0, start, getWidth(), 0, end);
        g2d.setPaint(paint);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.black);
        for (Point point : points) {
            Ellipse2D.Double ellipse = new Ellipse2D.Double(point.x - 2,
                    point.y - 2, 4, 4);
            g2d.draw(ellipse);
        }

        // Connect various points:
        if (points.size() > 0) {
            for (int i = 1; i < points.size(); i++) {
                Point p1 = points.get(i - 1);
                Point p2 = points.get(i);

                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            g2d.setStroke(futureLineStroke);

            Point p1 = points.get(points.size() - 1);
            Point p2 = points.get(0);

            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            g2d.setStroke(new BasicStroke());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        polygonPadFrame.setVisible(visible);
    }

    /**
     * Explicitly defines this pad's polygon. If <code>null</code> is supplied,
     * then the polygon is no longer explicitly defined and a call to <code>
	 * getPolygon()</code>
     * returns whatever is drawn on the pad, as long as the drawn polygon is
     * valid. If an actual polygon is passed, calls to <code>
	 * getPolygon()</code> will
     * return that polygon in the future.
     * 
     * @param polygon
     *            - the new polygon for this pad.
     */
    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    /**
     * Returns the polygon from this pad. If the polygon was explicitly defined
     * at some earlier time, it returns that polygon. If no polygon has been so
     * defined, but a valid polygon has been drawn, it will return that. In all
     * other cases, <code>null</code> is returned.
     * 
     * @return this pad's polygon.
     */
    public Polygon getPolygon() {
        if (points.size() > 2 && polygon == null) {
            int[] x = new int[points.size()];
            int[] y = new int[points.size()];

            for (int i = 0; i < points.size(); i++) {
                x[i] = points.get(i).x;
                y[i] = points.get(i).y;
            }

            Polygon toReturn = new Polygon(x, y, points.size());
            return toReturn;
        }

        else if (polygon != null) {
            return polygon;
        }

        return null;
    }
}
