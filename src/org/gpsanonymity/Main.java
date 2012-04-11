package org.gpsanonymity;

import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.GridMatrix;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.io.GpxReader;

public class Main {
	public static int n = 5;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int k=2;
		double gridSize = 2;
		GpxReader importReader= IOFunctions.importGPX("leipzig_track_example.gpx");
		List<WayPoint> waypoints = IOFunctions.getAllWaypoints(importReader);
		List<GpxTrack> tracks = IOFunctions.getAllTracks(importReader);
		
		IOFunctions.exportWayPoints(mergingWaypointsOnGrid(waypoints, k, gridSize), "output/PointGridMergeTest.gpx");
		//IOFunctions.exportTracks(mergingTracksOnGrid(tracks, k, gridSize), "/Distance/output/TrackGridMergeTest.gpx");
	}
	/**
	 * This function only prints and does not sort
	 * [if the referencePoint is wrong the output is also wrong]
	 * 
	 * @param mergedWayPoints list of sorted waypoints
	 * @param referencePoint the point where the list is sorted to
	 */
	public static void printFirstTenWaypointsWithReferencePoint(List<WayPoint> mergedWayPoints,
			WayPoint referencePoint) {
		System.out.println("Number of Waypoints:" + mergedWayPoints.size());
		System.out.println("Nearest Waypoints to "+ referencePoint.toString());
	    for (int i = 0; i < n && i<mergedWayPoints.size(); i++) {
	    	System.out.println((i+1)+". :" + mergedWayPoints.get(i) + "\n		Distance:"
	    			+mergedWayPoints.get(i).getCoor().greatCircleDistance(referencePoint.getCoor())+"m");
		}
		
	}
	public static List<WayPoint> importWayPoints(String filePath) {
		return IOFunctions.getAllWaypoints(
				IOFunctions.importGPX(filePath));
	}
	public static List<GpxTrack> importTracks(String filePath) {
		return new LinkedList<GpxTrack>(IOFunctions.importGPX(
				filePath).data.tracks);
	}

	public static List<WayPoint> merginWayPoints(List<WayPoint> waypoints,int k , double pointRadius) {
		return MergeGPS.eliminateLowerGrades(
				MergeGPS.mergeWaypoints(waypoints,
						pointRadius),
				k);
	}
	public static List<GpxTrack> mergingTracks(List<GpxTrack> tracks,int k , double pointRadius, double TrackDistance) {
		return null;
	}
	public static List<WayPoint> mergingWaypointsOnGrid(List<WayPoint> waypoints,int k , double gridSize) {
		GridMatrix gridMatrix = new GridMatrix(gridSize, waypoints);
		return MergeGPS.eliminateLowerGrades(gridMatrix.getMergedWaypoints(), k);
	}
	public static List<GpxTrack> mergingTracksOnGrid(List<GpxTrack> tracks,int k , double gridSize, boolean follow) {
		GridMatrix gridMatrix = new GridMatrix(tracks,k, gridSize,follow);
		gridMatrix.getTracks();
		//TODO
		return null;
	}

}
