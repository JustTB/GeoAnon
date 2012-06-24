package ac.essex.graphing.swing;

import ac.essex.graphing.plotting.PlotSettings;

import java.awt.event.*;

/**
 * <p>
 * An improvement to the graph panel, which allows the user to
 * interactively drag the panel around to explore the graph
 * easily.
 * </p>
 *
 * <p>
 * Easy to replace the standard Graph panel, just make sure your
 * GUI implements SettingsUpdateListener (called after the graph panel updates)
 * This allows your GUI to be informed when the plotSettings object changes its values.
 * </p>
 *
 * @author Olly Oechsle, University of Essex, Date: 20-Nov-2007
 * @version 1.0
 */
public class InteractiveGraphPanel extends GraphPanel {

    protected int mouseDownX, mouseDownY;
    protected double minX, maxX, minY, maxY;
    protected boolean mouseDown;

    public InteractiveGraphPanel(final SettingsUpdateListener listener) {

        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if (graph != null) {
                    PlotSettings p = graph.plotSettings;
                    mouseDownX = e.getX();
                    mouseDownY = e.getY();
                    minX = p.getMinX();
                    minY = p.getMinY();
                    maxX = p.getMaxX();
                    maxY = p.getMaxY();
                }
                mouseDown = true;
            }

            public void mouseReleased(MouseEvent e) {
                mouseDown = false;
                listener.graphUpdated(graph.plotSettings);
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent e) {

                if (graph != null) {

                    PlotSettings p = graph.plotSettings;

                    double movementX = graph.getPlotWidth(e.getX() - mouseDownX);
                    double movementY = graph.getPlotHeight(e.getY() - mouseDownY);

                    p.setMinX(minX-movementX);
                    p.setMaxX(maxX-movementX);
                    p.setMinY(minY+movementY);
                    p.setMaxY(maxY+movementY);
                   
                    repaint();

                }

            }

        });

        addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                if (graph != null && !mouseDown)  {

                    PlotSettings p = graph.plotSettings;

                    double multiplier;

                    if (e.getWheelRotation() < 0)  {
                        // zoom in
                        multiplier = 0.1;
                    } else {
                        // zoom out
                        multiplier = -0.1;
                    }

                    double xDiff = p.getRangeX() * multiplier;
                    double yDiff = p.getRangeY() * multiplier;

                    p.setMinX(p.getMinX() + xDiff);
                    p.setMaxX(p.getMaxX() - xDiff);

                    p.setMinY(p.getMinY() + yDiff);
                    p.setMaxY(p.getMaxY() - yDiff);

                    listener.graphUpdated(graph.plotSettings);
                    repaint();
                    
                }
            }

        });

    }

}
