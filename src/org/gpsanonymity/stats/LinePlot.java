package org.gpsanonymity.stats;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ac.essex.graphing.charts.discrete.BarChartPlot;
import ac.essex.graphing.plotting.DiscreteFunctionPlotter;
import ac.essex.graphing.plotting.Graph;
import ac.essex.graphing.plotting.Plotter;

public class LinePlot extends DiscreteFunctionPlotter {
	public LinePlot(String[] labels, double[] highs, double[] means,
			double[] lows) {
		super(labels, highs, means, lows);
	}

	public int maxX;
	public int maxY;
	HashMap<Double, List<Double>> highs = new HashMap<Double,List<Double>>();
	HashMap<Double, List<Double>> lows = new HashMap<Double,List<Double>>();
	List<String> labels = new LinkedList<String>();

	public void addHighLowTo(String label,Double key,Double low,Double high){
		if(!labels.contains(label)){
			labels.add(label);
			maxX=labels.size();
		}
		if(!highs.containsKey(key)){
			highs.put(key, new LinkedList<Double>());
			lows.put(key, new LinkedList<Double>());
		}
		highs.get(key).add(high);
		lows.get(key).add(low);
		if(high>maxY){
			maxY=high.intValue();
		}
	}
    public void plot(Graph p, Graphics g, int chartWidth, int chartHeight) {
    	List<Color> colors = new LinkedList<Color>();
    	colors.add(Color.BLACK);
    	colors.add(Color.GRAY);
    	colors.add(Color.BLUE);
    	colors.add(Color.RED);
    	colors.add(Color.ORANGE);
    	colors.add(Color.MAGENTA);
    	colors.add(Color.GREEN);
    	
    	int keycounter =-1;
    	Color temp = g.getColor();
    	LinkedList<Double> keys = new LinkedList<Double>(highs.keySet());
    	Collections.sort(keys);
    	for(Double key: keys){
    		keycounter++;
    		for (int x = 0; x+1 < labels.size(); x++) {
    			
    			((Graphics2D)g).setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] {5.0f}, 0.0f));
    			g.setColor(colors.get(keycounter));
    			try{
    			p.drawLine(g, x, highs.get(key).get(x).doubleValue(), x+1, highs.get(key).get(x+1).doubleValue());
    			}catch(IndexOutOfBoundsException e){
    				//dont draw
    			}catch(NullPointerException e){
    				
    			}
    			((Graphics2D)g).setStroke(new BasicStroke(2));
    			g.setColor(colors.get(keycounter));
    			try{
    			p.drawLine(g, x, lows.get(key).get(x).doubleValue(), x+1, lows.get(key).get(x+1).doubleValue());
    			}catch(IndexOutOfBoundsException e){
    				//dont draw
    			}catch(NullPointerException e){
    				
    			}
    			
    			
    			
    		}
    	}
    	g.setColor(temp);
    }

	@Override
	public String getName() {
		return "Waypoint Plotter";
	}
	@Override
	public int getColumnCount() {
        return labels.size();
    }
	@Override
	public String getLabel(int i)  {
        try {
            return labels.get(i);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

}
