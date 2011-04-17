package simulation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import simulation.engine.Body;
import simulation.engine.BodySetEvent;
import simulation.engine.BodySetListener;
import simulation.engine.Simulation;

/**
 * This class displays some information about a body, including a picture. This
 * also lets the user edit some of the body's properties like mass and color.
 * 
 * @author Tikhon Jelvis
 * 
 */
@SuppressWarnings("serial")
public class ShapeView extends JPanel {

    private Simulation model;

    private Body body;

    protected JTabbedPane pane;

    // The view panel:
    protected JPanel view;

    protected ShapeTracker tracker;

    // The body-editing panel:
    protected JPanel edit;

    protected JPanel mass_density;

    protected JPanel mass;

    protected JSpinner massSpinner;

    protected JLabel massLabel;

    protected JPanel density;

    protected JSpinner densitySpinner;

    protected JLabel densityLabel;

    protected JPanel bounciness;

    protected JSpinner bouncinessSpinner;

    protected JLabel bouncinessLabel;

    protected JPanel friction;

    protected JSpinner frictionSpinner;

    protected JLabel frictionLabel;

    protected JPanel colorPanel;

    protected ColorButton colorButton;

    protected JLabel colorLabel;

    /**
     * Creates a shape view with no body selected.
     */
    public ShapeView(final Simulation model) {
        super(new BorderLayout());

        this.model = model;

        setPreferredSize(new Dimension(175, getHeight()));
        setDoubleBuffered(true);

        pane = new JTabbedPane();

        view = new JPanel(new BorderLayout());

        tracker = new ShapeTracker(model);
        view.add(tracker);

        pane.addTab("View", view);

        edit = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));

        mass_density = new JPanel();
        mass_density.setBorder(BorderFactory
                .createTitledBorder("Mass / Density"));
        mass_density.setPreferredSize(new Dimension(150, 75));

        mass = new JPanel(new BorderLayout());
        mass.setPreferredSize(new Dimension(135, 20));

        massLabel = new JLabel("Mass: ");
        mass.add(massLabel, BorderLayout.WEST);

        massSpinner = new JSpinner();
        SpinnerModel massSpinnerModel = new SpinnerNumberModel(
                (body != null) ? body.getMass() : 42, 1, Integer.MAX_VALUE, 1);
        massSpinner.setModel(massSpinnerModel);
        massSpinner.setPreferredSize(new Dimension(70, 20));
        massSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (body != null) {
                    body.setMass((Double) massSpinner.getValue());
                }
            }
        });
        mass.add(massSpinner, BorderLayout.EAST);

        mass_density.add(mass);

        density = new JPanel(new BorderLayout());
        density.setPreferredSize(new Dimension(135, 20));

        densityLabel = new JLabel("Density: ");
        density.add(densityLabel, BorderLayout.WEST);

        densitySpinner = new JSpinner();
        SpinnerModel densitySpinnerModel = new SpinnerNumberModel(
                (body != null) ? body.getDensity() : 0.42, 0.001, 4, 0.001);
        densitySpinner.setModel(densitySpinnerModel);
        densitySpinner.setPreferredSize(new Dimension(70, 20));
        densitySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (body != null) {
                    body.setDensity((Double) densitySpinner.getValue());
                }
            }
        });
        density.add(densitySpinner, BorderLayout.EAST);

        mass_density.add(density);

        edit.add(mass_density);

        bounciness = new JPanel(new BorderLayout());
        bounciness.setBorder(BorderFactory.createTitledBorder("Bounciness"));
        bounciness.setPreferredSize(new Dimension(150, 47));

        bouncinessLabel = new JLabel("Bounciness: ");
        bounciness.add(bouncinessLabel, BorderLayout.WEST);

        bouncinessSpinner = new JSpinner();
        SpinnerModel bouncinessSpinnerModel = new SpinnerNumberModel(
                (body != null) ? body.getBounciness() : 0.42, 0, 1, 0.05);
        bouncinessSpinner.setModel(bouncinessSpinnerModel);
        bouncinessSpinner.setPreferredSize(new Dimension(70, 20));
        bouncinessSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (body != null) {
                    body.setBounciness((Double) bouncinessSpinner.getValue());
                }
            }
        });
        bounciness.add(bouncinessSpinner, BorderLayout.EAST);
        edit.add(bounciness);

        friction = new JPanel(new BorderLayout());
        friction.setBorder(BorderFactory.createTitledBorder("Friction"));
        friction.setPreferredSize(new Dimension(150, 47));

        frictionLabel = new JLabel("Friction: ");
        friction.add(frictionLabel, BorderLayout.WEST);

        frictionSpinner = new JSpinner();
        SpinnerModel frictionSpinnerModel = new SpinnerNumberModel(
                (body != null) ? body.getFriction() : 0.42, 0, 1, 0.05);
        frictionSpinner.setModel(frictionSpinnerModel);
        frictionSpinner.setPreferredSize(new Dimension(70, 20));
        frictionSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (body != null) {
                    body.setFriction((Double) frictionSpinner.getValue());
                }
            }
        });
        friction.add(frictionSpinner, BorderLayout.EAST);
        edit.add(friction);

        colorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Color"));
        colorPanel.setPreferredSize(new Dimension(150, 47));

        colorButton = new ColorButton((body != null) ? body.getColor() : null);
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(ShapeView.this,
                        "Choose color", colorButton.getColor());
                if (newColor != null && body != null) {
                    colorButton.setColor(newColor);
                    colorLabel.setText(colorButton.getColorName());
                    body.setColor(newColor);
                    model.update();
                }
            }
        });
        colorPanel.add(colorButton);

        colorLabel = new JLabel(colorButton.getColorName());
        colorPanel.add(colorLabel);

        edit.add(colorPanel);

        pane.addTab("Edit", edit);

        add(pane);

        if (body == null) {
            massSpinner.setEnabled(false);
            bouncinessSpinner.setEnabled(false);
            densitySpinner.setEnabled(false);
            frictionSpinner.setEnabled(false);
            colorButton.setEnabled(false);
        }

        this.model.addBodySetListener(new BodySetListener() {
            @Override
            public void bodyAdded(BodySetEvent e) {
                // Do nothing...
            }

            @Override
            public void bodyRemoved(BodySetEvent e) {
                if (e.getBody() == body) {
                    setBody(null);
                }
            }
        });

        validate();
    }

    /**
     * Sets the body that this view is showing. This body is what will be edited
     * by this pane as well. If <code>body</code> is <code>null</code>, then all
     * the spinners will be disabled until an actual body is set.
     * 
     * @see Body
     * @param body
     *            - the body that this view will show and edit.
     */
    public void setBody(final Body body) {
        this.body = body;
        tracker.setBody(body);

        if (body != null) {
            body.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    massSpinner.getModel().setValue(body.getMass());
                    densitySpinner.getModel().setValue(body.getDensity());
                    bouncinessSpinner.getModel().setValue(body.bounciness());
                    frictionSpinner.getModel().setValue(body.friction());
                    colorButton.setColor(body.getColor());
                    colorLabel.setText(colorButton.getColorName());
                }
            });

            massSpinner.setEnabled(true);
            bouncinessSpinner.setEnabled(true);
            densitySpinner.setEnabled(true);
            frictionSpinner.setEnabled(true);
            colorButton.setEnabled(true);

            massSpinner.getModel().setValue(body.getMass());
            densitySpinner.getModel().setValue(body.getDensity());
            bouncinessSpinner.getModel().setValue(body.bounciness());
            frictionSpinner.getModel().setValue(body.friction());
            colorButton.setColor(body.getColor());
            colorLabel.setText(colorButton.getColorName());
        }

        else {
            massSpinner.setEnabled(false);
            bouncinessSpinner.setEnabled(false);
            densitySpinner.setEnabled(false);
            frictionSpinner.setEnabled(false);
            colorButton.setEnabled(false);
        }
    }

    /**
     * Returns the body that this view is showing. This body is what the view is
     * showing and editing.
     * 
     * @see Body
     * @return the body that this view is showing and editing.
     */
    public Body getBody() {
        return body;
    }
}
