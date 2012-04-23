package org.gpsanonymity.tests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.junit.Test;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrack;

public class MergeGPSTest {
	@Test
	public void createMorePointsTest(){
		List<GpxTrack> tracks = IOFunctions.getAllTracks(IOFunctions.importGPX("leipzig_track_example.gpx"));
		List<GpxTrack> newTracks = new LinkedList<GpxTrack>(); 
		for (GpxTrack gpxTrack : tracks) {
			GpxTrack newTrack = MergeGPS.createMoreWaypointsOnTrack(
					gpxTrack,2);
			newTracks.add(newTrack);
		}
		IOFunctions.exportTracks(newTracks, "output/TestCreateMorePoints.gpx");
	}

}
