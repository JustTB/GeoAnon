// License: GPL. Copyright 2007 by Immanuel Scholz and others
package org.openstreetmap.josm.data.coor;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static org.openstreetmap.josm.tools.I18n.trc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;

/**
 * LatLon are unprojected latitude / longitude coordinates.
 *
 * This class is immutable.
 *
 * @author Imi
 */
public class LatLon extends Coordinate {


    /**
     * Minimum difference in location to not be represented as the same position.
     * The API returns 7 decimals.
     */
    public static final double MAX_SERVER_PRECISION = 1e-7;
    public static final int    MAX_SERVER_DIGITS = 7;

    private static DecimalFormat cDmsMinuteFormatter = new DecimalFormat("00");
    private static DecimalFormat cDmsSecondFormatter = new DecimalFormat("00.0");
    private static DecimalFormat cDmMinuteFormatter = new DecimalFormat("00.000");
    public static final DecimalFormat cDdFormatter;
    static {
        // Don't use the localized decimal separator. This way we can present
        // a comma separated list of coordinates.
        cDdFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.UK);
        cDdFormatter.applyPattern("###0.0000000");
    }

    /**
     * Replies true if lat is in the range [-90,90]
     *
     * @param lat the latitude
     * @return true if lat is in the range [-90,90]
     */
    public static boolean isValidLat(double lat) {
        return lat >= -90d && lat <= 90d;
    }

    /**
     * Replies true if lon is in the range [-180,180]
     *
     * @param lon the longitude
     * @return true if lon is in the range [-180,180]
     */
    public static boolean isValidLon(double lon) {
        return lon >= -180d && lon <= 180d;
    }

    /**
     * Replies true if lat is in the range [-90,90] and lon is in the range [-180,180]
     *
     * @return true if lat is in the range [-90,90] and lon is in the range [-180,180]
     */
    public boolean isValid() {
        return isValidLat(lat()) && isValidLon(lon());
    }

    public static double toIntervalLat(double value) {
        if (value < -90)
            return -90;
        if (value > 90)
            return 90;
        return value;
    }

    /**
     * Returns a valid OSM longitude [-180,+180] for the given extended longitude value.
     * For example, a value of -181 will return +179, a value of +181 will return -179.
     * @param lon A longitude value not restricted to the [-180,+180] range.
     */
    public static double toIntervalLon(double value) {
        if (isValidLon(value))
            return value;
        else {
            int n = (int) (value + Math.signum(value)*180.0) / 360;
            return value - n*360.0;
        }
    }

    /**
     * Replies the coordinate in degrees/minutes/seconds format
     */
    public static String dms(double pCoordinate) {

        double tAbsCoord = Math.abs(pCoordinate);
        int tDegree = (int) tAbsCoord;
        double tTmpMinutes = (tAbsCoord - tDegree) * 60;
        int tMinutes = (int) tTmpMinutes;
        double tSeconds = (tTmpMinutes - tMinutes) * 60;

        return tDegree + "\u00B0" + cDmsMinuteFormatter.format(tMinutes) + "\'"
        + cDmsSecondFormatter.format(tSeconds) + "\"";
    }

    public static String dm(double pCoordinate) {

        double tAbsCoord = Math.abs(pCoordinate);
        int tDegree = (int) tAbsCoord;
        double tMinutes = (tAbsCoord - tDegree) * 60;
        return tDegree + "\u00B0" + cDmMinuteFormatter.format(tMinutes) + "\'";
    }

    public LatLon(double lat, double lon) {
        super(lon, lat);
    }

    public LatLon(LatLon coor) {
        super(coor.lon(), coor.lat());
    }

    public double lat() {
        return y;
    }

    public final static String SOUTH = trc("compass", "S");
    public final static String NORTH = trc("compass", "N");
    public String latToString(CoordinateFormat d) {
        switch(d) {
        case DECIMAL_DEGREES: return cDdFormatter.format(y);
        case DEGREES_MINUTES_SECONDS: return dms(y) + ((y < 0) ? SOUTH : NORTH);
        case NAUTICAL: return dm(y) + ((y < 0) ? SOUTH : NORTH);
        case EAST_NORTH: return cDdFormatter.format(Main.getProjection().latlon2eastNorth(this).north());
        default: return "ERR";
        }
    }

    public double lon() {
        return x;
    }

    public final static String WEST = trc("compass", "W");
    public final static String EAST = trc("compass", "E");
    public String lonToString(CoordinateFormat d) {
        switch(d) {
        case DECIMAL_DEGREES: return cDdFormatter.format(x);
        case DEGREES_MINUTES_SECONDS: return dms(x) + ((x < 0) ? WEST : EAST);
        case NAUTICAL: return dm(x) + ((x < 0) ? WEST : EAST);
        case EAST_NORTH: return cDdFormatter.format(Main.getProjection().latlon2eastNorth(this).east());
        default: return "ERR";
        }
    }

    /**
     * @return <code>true</code> if the other point has almost the same lat/lon
     * values, only differing by no more than
     * 1 / {@link #MAX_SERVER_PRECISION MAX_SERVER_PRECISION}.
     */
    public boolean equalsEpsilon(LatLon other) {
        double p = MAX_SERVER_PRECISION / 2;
        return Math.abs(lat()-other.lat()) <= p && Math.abs(lon()-other.lon()) <= p;
    }

    /**
     * @return <code>true</code>, if the coordinate is outside the world, compared
     * by using lat/lon.
     */
    public boolean isOutSideWorld() {
        Bounds b = Main.getProjection().getWorldBoundsLatLon();
        return lat() < b.getMin().lat() || lat() > b.getMax().lat() ||
                lon() < b.getMin().lon() || lon() > b.getMax().lon();
    }

    /**
     * @return <code>true</code> if this is within the given bounding box.
     */
    public boolean isWithin(Bounds b) {
        return b.contains(this);
    }

    /**
     * Computes the distance between this lat/lon and another point on the earth.
     * Uses Haversine formular.
     * @param other the other point.
     * @return distance in metres.
     */
    public double greatCircleDistance(LatLon other) {
        double R = 6378135;
        double sinHalfLat = sin(toRadians(other.lat() - this.lat()) / 2);
        double sinHalfLon = sin(toRadians(other.lon() - this.lon()) / 2);
        double d = 2 * R * asin(
                sqrt(sinHalfLat*sinHalfLat +
                        cos(toRadians(this.lat()))*cos(toRadians(other.lat()))*sinHalfLon*sinHalfLon));
        // For points opposite to each other on the sphere,
        // rounding errors could make the argument of asin greater than 1
        // (This should almost never happen.)
        if (java.lang.Double.isNaN(d)) {
            System.err.println("Error: NaN in greatCircleDistance");
            d = PI * R;
        }
        return d;
    }

    /**
     * Returns the heading, in radians, that you have to use to get from
     * this lat/lon to another.
     *
     * (I don't know the original source of this formula, but see
     * http://math.stackexchange.com/questions/720/how-to-calculate-a-heading-on-the-earths-surface
     * for some hints how it is derived.)
     *
     * @param other the "destination" position
     * @return heading in the range 0 <= hd < 2*PI
     */
    public double heading(LatLon other) {
        double hd = atan2(sin(toRadians(this.lon() - other.lon())) * cos(toRadians(other.lat())),
                cos(toRadians(this.lat())) * sin(toRadians(other.lat())) -
                sin(toRadians(this.lat())) * cos(toRadians(other.lat())) * cos(toRadians(this.lon() - other.lon())));
        hd %= 2 * PI;
        if (hd < 0) {
            hd += 2 * PI;
        }
        return hd;
    }

    /**
     * Returns this lat/lon pair in human-readable format.
     *
     * @return String in the format "lat=1.23456 deg, lon=2.34567 deg"
     */
    public String toDisplayString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(5);
        return "lat=" + nf.format(lat()) + "\u00B0, lon=" + nf.format(lon()) + "\u00B0";
    }

    public LatLon interpolate(LatLon ll2, double proportion) {
        return new LatLon(this.lat() + proportion * (ll2.lat() - this.lat()),
                this.lon() + proportion * (ll2.lon() - this.lon()));
    }

    public LatLon getCenter(LatLon ll2) {
        return new LatLon((this.lat() + ll2.lat())/2.0, (this.lon() + ll2.lon())/2.0);
    }
    /**
     * Gives a LatLon back, m meters in north/south direction
     * (this is only a simpel approach)
     *@param m is the distance in meters, could also be negative for south direction
     **/
    public LatLon getPointFromNorthSouth(double m) {

        return new LatLon(this.x +m/110574,this.y);
    }
    /**
     * Gives a LatLon back, m meters in west/east direction
     * (this is only a simpel approach)
     * @param m is the distance in meters, could also be negative for east direction
     **/
    public LatLon getPointFromEastWest(double m) {

        return new LatLon(this.x,this.y +m/(111320*Math.cos(this.x)));
    }

    @Override public String toString() {
        return "LatLon[lat="+lat()+",lon="+lon()+"]";
    }

    /**
     * Returns the value rounded to OSM precisions, i.e. to
     * LatLon.MAX_SERVER_PRECISION
     *
     * @return rounded value
     */
    public static double roundToOsmPrecision(double value) {
        //return Math.round(value / MAX_SERVER_PRECISION) * MAX_SERVER_PRECISION; // causes tiny rounding errors (see LatLonTest)
    	//in my project should not be rounded
    	return value;
    }

    /**
     * Returns the value rounded to OSM precisions, i.e. to
     * LatLon.MAX_SERVER_PRECISION. The result is guaranteed to be exact, but at a great cost.
     * This function is about 1000 times slower than roundToOsmPrecision(), use it with caution.
     *
     * @return rounded value
     */
    public static double roundToOsmPrecisionStrict(double value) {
        double absV = Math.abs(value);
        int numOfDigits = MAX_SERVER_DIGITS + (absV < 1 ? 0 : (absV < 10 ? 1 : (absV < 100 ? 2 : 3)));
        return BigDecimal.valueOf(value).round(new MathContext(numOfDigits)).doubleValue();
    }

    /**
     * Replies a clone of this lat LatLon, rounded to OSM precisions, i.e. to
     * MAX_SERVER_PRECISION
     *
     * @return a clone of this lat LatLon
     */
    public LatLon getRoundedToOsmPrecision() {
        return new LatLon(
                roundToOsmPrecision(lat()),
                roundToOsmPrecision(lon())
                );
    }

    /**
     * Replies a clone of this lat LatLon, rounded to OSM precisions, i.e. to
     * MAX_SERVER_PRECISION
     *
     * @return a clone of this lat LatLon
     */
    public LatLon getRoundedToOsmPrecisionStrict() {
        return new LatLon(
                roundToOsmPrecisionStrict(lat()),
                roundToOsmPrecisionStrict(lon())
                );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = java.lang.Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = java.lang.Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        if (java.lang.Double.doubleToLongBits(x) != java.lang.Double.doubleToLongBits(other.x))
            return false;
        if (java.lang.Double.doubleToLongBits(y) != java.lang.Double.doubleToLongBits(other.y))
            return false;
        return true;
    }
}
