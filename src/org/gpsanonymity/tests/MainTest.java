package org.gpsanonymity.tests;

import java.awt.Point;
import java.util.IdentityHashMap;
import java.util.LinkedList;

import org.gpsanonymity.Main;
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
		double pointDensity=5;
		double trackDistance=4;
		boolean ignoreDirection=true;
		double angelAllowance=1;
		//String file = "input/leipzig2.gpx";
		String file = "leipzig_track_example.gpx";
		
		IOFunctions.exportTracks(
				Main.mergingTracks(
						Main.importTracks(file),
						k, pointDensity,trackDistance,segmentLenght,ignoreDirection,angelAllowance),
				"output/GPXMergeTracks_"+k+"_"+pointDensity+"_"+trackDistance+"_"+segmentLenght+".gpx");
	}
	@Test
	public void testHashing(){
		IdentityHashMap<Point,String>	hm = new IdentityHashMap<Point, String>();
		Point a,b;
		a= new Point(1, 1);
		b= new Point(1, 1);
		hm.put(a, "a");
		hm.put(b, "b");
		System.out.println(":::::before");
		System.out.println("a:"+hm.get(a));
		System.out.println("b:"+hm.get(b));
		a.setLocation(1,2);
		System.out.println(":::::after");
		System.out.println("a:"+hm.get(a) + " //a as key has changed, so no value; right");
		System.out.println("b:"+hm.get(b) + " //we putted b at last but not the key");
		System.out.println("keys:" + hm.keySet());
		
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
