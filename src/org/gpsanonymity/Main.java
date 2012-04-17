package org.gpsanonymity;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.GridMatrix;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrack;
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
	/**
	 * Imports all WayPoints from a gpx file, path given by filePath
	 * @param filePath path of the gpx file
	 * @return waypoints from the file
	 */
	public static List<WayPoint> importWayPoints(String filePath) {
		return IOFunctions.getAllWaypoints(
				IOFunctions.importGPX(filePath));
	}
	/**
	 * Imports all tracks from a gpx file, path given by filePath
	 * @param filePath path of the gpx file
	 * @return tracks from the file
	 */
	public static List<GpxTrack> importTracks(String filePath) {
		return new LinkedList<GpxTrack>(IOFunctions.importGPX(
				filePath).data.tracks);
	}
	/**
	 * Merging waypoints in a given radius to one point
	 * @param waypoints waypoints wich should merged
	 * @param k a merged point has to guarantee k points in the mergedpoint
	 * @param pointRadius radius around the first point
	 * @return list of all merged points
	 */
	public static List<WayPoint> merginWayPoints(List<WayPoint> waypoints,int k , double pointRadius) {
		return MergeGPS.eliminateLowerGrades(
				MergeGPS.mergeWaypoints(waypoints,
						pointRadius),
				k);
	}
	/**
	 * merges given Tracks
	 * @param tracks given Tracks
	 * @param k factor for k-anonymity
	 * @param pointRadius radius around the points
	 * @param TrackDistance distance between to tracks
	 * @return new merged Tracks
	 * @see Main#merginWayPoints(List, int, double)
	 */
	public static List<GpxTrack> mergingTracks(List<GpxTrack> tracks,int k , double pointRadius, double TrackDistance) {
		//TODO
		return null;
	}
	/**
	 * Merges Waypoints with a grid over the map. WayPoints in the same grid coordinates will be merged. 
	 * @param waypoints given waypoints
	 * @param k factor for k-anonymity
	 * @param gridSize sitelength of a grid coordinate in meter
	 * @return merged waypoints
	 */
	public static List<WayPoint> mergingWaypointsOnGrid(List<WayPoint> waypoints,int k , double gridSize) {
		GridMatrix gridMatrix = new GridMatrix(gridSize, waypoints);
		return MergeGPS.eliminateLowerGrades(
				gridMatrix.getMergedWaypoints(), k);
	}
	/**
	 * Merges
	 * @param tracks
	 * @param k
	 * @param gridSize
	 * @param follow
	 * @return
	 * @see Main#mergingWaypointsOnGrid(List, int, double)
	 */
	public static List<GpxTrack> mergingTracksOnGrid(List<GpxTrack> tracks,int k , double gridSize, boolean follow) {
		List<GpxTrack> newTracks= new LinkedList<GpxTrack>();
		for (GpxTrack gpxTrack : tracks) {
			newTracks.add(MergeGPS.createMoreWaypoints(gpxTrack, gridSize));
		}
		GridMatrix gridMatrix = new GridMatrix(newTracks,k, gridSize,follow);
		return gridMatrix.getTracks();
	}

}
