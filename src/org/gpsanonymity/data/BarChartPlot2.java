package org.gpsanonymity.data;

import java.awt.Color;
import java.awt.Graphics;

import ac.essex.graphing.charts.discrete.BarChartPlot;
import ac.essex.graphing.plotting.Graph;

public class BarChartPlot2 extends BarChartPlot {

	public BarChartPlot2(String[] labels, double[] highs, double[] lows,
			double[] means) {
		super(labels, highs, lows, means);
	}
	
    public void plot(Graph p, Graphics g, int chartWidth, int chartHeight) {

        /**
         * Go through each X pixel on the screen from left to right
         */
        for (int x = 0; x < getColumnCount(); x++) {

            p.drawBar(g, COLUMN_WIDTH, x, getHigh(x), Color.GRAY);

            p.drawBar(g, COLUMN_WIDTH, x, getLow(x), Color.DARK_GRAY);

        }

    }

}
