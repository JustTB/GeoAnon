package org.gpsanonymity.data;
import java.util.Collection;

import org.openstreetmap.josm.data.gpx.WayPoint;


public class DistanceMatrix extends HalfMatrix<WayPoint, Double> {
	public DistanceMatrix(Collection<WayPoint> firstWaypoints ,Collection<WayPoint> secondWaypoints){
		for (WayPoint w1 : secondWaypoints) {
			for (WayPoint w2 : firstWaypoints) {
				if (!containsKey(w1,w2)){
					Double dist = w1.getCoor().greatCircleDistance(w2.getCoor()); 
					put(w1,w2, dist);
				}
			}
		}
	}
}
