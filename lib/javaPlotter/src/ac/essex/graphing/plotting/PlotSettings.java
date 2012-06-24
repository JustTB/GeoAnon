package ac.essex.graphing.plotting;

import java.awt.*;
import java.text.Format;
import java.text.DecimalFormat;
import java.io.Serializable;

/**
 *
 * <p>
 * Determines the area that will be plotted, and all the appearance
 * parameters of the graph.
 * </p>
 *
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
 * @author Olly Oechsle, University of Essex, Date: 20-Nov-2007
 * @version 1.0
 */
public class PlotSettings implements Serializable {

    /**
     * Area Parameters
     */
    protected double minX = -5, maxX = 5, minY = -5, maxY = 5;

    /**
     * Margin
     */
    protected int marginTop = 10, marginBottom = 50, marginLeft = 50, marginRight = 20;

    /**
     * The colour of the axes
     */
    protected Color axisColor = Color.BLACK;

    /**
     * The colour of line graphs
     */
    protected Color plotColor = Color.BLACK;

    /**
     * The colour of the background
     */
    protected Color backgroundColor = Color.WHITE;

    /**
     * The colour of the grid
     */
    protected Color gridColor = Color.LIGHT_GRAY;

    /**
     * The font colour (title and labels)
     */
    protected Color fontColor = Color.BLACK;

    /**
     * The length (in pixels) of each noch on the horizontal and vertical axes
     */
    protected int notchLength = 4;

    /**
     * The distance in pixels between the end of a notch and the corresponding label.
     * Increase this value to move text further away from the notch.
     */
    protected int notchGap = 4;

    /**
     * Display the horizontal grid?
     */
    protected boolean horizontalGridVisible = true;

    /**
     * Display the vertical grid?
     */
    protected boolean verticalGridVisible = true;

    /**
     * Formats the numbers displayed beneath each notch.
     */
    protected Format numberFormatter = new DecimalFormat("0.00");

    /**
     * How many notches in the X direction
     */
    protected double gridSpacingX = 0.25;

    /**
     * How many notches in the Y direction
     */
    protected double gridSpacingY = 0.25;

    /**
     * The title of the graph
     */
    protected String title = null;

    public PlotSettings() {
        // use defaults.
    }

    public PlotSettings(double xMin, double xMax, double yMin, double yMax) {
       this.minX = xMin;
       this.maxX = xMax;
       this.minY = yMin;
       this.maxY = yMax;
    }

    /**
     * Gets the minimum X value for plotting
     */
    public double getMinX() {
        return minX;
    }

    /**
     * Sets the minimum X value for plotting.
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * Gets the maximum X value for plotting.
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * Sets the maximum X value for plotting.
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * Gets the minimum Y value for plotting
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Sets the minimum Y value for plotting
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * Gets the maximum Y value for plotting
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * Sets the maximum Y value for plotting
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getRangeX() {
        return maxX - minX;
    }

    public double getRangeY() {
        return maxY - minY;
    }

    /**
     * Gets the top margin
     */
    public int getMarginTop() {
        return marginTop;
    }

    /**
     * Sets the top margin.
     */
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public Color getAxisColor() {
        return axisColor;
    }

    public void setAxisColor(Color axisColor) {
        this.axisColor = axisColor;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getPlotColor() {
        return plotColor;
    }

    public void setPlotColor(Color plotColor) {
        this.plotColor = plotColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public int getNotchLength() {
        return notchLength;
    }

    public void setNotchLength(int notchLength) {
        this.notchLength = notchLength;
    }

    public int getNotchGap() {
        return notchGap;
    }

    public void setNotchGap(int notchGap) {
        this.notchGap = notchGap;
    }

    public boolean isHorizontalGridVisible() {
        return horizontalGridVisible;
    }

    public void setHorizontalGridVisible(boolean horizontalGridVisible) {
        this.horizontalGridVisible = horizontalGridVisible;
    }

    public boolean isVerticalGridVisible() {
        return verticalGridVisible;
    }

    public void setVerticalGridVisible(boolean verticalGridVisible) {
        this.verticalGridVisible = verticalGridVisible;
    }

    public Format getNumberFormatter() {
        return numberFormatter;
    }

    public void setNumberFormatter(Format numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    public double getGridSpacingX() {
        return gridSpacingX;
    }

    public void setGridSpacingX(double gridSpacingX) {
        this.gridSpacingX = gridSpacingX;
    }

    public double getGridSpacingY() {
        return gridSpacingY;
    }

    public void setGridSpacingY(double gridSpacingY) {
        this.gridSpacingY = gridSpacingY;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
