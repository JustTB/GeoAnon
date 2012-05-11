package org.gpsanonymity.tests;

import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.MergedWayPoint;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class MergeGPSTest {
	@Test
	public void differenceInAngleIsLowerThanTest(){
		WayPoint wp = new WayPoint(new LatLon(0,0));
		WayPoint wpNorth = new WayPoint(
				MergeGPS.addDistance(wp.getCoor(), 10, 0));
		WayPoint wpWest = new WayPoint(
				MergeGPS.addDistance(wp.getCoor(), 0, 10));
		LinkedList<WayPoint> listNorth = new LinkedList<WayPoint>();
		LinkedList<WayPoint> listWest = new LinkedList<WayPoint>();
		listNorth.add(wp);
		listNorth.add(wpNorth);
		listWest.add(wp);
		listWest.add(wpWest);
		ImmutableGpxTrackSegment segmentNorth = new ImmutableGpxTrackSegment(listNorth);
		ImmutableGpxTrackSegment segmentWest = new ImmutableGpxTrackSegment(listWest);
		Assert.assertTrue(MergeGPS.differenceInAngleIsLowerThan(segmentNorth, segmentWest, Math.PI));//180°
		Assert.assertTrue(MergeGPS.differenceInAngleIsLowerThan(segmentNorth, segmentWest, Math.PI*0.6));//108°
		Assert.assertFalse(MergeGPS.differenceInAngleIsLowerThan(segmentNorth, segmentWest, Math.PI*0.25));//45°
	}
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
