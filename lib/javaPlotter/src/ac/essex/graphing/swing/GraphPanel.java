package ac.essex.graphing.swing;

import ac.essex.graphing.plotting.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Wraps around the graph object making it easy to integrate
 * into Java Swing applications.
 *
 * @author Olly Oechsle, University of Essex, Date: 13-Jun-2007
 * @version 1.00
 */
public class GraphPanel extends JPanel {

    /**
     * The graph object renders charts and graphs.
     */
    protected Graph graph;

    /**
     * Initialises the panel with a graph object
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint();
    }

    /**
     * Draws the graph directly onto the JPanel
     */
    public void paintComponent(Graphics g) {
       super.paintComponent(g);
       if (graph != null) graph.draw(g, getWidth(), getHeight());
    }

    /**
     * Returns an image of the graph which can be saved to disk.
     */
    public BufferedImage getImage() {
       return graph != null? graph.getImage(getWidth(), getHeight()) : null;
    }

    /**
     * Provides access to the graph object.
     * @return
     */
    public Graph getGraph() {
        return graph;
    }

}
