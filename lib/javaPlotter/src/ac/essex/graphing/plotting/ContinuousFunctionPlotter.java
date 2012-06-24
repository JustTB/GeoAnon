package ac.essex.graphing.plotting;

import ac.essex.graphing.plotting.Plotter;
import ac.essex.graphing.plotting.Graph;

import java.awt.*;

/**
 *
 * Graphable Function
 * Basic interface allowing mathematical functions to talk to the Graph renderer.
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
public abstract class ContinuousFunctionPlotter extends Plotter {

    public abstract double getY(double x);

    public void plot(Graph graph, Graphics g, int chartWidth, int chartHeight) {

        /**
         * Record the last two points. Plotting works by drawing lines between consecutive points
         * This ensures there are no gaps.
         */
        double prevX = 0, prevY = 0;

        /**
         * Flag to make sure the first point is not drawn (there is not previous point to connect the dots to)
         */
        boolean first = true;

        double xRange = graph.plotSettings.getRangeX();

        /**
         * Plot for every pixel going across the chart
         */
        for (int ax = 0; ax < chartWidth; ax++) {

            // figure out what X is
            double x = graph.plotSettings.getMinX() + ((ax / (double) chartWidth) * xRange);

            /**
             * For this value of X, get the value of Y (via the abstract method)
             */
            double y = getY(x);

            /**
             * Draw a line between two points
             */
            if (!first && y <= graph.plotSettings.getMaxY() && y >= graph.plotSettings.getMinY()) graph.drawLine(g, prevX, prevY, x, y);

            /**
             * Remember the last two values
             */
            prevX = x;
            prevY = y;

            /**
             * To stop the first point being drawn
             */
            first = false;

        }

    }

}
