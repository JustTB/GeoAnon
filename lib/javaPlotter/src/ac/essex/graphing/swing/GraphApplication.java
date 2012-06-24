package ac.essex.graphing.swing;

import ac.essex.graphing.plotting.Graph;
import ac.essex.graphing.plotting.PlotSettings;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Small application demonstrating the use of the graph panel in a swing gui.
 *
 * @author Olly Oechsle, University of Essex, Date: 05-Jun-2007
 * @version 1.01 - Added suport for the Interactive Graph Panel.
 */
public class GraphApplication extends JFrame implements ActionListener, SettingsUpdateListener {

    protected JButton save, exit, update;
    protected JTextField minX, minY, maxX, maxY;

    protected GraphPanel graphPanel;

    public GraphApplication(Graph graph) {


        save = new JButton("Save");
        save.addActionListener(this);

        exit = new JButton("Exit");
        exit.addActionListener(this);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        minX = new JTextField(String.valueOf(graph.plotSettings.getMinX()));
        minY = new JTextField(String.valueOf(graph.plotSettings.getMinY()));
        maxX = new JTextField(String.valueOf(graph.plotSettings.getMaxX()));
        maxY = new JTextField(String.valueOf(graph.plotSettings.getMaxY()));

        toolbar.add(new JLabel("X: "));
        toolbar.add(minX);
        toolbar.add(new JLabel("-"));
        toolbar.add(maxX);
        toolbar.add(new JLabel(", Y:"));
        toolbar.add(minY);
        toolbar.add(new JLabel("-"));
        toolbar.add(maxY);

        update = new JButton("Update");
        update.addActionListener(this);
        toolbar.add(update);

        toolbar.add(save);
        toolbar.add(exit);

        // add the panel to the middle of the BorderLayout, it will fill the window.
        graphPanel = new InteractiveGraphPanel(this);

        // Make sure Java Exits when the close button is clicked
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        }
        );

        // Add the toolbar and graph to the frame
        Container c = getContentPane();
        c.add(toolbar, BorderLayout.SOUTH);
        c.add(graphPanel, BorderLayout.CENTER);

        // default size of the window, the Graph Panel will be slightly smaller.
        setSize(640, 480);      

        // window title
        setTitle(Graph.VERSION);

        // show the Window
        setVisible(true);

        graphPanel.setGraph(graph);

    }


    public void graphUpdated(PlotSettings settings) {
        minX.setText(String.valueOf(settings.getMinX()));
        minY.setText(String.valueOf(settings.getMinY()));
        maxX.setText(String.valueOf(settings.getMaxX()));
        maxY.setText(String.valueOf(settings.getMaxY()));
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == update) {
            Graph g = graphPanel.getGraph();
            g.plotSettings.setMinX(Double.parseDouble(minX.getText()));
            g.plotSettings.setMaxX(Double.parseDouble(maxX.getText()));
            g.plotSettings.setMinY(Double.parseDouble(minY.getText()));
            g.plotSettings.setMaxY(Double.parseDouble(maxY.getText()));
            graphPanel.repaint();
        }

        // Saves an image of the graph to disk.
        if (e.getSource() == save) {
            JFileChooser filechooser = new JFileChooser(System.getProperty("user.home"));
            filechooser.setDialogTitle("Save Graph Image");
            filechooser.setSelectedFile(new File(filechooser.getCurrentDirectory(), "graph.png"));
            filechooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    String extension = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
                    if (f.isDirectory()) return true;
                    if (extension.equals("bmp")) return true;
                    if (extension.equals("jpg")) return true;
                    if (extension.equals("png")) return true;
                    return false;
                }

                public String getDescription() {
                    return "Image Files: jpg, png, bmp";
                }
            });
            int action = filechooser.showSaveDialog(this);
            if (action == JFileChooser.APPROVE_OPTION) {
                File f = filechooser.getSelectedFile();
                try {
                    String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                    javax.imageio.ImageIO.write(graphPanel.getImage(), extension, f);
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(this, "Could not save image: " + err.getMessage());
                }
            }
        }

        // Exits the program.
        if (e.getSource() == exit) {
            System.exit(0);
        }

    }

}
