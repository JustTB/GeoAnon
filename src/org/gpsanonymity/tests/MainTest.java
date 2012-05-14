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
						3, 2),
				"output/GPXMergeWaypointsOnGrid_1_2.gpx");
	}
	@Test
	public void testMergeWayPoints(){
		IOFunctions.exportWayPoints(
				Main.mergingWaypoints(
						Main.importWayPoints("leipzig_track_example.gpx"),
						3, 2),
				"output/GPXMergeWaypoint_1_2.gpx");
	}
	@Test
	public void testMergeTracksOnGrid(){
		IOFunctions.exportTracks(
				Main.mergingTracksOnGrid(
						Main.importTracks("input/leipzig.gpx"),
						3, 4,0.5),
				"output/GPXMergeTracksOnGrid_1_2_0.5.gpx");
	}
	@Test
	public void testMergeTracks(){
		int k=5;
		int segmentLenght=2;
		double pointDensity=7;
		double trackDistance=6;
		
		IOFunctions.exportTracks(
				Main.mergingTracks(
						Main.importTracks("input/leipzig.gpx"),
						k, pointDensity,trackDistance,segmentLenght),
				"output/GPXMergeTracks_"+k+"_"+pointDensity+"_"+trackDistance+"_"+segmentLenght+".gpx");
	}

}
