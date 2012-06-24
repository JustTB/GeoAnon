package ac.essex.graphing.charts.continuous;

import ac.essex.graphing.plotting.ContinuousFunctionPlotter;

/**
 * <p>Allows you to plot a Sine Wave.</p>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version,
 * provided that any use properly credits the author.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details at http://www.gnu.org
 *
 @author Olly Oechsle, University of Essex
 @version 1.0
*/
public class SineWave extends ContinuousFunctionPlotter {

    public double getY(double x) {
        return Math.sin(x);
    }

    public String getName() {
        return "Sine Wave";
    }

}
