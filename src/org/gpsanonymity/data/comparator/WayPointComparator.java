package org.gpsanonymity.data.comparator;
import java.util.Comparator;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class WayPointComparator implements Comparator<WayPoint> {

	WayPoint referencePoint=new WayPoint(new LatLon(0, 0));
	public WayPointComparator() {
		super();
	}
	public WayPointComparator(WayPoint referencePoint) {
		super();
		this.referencePoint = new WayPoint(referencePoint);
	}
	public int compare(WayPoint o1, WayPoint o2) {
		double diff = o1.getCoor().greatCircleDistance(referencePoint.getCoor())-o2.getCoor().greatCircleDistance(referencePoint.getCoor()); 
		if (diff>0)
			return 1;
		else if (diff<0)
			return -1;
		else
			return 0;
	}
	
	public void setReferencePoint(WayPoint ref){
		referencePoint = new WayPoint(ref);
	}
}
