package org.gpsanonymity.tests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.gpsanonymity.data.MergedWayPoint;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.junit.Test;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrack;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class MergeGPSTest {
	@Test
	public void kMeansTest(){
		LinkedList<WayPoint> wps =  IOFunctions.getAllWaypoints(
				IOFunctions.importGPX("leipzig_track_example.gpx"));
		List<MergedWayPoint> mwps = MergeGPS.mergeWithKMeans(wps, wps.size()/3);
		//Assert.assertTrue(mwps.size()==wps.size()/3);
		IOFunctions.exportWayPoints((List<WayPoint>)(List)mwps, "output/TestkMeans.gpx");
	}
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
	@Test
	public void kMeansMoreWayPointsTest(){
		List<GpxTrack> tracks = IOFunctions.getAllTracks(
				IOFunctions.importGPX("leipzig_track_example.gpx"));
		for (GpxTrack gpxTrack : tracks) {
			gpxTrack=MergeGPS.createMoreWaypointsOnTrack(gpxTrack,2);
		}
		List<WayPoint> wps =IOFunctions.getAllWaypointsFromTrack(tracks);
		List<MergedWayPoint> mwps = MergeGPS.mergeWithKMeans(wps, wps.size()/3);
		//Assert.assertTrue(mwps.size()==wps.size()/3);
		IOFunctions.exportWayPoints((List<WayPoint>)(List)mwps, "output/TestkMeansMoreWayPoints.gpx");
	}

}
