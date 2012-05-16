package org.gpsanonymity.tests;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;

import org.gpsanonymity.Main;
import org.gpsanonymity.data.MergedWayPoint;
import org.gpsanonymity.io.IOFunctions;
import org.junit.Assert;
import org.junit.Test;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

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
		int k=3;
		int segmentLenght=2;
		double pointDensity=3;
		double trackDistance=2;
		
		IOFunctions.exportTracks(
				Main.mergingTracks(
						Main.importTracks("input/leipzig2.gpx"),
						k, pointDensity,trackDistance,segmentLenght),
				"output/GPXMergeTracks_"+k+"_"+pointDensity+"_"+trackDistance+"_"+segmentLenght+".gpx");
	}
	@Test
	public void testHashing(){
		HashMap<Point,String>	hm = new HashMap<Point, String>();
		Point a,b;
		a= new Point(1,2);
		b= new Point(1,2);
		hm.put(a, "a");
		hm.put(b, "b");
		System.out.println(":::::before");
		System.out.println("a:"+hm.get(a));
		System.out.println("b:"+hm.get(b));
		a.setLocation(3, 4);
		System.out.println(":::::after");
		System.out.println("b:"+hm.get(b));
		System.out.println("a:"+hm.get(a));
		System.out.println("keys:" + hm.keySet());
		System.out.println("location b: "+b.getY() +" "+ b.getX());
		
	}
	@Test
	public void testEquals(){
		LinkedList<WayPoint> wp1s = new LinkedList<WayPoint>();
		LinkedList<WayPoint> wp2s = new LinkedList<WayPoint>();
		WayPoint wp1 = new WayPoint(new LatLon(1,1));
		WayPoint wp2 = new WayPoint(new LatLon(1,1));
		wp1s.add(wp1);
		wp2s.add(wp2);
		wp1s.add(new WayPoint(new LatLon(1,2)));
		wp2s.add(new WayPoint(new LatLon(1,2)));
		ImmutableGpxTrackSegment a = new ImmutableGpxTrackSegment(wp1s);
		ImmutableGpxTrackSegment b = new ImmutableGpxTrackSegment(wp2s);
		Assert.assertTrue(wp1.equals(wp2));
		Assert.assertTrue(a.equals(b));
	}
}
