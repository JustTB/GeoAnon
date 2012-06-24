package ac.essex.graphing.plotting;

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
public abstract class DiscreteFunctionPlotter extends Plotter{

    protected String[] labels;
    protected double[] highs, means, lows;

    public DiscreteFunctionPlotter(String[] labels, double[] highs, double[] means, double[] lows) {
        this.labels = labels;
        this.highs = highs;
        this.lows = lows;
        this.means = means;
        if (highs.length != labels.length) System.err.println("Mismatch between highs length and labels length");
        if (means.length != labels.length) System.err.println("Mismatch between means length and labels length");
        if (lows.length != labels.length) System.err.println("Mismatch between lows length and labels length");
    }

    public int getColumnCount() {
        return labels.length;
    }

    public String getLabel(int i)  {
        try {
            return labels[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "-";
        }
    }

    public double getHigh(int i) {
        return highs[i];
    }

    public double getLow(int i) {
        return lows[i];
    }

    public double getMean(int i) {
        return means[i];
    }

}
