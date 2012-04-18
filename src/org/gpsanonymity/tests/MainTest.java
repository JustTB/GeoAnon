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
	@Test
	public void testMergetracksOnGrid1(){
		IOFunctions.exportTracks(
				Main.mergingTracksOnGrid(
						Main.importTracks("leipzig_track_example.gpx"),
						1, 2,0.5),
				"output/GPXMergeTracksOnGrid.1.2.false.gpx");
	}
	@Test
	public void testMergetracksOnGrid2(){
		IOFunctions.exportTracks(
				Main.mergingTracksOnGrid(
						Main.importTracks("leipzig_track_example.gpx"),
						1, 2,0.5),
				"output/GPXMergeTracksOnGrid.1.2.true.gpx");
	}

}
