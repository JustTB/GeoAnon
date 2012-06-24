package ac.essex.graphing.charts.discrete;

import ac.essex.graphing.plotting.Graph;
import ac.essex.graphing.plotting.DiscreteFunctionPlotter;

import java.awt.*;

/**
 * <p/>
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
 * @author Olly Oechsle, University of Essex, Date: 13-Jun-2007
 * @version 1.0
 */
public class BarChartPlot extends DiscreteFunctionPlotter {

    public static double COLUMN_WIDTH = 1;

    public BarChartPlot(String[] labels, double[] highs, double[] lows, double[] means) {
        super(labels, highs, lows, means);
    }

    public String getName() {
        return "Bar Chart";
    }

    public void plot(Graph p, Graphics g, int chartWidth, int chartHeight) {

        /**
         * Go through each X pixel on the screen from left to right
         */
        for (int x = 0; x < getColumnCount(); x++) {

            p.drawBar(g, COLUMN_WIDTH, x, getHigh(x), Color.RED);

            p.drawBar(g, COLUMN_WIDTH, x, getLow(x), Color.BLUE);

        }

    }

}
