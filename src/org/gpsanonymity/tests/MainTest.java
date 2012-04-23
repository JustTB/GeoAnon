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
				"output/GPXMergeWaypointsOnGrid_1_2.gpx");
	}
	@Test
	public void testMergeWayPoints(){
		IOFunctions.exportWayPoints(
				Main.mergingWaypoints(
						Main.importWayPoints("leipzig_track_example.gpx"),
						1, 2),
				"output/GPXMergeWaypoint_1_2.gpx");
	}
	@Test
	public void testMergeTracksOnGrid(){
		IOFunctions.exportTracks(
				Main.mergingTracksOnGrid(
						Main.importTracks("leipzig_track_example.gpx"),
						1, 2,0.5),
				"output/GPXMergeTracksOnGrid_1_2_0.5.gpx");
	}
	@Test
	public void testMergeTracks(){
		IOFunctions.exportTracks(
				Main.mergingTracks(
						Main.importTracks("leipzig_track_example.gpx"),
						1, 2,0.5,2),
				"output/GPXMergeTracks_1_2_2.gpx");
	}

}
