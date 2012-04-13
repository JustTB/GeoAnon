package org.gpsanonymity.tests;

import org.gpsanonymity.Main;
import org.gpsanonymity.io.IOFunctions;
import org.junit.Test;

public class MainTest {
	@Test
	public void testMergeWayPointsOnGrid(){
		IOFunctions.exportWayPoints(
				Main.mergingWaypointsOnGrid(
						Main.importWayPoints("leipzig_track_example.gpx"),
						1, 2),
				"output/GPXMergeTest.gpx");
	}

}
