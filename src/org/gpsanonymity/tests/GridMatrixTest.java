package org.gpsanonymity.tests;

import org.gpsanonymity.data.GridMatrix;
import org.junit.Test;
import org.openstreetmap.josm.data.coor.LatLon;

public class GridMatrixTest {
	@Test
	public void addDistanceTests(){
		LatLon ll = new LatLon(51, 52);
		LatLon lln =GridMatrix.addNorthernDistance(ll, 200);
		System.out.println("N:Distance should be 200 and is:" +ll.greatCircleDistance(lln));
		lln =GridMatrix.addDistance(ll, 200,0);
		System.out.println("N:Distance should be 200 and is:" +ll.greatCircleDistance(lln));
		LatLon llw =GridMatrix.addWesternDistance(ll, 200);
		System.out.println("W:Distance should be 200 and is:" +ll.greatCircleDistance(llw));
		llw =GridMatrix.addDistance(ll,0, 200);
		System.out.println("W:Distance should be 200 and is:" +ll.greatCircleDistance(llw));
		
	}

}
