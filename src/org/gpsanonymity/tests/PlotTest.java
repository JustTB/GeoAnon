package org.gpsanonymity.tests; 
 
import ac.essex.graphing.plotting.Graph; 
import ac.essex.graphing.plotting.PlotSettings; 
import ac.essex.graphing.swing.GraphApplication; 
import ac.essex.graphing.charts.continuous.Circle1; 
import ac.essex.graphing.charts.continuous.SineWave; 
import ac.essex.graphing.charts.discrete.CandleStickPlot; 
import ac.essex.graphing.charts.discrete.BarChartPlot; 
 
import java.awt.*; 
 
/** 
 * Demonstrates how to display a graph using JavaPlot. 
 * 
 * <p> 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version, 
 * provided that any use properly credits the author. 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details at http://www.gnu.org 
 * </p> 
 * 
 @author Olly Oechsle, University of Essex 
 @version 1.0 
 */ 
public class PlotTest { 
 
    public static void main(String[] args) { 
 
        /** 
         * Use the Graph Application to display the data 
         */ 
        new GraphApplication(getExampleGraph1()); 
        //new GraphApplication(getExampleGraph2()); 
 
    } 
 
    public static Graph getExampleGraph1() { 
 
        // Create some data 
        String[] labels = new String[]{"Standard GP", "SGGP", "1 Layer MLP", "2 Layer MLP", "3 Layer MLP", "K-NN", "Adaboosted GA", "Kohonen NN"}; 
        double[] lows = new double[]{31.7,76.7,64.1,63.3,63.3,43.3,63.3,40.8}; 
        double[] high = new double[]{75.8,85.8,68.3,68.3,67.5,55.8,70.8,53.3}; 
        double[] means = new double[]{60.84,81.66,66.62,65.8,65.5,50.5,68.3,48.8}; 
 
        // All the graph settings are stored in this object. 
        PlotSettings p = new PlotSettings(); 
         
        p.setMinX(0); 
        p.setMinY(0); 
 
        p.setMaxX(labels.length); 
        p.setMaxY(100); 
 
        p.setGridSpacingX(1); 
        p.setGridSpacingY(10); 
 
        // Set the title 
        p.setTitle("Performance Comparison for the Pasta Experiment"); 
 
        // Create the graph object 
        Graph graph = new Graph(p); 
 
        // Add a CandleStick plot to the graph's function list 
        graph.functions.add(new BarChartPlot(labels, high, means, lows)); 
        //graph.functions.add(new CandleStickPlot(labels, high, means, lows)); 
 
        return graph; 
 
    } 
 
    public static Graph getExampleGraph2() { 
 
        PlotSettings p = new PlotSettings(-2, 2, -1, 1); 
        p.setPlotColor(Color.RED); 
        p.setGridSpacingX(0.5); 
        p.setGridSpacingY(0.5); 
        p.setTitle("Two functions being rendered together"); 
        Graph graph = new Graph(p); 
        graph.functions.add(new Circle1()); 
        graph.functions.add(new SineWave()); 
 
        return graph; 
    } 
 
}