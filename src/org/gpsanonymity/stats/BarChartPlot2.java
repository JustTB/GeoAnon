package org.gpsanonymity.stats;

import java.awt.Color;
import java.awt.Graphics;

import ac.essex.graphing.charts.discrete.BarChartPlot;
import ac.essex.graphing.plotting.DiscreteFunctionPlotter;
import ac.essex.graphing.plotting.Graph;

public class BarChartPlot2 extends DiscreteFunctionPlotter {

	public static double COLUMN_WIDTH = 1;
	
	public BarChartPlot2(String[] labels, double[] highs, double[] means,
			double[] lows) {
		super(labels, highs, means,lows);
	}
	
    public void plot(Graph p, Graphics g, int chartWidth, int chartHeight) {

        /**
         * Go through each X pixel on the screen from left to right
         */
        for (int x = 0; x < getColumnCount(); x++) {
            p.drawBar(g, COLUMN_WIDTH, x, getHigh(x), Color.YELLOW);
            p.drawBar(g, COLUMN_WIDTH, x, getMean(x), Color.YELLOW);
            p.drawBar(g, COLUMN_WIDTH, x, getLow(x), Color.DARK_GRAY);

        }

    }

	@Override
	public String getName() {
		return "Bar Chart";
	}

}
