package org.gpsanonymity.data.comparator;
import java.util.Comparator;

import org.openstreetmap.josm.data.gpx.WayPoint;


public class CoordinateWayPointComparator implements Comparator<WayPoint> {

	public CoordinateWayPointComparator() {
		super();
	}
	public int compare(WayPoint o1, WayPoint o2) {
		double latDiff = o1.getCoor().getY()-o2.getCoor().getY();
		double lonDiff = o1.getCoor().getX()-o2.getCoor().getX();
		if (latDiff>0 || (latDiff==0 && lonDiff>0))
			return 1;
		else if (latDiff<0 || (latDiff==0 && lonDiff<0))
			return -1;
		else
			return 0;
	}
}
