package ac.essex.graphing.charts.continuous;

import ac.essex.graphing.plotting.ContinuousFunctionPlotter;

/**
 * Formula for plotting a circle: x squared + y squared = radius squared
 */
public class Circle2 extends ContinuousFunctionPlotter {

    public String getName() {
        return "Circle";
    }

    public double getY(double x) {
        return -(x * x);
    }
}
