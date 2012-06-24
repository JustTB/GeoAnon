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
public class CandleStickPlot extends DiscreteFunctionPlotter {

    public static double COLUMN_WIDTH = 1.0d;
    public static Color BACKGROUND = new Color(220, 220, 220, 128);
    public static Color LINE_COLOUR = Color.BLACK;

    public CandleStickPlot(String[] labels, double[] highs, double[] lows, double[] means) {
        super(labels, highs, lows, means);
    }

    public String getName() {
        return "Candle Stick Plot";
    }

    public void plot(Graph p, Graphics g, int chartWidth, int chartHeight) {

        /**
         * Draw each candlestick
         */
        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
            p.drawCandleStick(g, COLUMN_WIDTH, columnIndex, getHigh(columnIndex), getMean(columnIndex), getLow(columnIndex), LINE_COLOUR, BACKGROUND);
        }

    }

}
