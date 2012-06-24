package ac.essex.graphing.swing;

import ac.essex.graphing.plotting.Graph;
import ac.essex.graphing.plotting.PlotSettings;
import ac.essex.graphing.Demo;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Small application demonstrating the use of the graph panel in a swing gui.
 *
 * @author Olly Oechsle, University of Essex, Date: 05-Jun-2007
 * @version 1.01 - Added suport for the Interactive Graph Panel.
 */
public class GraphApplet extends JApplet implements SettingsUpdateListener {

    protected JLabel minX, minY, maxX, maxY;

    protected GraphPanel graphPanel;

    protected DecimalFormat f = new DecimalFormat("0.000");    

    public GraphApplet() {

        Graph graph = Demo.getExampleGraph2();
        
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        minX = new JLabel(f.format(graph.plotSettings.getMinX()));
        minY = new JLabel(f.format(graph.plotSettings.getMinY()));
        maxX = new JLabel(f.format(graph.plotSettings.getMaxX()));
        maxY = new JLabel(f.format(graph.plotSettings.getMaxY()));

        statusBar.add(new JLabel("X: "));
        statusBar.add(minX);
        statusBar.add(new JLabel(":"));
        statusBar.add(maxX);
        statusBar.add(new JLabel(", Y:"));
        statusBar.add(minY);
        statusBar.add(new JLabel(":"));
        statusBar.add(maxY);

        // add the panel to the middle of the BorderLayout, it will fill the window.
        graphPanel = new InteractiveGraphPanel(this);

        // Add the toolbar and graph to the frame
        Container c = getContentPane();
        c.add(statusBar, BorderLayout.SOUTH);
        c.add(graphPanel, BorderLayout.CENTER);

        // default size of the window, the Graph Panel will be slightly smaller.
        setSize(640, 480);

        // show the Window
        setVisible(true);

        graphPanel.setGraph(graph);

    }

    public void graphUpdated(PlotSettings settings) {
        minX.setText(f.format(settings.getMinX()));
        minY.setText(f.format(settings.getMinY()));
        maxX.setText(f.format(settings.getMaxX()));
        maxY.setText(f.format(settings.getMaxY()));
    }

}
