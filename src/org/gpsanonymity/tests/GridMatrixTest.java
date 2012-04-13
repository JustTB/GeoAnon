package org.gpsanonymity.tests;

import org.gpsanonymity.data.GridMatrix;
import org.junit.Test;
import org.openstreetmap.josm.data.coor.LatLon;

public class GridMatrixTest {
	@Test
	public void addDistanceTests(){
		LatLon ll = new LatLon(51, 52);
		LatLon lln =GridMatrix.addNorthernDistance(ll, 200);
		System.out.println("Distance should be 200 and is:" +ll.greatCircleDistance(lln));
		LatLon llw =GridMatrix.addWesternDistance(ll, 200);
		System.out.println("Distance should be 200 and is:" +ll.greatCircleDistance(llw));
	}

}
