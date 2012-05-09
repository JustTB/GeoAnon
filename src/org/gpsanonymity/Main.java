package org.gpsanonymity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.html.MinimalHTMLWriter;

import org.gpsanonymity.data.GridMatrix;
import org.gpsanonymity.data.TrackCloud;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrack;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.io.GpxReader;

public class Main {
	public static int n = 5;
	public static List<Integer> kList;
	public static double minSpeed=0.5;
	public static List<Double> gridDistanceList;
	public static List<String> inputFileList;
	public static String inputFolder="input/";
	
	public static void initialize(){
		kList=new LinkedList<Integer>();
		kList.add(1);
		kList.add(2);
		kList.add(3);
		kList.add(4);
		kList.add(5);
		kList.add(10);
		kList.add(20);
		kList.add(50);
		kList.add(100);
		kList.add(200);
		gridDistanceList=new LinkedList<Double>();
		gridDistanceList.add(0.5);
		gridDistanceList.add(1.0);
		gridDistanceList.add(2.0);
		gridDistanceList.add(4.0);
		gridDistanceList.add(5.0);
		gridDistanceList.add(6.0);
		gridDistanceList.add(7.0);
		gridDistanceList.add(8.0);
		gridDistanceList.add(10.0);
		gridDistanceList.add(20.0);
		inputFileList = new LinkedList<String>();
		inputFileList.add("Berlin.gpx");
		inputFileList.add(inputFolder+"Leipzig.gpx");
		inputFileList.add(inputFolder+"Toronto.gpx");
		inputFileList.add(inputFolder+"Tokyo.gpx");
		inputFileList.add(inputFolder+"Prignitz.gpx");
		inputFileList.add(inputFolder+"Dolomites.gpx");
	}
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
	public static void simulateTrackGridMerge(){
		initialize();
		for (Integer k : kList) {
			for (Double gridDistance : gridDistanceList){
				for (String inputFile : inputFileList){
					String exportFile= inputFile+"_"+k+"_"+gridDistance+".gpx";
					IOFunctions.exportTracks(
							mergingTracksOnGrid(importTracks(inputFolder+inputFile+".gpx"),
									k,
									gridDistance,
									minSpeed),
							exportFile);
				}
			}
		}
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
	public static List<WayPoint> mergingWaypoints(List<WayPoint> waypoints,int k , double pointRadius) {
		return MergeGPS.mergeWaypoints(waypoints,
						pointRadius,k);
	}
	/**
	 * merges given Tracks
	 * @param tracks given Tracks
	 * @param k factor for k-anonymity
	 * @param pointDensity radius around the points
	 * @param trackDistance distance between to tracks
	 * @param segmentLength 
	 * @return new merged Tracks
	 * @see Main#merginWayPoints(List, int, double)
	 */
	public static List<GpxTrack> mergingTracks(List<GpxTrack> tracks,int k , double pointDensity, double trackDistance, int segmentLength) {
		List<GpxTrack> morePointTracks = MergeGPS.createMoreWaypointsOnTracks(tracks, pointDensity);
		TrackCloud tc= new TrackCloud(morePointTracks,k,trackDistance, segmentLength);
		return tc.getMergedTracks();
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
	 * @param minimalSpeed 
	 * @return
	 * @see Main#mergingWaypointsOnGrid(List, int, double)
	 */
	public static List<GpxTrack> mergingTracksOnGrid(List<GpxTrack> tracks,int k , double gridSize, double minimalSpeed) {
		List<GpxTrack> newTracks= MergeGPS.createMoreWaypointsOnTracks(tracks, gridSize);
		GridMatrix gridMatrix = new GridMatrix(newTracks,k, gridSize,minimalSpeed);
		return gridMatrix.getTracks();
	}

}
