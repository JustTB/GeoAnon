package org.gpsanonymity;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.MinimalAreaCloud;
import org.gpsanonymity.data.MinimalAreaExtendedCloud;
import org.gpsanonymity.data.GridMatrix;
import org.gpsanonymity.data.KMeansCloud;
import org.gpsanonymity.data.SegmentCloud;
import org.gpsanonymity.data.SegmentClusterCloud;
import org.gpsanonymity.data.Statistician;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.io.Importer;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class Main {
	public static int n = 5;
	public static List<Integer> kList;
	public static double minSpeed=0.5;
	public static List<Double> distanceList;
	public static List<Double> pointDensityList;
	public static List<String> inputFileList;
	public static String inputFolder="input/";
	public static LinkedList<Double> angelAllowanceList;
	public static LinkedList<Integer> intoleranceList;
	public static LinkedList<Double> minimalAreaDistanceList;
	private static HashMap<Bounds, String> boundsAndFilenames;
	private static String tempFilename;
	
	public static void initialize(){
		kList=new LinkedList<Integer>();
		kList.add(2);
		kList.add(3);
		kList.add(4);
		kList.add(5);
		kList.add(6);
		kList.add(7);
		kList.add(8);
		kList.add(9);
		kList.add(10);
		kList.add(20);
		kList.add(50);
		kList.add(100);
		kList.add(200);
		distanceList=new LinkedList<Double>();
		distanceList.add(0.5);
		distanceList.add(1.0);
		distanceList.add(2.0);
		distanceList.add(4.0);
		distanceList.add(5.0);
		distanceList.add(6.0);
		distanceList.add(7.0);
		distanceList.add(8.0);
		distanceList.add(10.0);
		distanceList.add(20.0);
		pointDensityList= new LinkedList<Double>();
		pointDensityList.add(0.0);
		pointDensityList.add(1.0);
		pointDensityList.add(3.0);
		pointDensityList.add(2.0);
		pointDensityList.add(4.0);
		pointDensityList.add(8.0);
		pointDensityList.add(16.0);
		angelAllowanceList = new LinkedList<Double>();
		angelAllowanceList.add(0.0);
		angelAllowanceList.add(0.25);
		angelAllowanceList.add(0.5);
		angelAllowanceList.add(0.75);
		angelAllowanceList.add(1.0);
		intoleranceList= new LinkedList<Integer>();
		intoleranceList.add(1);
		intoleranceList.add(2);
		intoleranceList.add(3);
		intoleranceList.add(4);
		intoleranceList.add(5);
		intoleranceList.add(10);
		minimalAreaDistanceList=new LinkedList<Double>();
		minimalAreaDistanceList.add(0.0);
		minimalAreaDistanceList.add(1.0);
		minimalAreaDistanceList.add(2.0);
		minimalAreaDistanceList.add(4.0);
		minimalAreaDistanceList.add(8.0);
		inputFileList = new LinkedList<String>();
		inputFileList.add("output/Belluno.dat");
		inputFileList.add("output/Berlin.dat");
		inputFileList.add("output/McPomm.dat");
		//////////////////////Bounds and Filenames ///////////////////////////
		boundsAndFilenames = new HashMap<Bounds, String>();
		//Belluno
		double minLat=45.940;
		double minLon=11.646;
		double maxLat=46.279;
		double maxLon=12.328;
		Bounds bounds = new Bounds(new LatLon(minLat,minLon),new LatLon(maxLat,maxLon));
		String filename= "input/Belluno/Belluno.gpx";
		boundsAndFilenames.put(bounds, filename);
		//Berlin
		minLat=52.286;
		minLon=12.914;
		maxLat=52.701;
		maxLon=13.867;
		bounds = new Bounds(new LatLon(minLat,minLon),new LatLon(maxLat,maxLon));
		filename= "input/Berlin/Berlin.gpx";
		boundsAndFilenames.put(bounds, filename);
		//South of Cairo
		minLat=27.595;
		minLon=30.333;
		maxLat=29.907;
		maxLon=31.508;
		bounds = new Bounds(new LatLon(minLat,minLon),new LatLon(maxLat,maxLon));
		filename= "input/SoCairo/SoCairo.gpx";
		boundsAndFilenames.put(bounds, filename);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize();
		//downloadData();
		simulateTrackGridMerge();
		simulateTrackCliqueCloakMerge();
		simulateTrackCliqueCloakExtendedMerge();
		simulateTrackSegmentCloudMerge();
		simulateTrackKMeansMerge();
	}
	private static void downloadData(){
		tempFilename= "output/temp.gpx";
		if (boundsAndFilenames==null){
			initialize();
		}
		for (Bounds key : boundsAndFilenames.keySet()) {
			IOFunctions.getDataFromOSMWithCutting(key, boundsAndFilenames.get(key), tempFilename);
		}
	}
	private static void simulateTrackGridMerge(){
		for(String path : inputFileList){
			System.out.println("Simulating"+ path);
			Importer importer= new Importer(path);
			while(importer.hasnext()){
				importer.next();
				if(importer.current().size()!=0){
					GridMatrix gridmatrix=null;
					Statistician statistician = new Statistician();
					for(Double gridSize : distanceList){
						System.out.println("Gridsize: "+ gridSize);
						for(Integer k: kList){
							System.out.println("K: "+ k);
							if(gridSize>2){
								String statisticianPath = "output/stats/"
										+ "GridMerge"
										+ path.substring(path.lastIndexOf("/")+1).replace(".dat", "")
										+ importer.getCurrentX()
										+ "_"
										+ importer.getCurrentY()
										+ "_"
										+ "k"+k
										+ "_"
										+ "gS" +gridSize
										+ "_"
										+ "mS" + minSpeed
										+".ps";
								if(!(new File(statisticianPath)).exists()){
									if(gridmatrix==null){
										gridmatrix=mergingTracksOnGrid(importer.current(), k, gridSize, minSpeed,statistician);
									}else{
										gridmatrix.initAgainWithHigherK(k, statistician);
									}
									statistician.write(statisticianPath);
								}
							}
						}
					}
				}
			}
		}
	}
	private static void simulateTrackKMeansMerge(){
		for(String path : inputFileList){
			Importer importer= new Importer(path);
			while(importer.hasnext()){
				importer.next();
				if(importer.current().size()!=0){
					KMeansCloud kMeansCloud=null;
					Statistician statistician = new Statistician();
					for(Double pointDensity :pointDensityList){
						if(pointDensity>7 || pointDensity==0){
							for(Integer k: kList){
								String statisticianPath = "output/stats/"
										+ "KMeansMerge"
										+ path.substring(path.lastIndexOf("/")+1).replace(".dat", "")
										+ importer.getCurrentX()
										+ "_"
										+ importer.getCurrentY()
										+ "_"
										+ "k"+k
										+ "_"
										+ "pD" +pointDensity
										+".ps";
								if(!(new File(statisticianPath)).exists()){
									if(kList.get(0)==k){
										kMeansCloud=mergingTracksWithKMeans(importer.current(), k,pointDensity, statistician);
									}else{
										kMeansCloud.initAgainWithHigherK(k, statistician);
									}

									statistician.write(statisticianPath);
								}
							}
						}
					}
				}
			}
		}
	}
	private static void simulateTrackSegmentCloudMerge(){
		for(String path : inputFileList){
			Importer importer= new Importer(path);
			while(importer.hasnext()){
				importer.next();
				if(importer.current().size()!=0){
					SegmentCloud segmentCloud=null;
					Statistician statistician = new Statistician();
					for(Double trackDistance : distanceList){
						for(Double pointDensity :pointDensityList){
							for(Double angelAllowance: angelAllowanceList){
								for(Integer k: kList){
									if(trackDistance<pointDensity && pointDensity>=2){
										String statisticianPath = "output/stats/"
												+ "SegmentCloudMerge"
												+ path.substring(path.lastIndexOf("/")+1).replace(".dat", "")
												+ importer.getCurrentX()
												+ "_"
												+ importer.getCurrentY()
												+ "_"
												+ "k"+k
												+ "_"
												+ "tD" +trackDistance
												+ "_"
												+ "pD" +pointDensity
												+ "_"
												+ "iD" +true
												+ "_"
												+ "aA" +angelAllowance
												+".ps";
										if(!(new File(statisticianPath)).exists()){
											if(kList.get(0)==k){
												segmentCloud=mergingTracksWithSegmentCloud(importer.current(), k,pointDensity, trackDistance, true,angelAllowance,statistician);
											}else{
												segmentCloud.initAgainWithHigherK(k, statistician);
											}

											statistician.write(statisticianPath);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	private static void simulateTrackCliqueCloakMerge(){
		for(String path : inputFileList){
			Importer importer= new Importer(path);
			while(importer.hasnext()){
				importer.next();
				if(importer.current().size()!=0){
					for(Double pointDensity :pointDensityList){
						MinimalAreaCloud cliqueCloakCloud=null;
						Statistician statistician = new Statistician();
						for(Integer k: kList){
							String statisticianPath = "output/stats/"
									+ "CliqueCloakMerge"
									+ path.substring(path.lastIndexOf("/")+1).replace(".dat", "")
									+ importer.getCurrentX()
									+ "_"
									+ importer.getCurrentY()
									+ "_"
									+ "k"+k
									+ "_"
									+ "pD" +pointDensity
									+".ps";
							if(!(new File(statisticianPath)).exists()){
								if(cliqueCloakCloud==null){
									cliqueCloakCloud=mergingTracksWithCliqueCloak(importer.current(), k,pointDensity, statistician);
								}else{
									cliqueCloakCloud.initAgainWithHigherK(k, statistician);
								}
								statistician.write(statisticianPath);
							}
						}
					}
				}
			}
		}
	}
	private static void simulateTrackCliqueCloakExtendedMerge(){
		for(String path : inputFileList){
			Importer importer= new Importer(path);
			while(importer.hasnext()){
				importer.next();
				if(importer.current().size()!=0){
					MinimalAreaCloud cliqueExtendedCloakCloud=null;
					Statistician statistician = new Statistician();
					for(Double pointDensity :pointDensityList){
						for(Integer intolerance : intoleranceList){
							for(Double minimalAreaDistance : minimalAreaDistanceList){
								for(Integer k: kList){
									if (k>intolerance){
										String statisticianPath = "output/stats/"
												+ "CliqueCloakExtendedMerge"
												+ path.substring(path.lastIndexOf("/")+1).replace(".dat", "")
												+ importer.getCurrentX()
												+ "_"
												+ importer.getCurrentY()
												+ "_"
												+ "k"+k
												+ "_"
												+ "pD" +pointDensity
												+ "_"
												+ "mAD" +minimalAreaDistance
												+".ps";
										if(!(new File(statisticianPath)).exists()){
											if(cliqueExtendedCloakCloud==null){
												cliqueExtendedCloakCloud=mergingTracksWithCliqueCloakExtended(importer.current(), k,pointDensity,intolerance,minimalAreaDistance, statistician);
											}else{
												cliqueExtendedCloakCloud.initAgainWithHigherK(k, statistician);
											}

											statistician.write(statisticianPath);
										}
									}
								}
							}
						}
					}
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
	 * merges given Tracks by cutting them into peaces and find similar peaces in a radius of trackDistance
	 * @param tracks given Tracks
	 * @param k factor for k-anonymity
	 * @param pointDensity radius around the points
	 * @param trackDistance distance between to tracks
	 * @param segmentLength 
	 * @param angelAllowance 
	 * @param ignoreDirection 
	 * @param statistician 
	 * @return new merged Tracks
	 * @see Main#merginWayPoints(List, int, double)
	 */
	public static SegmentCloud mergingTracksWithSegmentCloud(List<GpxTrack> tracks,int k , double pointDensity, double trackDistance, boolean ignoreDirection, double angelAllowance, Statistician statistician) {
		List<GpxTrack> morePointTracks;
		if (pointDensity!=0){
			System.out.println("Status: Create more Waypoints");
			morePointTracks = MergeGPS.createMoreWaypointsOnTracks(tracks, pointDensity);
		}else{
			morePointTracks = new LinkedList<GpxTrack>(tracks);
		}
		SegmentCloud sc= new SegmentCloud(morePointTracks,k,trackDistance, ignoreDirection, angelAllowance, statistician);
		return sc;
	}
	/**
	 * merges given Tracks by cutting them into peaces and find segment clusters
	 * @param tracks given Tracks
	 * @param k factor for k-anonymity
	 * @param pointDensity radius around the points
	 * @param trackDistance distance between to tracks
	 * @param segmentLength 
	 * @param angelAllowance 
	 * @param ignoreDirection 
	 * @param statistician 
	 * @param angleWeight 
	 * @param distanceWeight 
	 * @return new merged Tracks
	 * @see Main#merginWayPoints(List, int, double)
	 */
	public static SegmentClusterCloud mergingTracksWithSegmentClusters(List<GpxTrack> tracks,int k , double pointDensity, double trackDistance, int segmentLength, boolean ignoreDirection, double angelAllowance, Statistician statistician, double angleWeight, double distanceWeight) {
		List<GpxTrack> morePointTracks;
		if (pointDensity!=0){
			System.out.println("Status: Create more Waypoints");
			morePointTracks = MergeGPS.createMoreWaypointsOnTracks(tracks, pointDensity);
		}else{
			morePointTracks = new LinkedList<GpxTrack>(tracks);
		}
		SegmentClusterCloud scc= new SegmentClusterCloud(morePointTracks,k,trackDistance, ignoreDirection, angelAllowance,statistician,angleWeight, distanceWeight);
		return scc;
	}
	/**
	 * Merges Waypoints with a grid over the map. WayPoints in the same grid coordinates will be merged. 
	 * @param waypoints given waypoints
	 * @param k factor for k-anonymity
	 * @param gridSize sitelength of a grid coordinate in meter
	 * @return merged waypoints
	 */
	public static List<WayPoint> mergingWaypointsOnGrid(List<WayPoint> waypoints,int k , double gridSize) {
		Statistician statistician = new Statistician();
		GridMatrix gridMatrix = new GridMatrix(gridSize, waypoints,statistician);
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
	public static GridMatrix mergingTracksOnGrid(List<GpxTrack> tracks,int k , double gridSize, double minimalSpeed,Statistician statistician) {
		List<GpxTrack> newTracks= MergeGPS.createMoreWaypointsOnTracks(tracks, gridSize/2);
		GridMatrix gridMatrix = new GridMatrix(newTracks,k, gridSize,minimalSpeed,statistician);
		return gridMatrix;
	}
	/**
	 * merges given Tracks by cutting them into peaces and find similar peaces in a radius of trackDistance
	 * @param tracks given Tracks
	 * @param k factor for k-anonymity
	 * @param pointDensity radius around the points
	 * @param trackDistance distance between to tracks
	 * @param segmentLength 
	 * @param angelAllowance 
	 * @param ignoreDirection 
	 * @param statistician 
	 * @return new merged Tracks
	 * @see Main#merginWayPoints(List, int, double)
	 */
	public static KMeansCloud mergingTracksWithKMeans(List<GpxTrack> tracks,int k , double pointDensity, Statistician statistician) {
		List<GpxTrack> morePointTracks;
		if (pointDensity!=0){
			System.out.println("Status: Create more Waypoints");
			morePointTracks = MergeGPS.createMoreWaypointsOnTracks(tracks, pointDensity);
		}else{
			morePointTracks = new LinkedList<GpxTrack>(tracks);
		}
		KMeansCloud kmc= new KMeansCloud(morePointTracks,k, statistician);
		return kmc;
	}
	public static MinimalAreaCloud mergingTracksWithCliqueCloak(List<GpxTrack> tracks,int k , double pointDensity, Statistician statistician) {
		List<GpxTrack> morePointTracks;
//		if (pointDensity!=0){
//			System.out.println("Status: Create more Waypoints");
//			morePointTracks = MergeGPS.createMoreWaypointsOnTracks(tracks, pointDensity);
//		}else{
			morePointTracks = new LinkedList<GpxTrack>(tracks);
//		}
		MinimalAreaCloud ccc= new MinimalAreaCloud(morePointTracks,k, statistician);
		return ccc;
	}
	public static MinimalAreaExtendedCloud mergingTracksWithCliqueCloakExtended(List<GpxTrack> tracks,int k , double pointDensity, int intolerance, double minimalAreaDistance, Statistician statistician) {
		List<GpxTrack> morePointTracks;
//		if (pointDensity!=0){
//			System.out.println("Status: Create more Waypoints");
//			morePointTracks = MergeGPS.createMoreWaypointsOnTracks(tracks, pointDensity);
//		}else{
			morePointTracks = new LinkedList<GpxTrack>(tracks);
//		}
		MinimalAreaExtendedCloud ccc= new MinimalAreaExtendedCloud(morePointTracks,k,intolerance,minimalAreaDistance, statistician);
		return ccc;
	}
}
