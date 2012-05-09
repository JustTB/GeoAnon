package org.gpsanonymity.merge;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.gpsanonymity.data.DistanceMatrix;
import org.gpsanonymity.data.MergedWayPoint;
import org.gpsanonymity.data.comparator.WayPointComparator;
import org.openstreetmap.josm.actions.AddNodeAction;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrack;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class MergeGPS {
	static public List<WayPoint> mergeWaypoints(List<WayPoint> givenWaypoints, double accuracy, int grade){
		return eliminateLowerGrades(mergeWaypoints(givenWaypoints, accuracy),grade);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<WayPoint> eliminateLowerGrades(
			List<MergedWayPoint> mergeWaypoints
			,int grade){
		return (List<WayPoint>)(List)eliminateLowerGradesMerged(mergeWaypoints, grade);
	}
	public static List<MergedWayPoint> eliminateLowerGradesMerged(
			List<MergedWayPoint> mergeWaypoints
			,int grade) {
		LinkedList<MergedWayPoint> resultlist = new LinkedList<MergedWayPoint>();
		for (MergedWayPoint mergedWayPoint : mergeWaypoints) {
			if (mergedWayPoint.getGrade() >= grade){
				resultlist.add(mergedWayPoint);
			}
		}
		return resultlist;
	}
	/**
	 * Simple approach
	 * @param waypoints
	 * @param accuracy
	 * @return
	 */
	static public List<MergedWayPoint> mergeWaypoints(Collection<WayPoint> waypoints, double accuracy) {
		LinkedList<WayPoint> tempWaypoints = new LinkedList<WayPoint>(waypoints);
		LinkedList<MergedWayPoint> mergedWaypoints = new LinkedList<MergedWayPoint>();
		int procentBlocks = Math.round(tempWaypoints.size()/100);
		int procents=100;
		while(!tempWaypoints.isEmpty()){
			if(Math.round(tempWaypoints.size()/procentBlocks)==procents){
				System.out.println("WayPoints Merged: "+ procents + "%");
				procents--;
			}
			MergedWayPoint mwp = new MergedWayPoint(tempWaypoints.getFirst());
			WayPointComparator wpc = new WayPointComparator();
			wpc.setReferencePoint(tempWaypoints.getFirst());
			Collections.sort(tempWaypoints, wpc);
			for (int i = 1; i < tempWaypoints.size() 
					&& (tempWaypoints.get(i).getCoor()
							.greatCircleDistance(
									tempWaypoints.getFirst().getCoor()
					)<=accuracy) ; i++) {
				mwp.addWayPoint(tempWaypoints.get(i));
				tempWaypoints.remove(i);
				i--;// correct index cause we removed the point
			}
			mwp.calculateNewCoordinates();
			mwp.calculateNewDate();
			tempWaypoints.removeFirst();
			mergedWaypoints.add(mwp);
		}

		return mergedWaypoints;

	}

	public static LatLon calculateCentroid(List<WayPoint> cluster) {
		if (cluster.isEmpty()){
			return null;
		}
		double lat =0;
		double lon = 0;
		for (Iterator<WayPoint> iterator = cluster.iterator(); iterator.hasNext();) {
			WayPoint wp = (WayPoint) iterator.next();
			lat+=wp.getCoor().getY();//Lat
			lon+=wp.getCoor().getX();//Lon
		}
		return new LatLon(lat/cluster.size(),lon/cluster.size());
	}

	public static String simpleGeneralizeDate(LinkedList<WayPoint> sourceWaypoints) {
		//FIXME:something goes wrong
		//sort by Date
		if (!sourceWaypoints.isEmpty()){
			Collections.sort(sourceWaypoints);
			Date first = sourceWaypoints.getFirst().getTime();
			Date last = sourceWaypoints.getLast().getTime();
			SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
			SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM");
			SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
			SimpleDateFormat dateFormatHour = new SimpleDateFormat("HH");
			SimpleDateFormat dateFormatMinute = new SimpleDateFormat("mm");
			SimpleDateFormat dateFormatSecond = new SimpleDateFormat("ss");
			SimpleDateFormat dateFormatMilli = new SimpleDateFormat("SSS");
			String result = new String();
			//Year
			if (	dateFormatYear.format(first)
					.compareTo(
							dateFormatYear.format(last))
							==0
					){
				result+=dateFormatYear.format(last)+":";
			}else{
				result+="XXXX:";
			}
			//Month
			if (	dateFormatMonth.format(first)
					.compareTo(
							dateFormatMonth.format(last))
							==0
					){
				result+=dateFormatMonth.format(last)+":";
			}else{
				result+="XX:";
			}
			//Day
			if (	dateFormatDay.format(first)
					.compareTo(
							dateFormatDay.format(last))
							==0
					){
				result+=dateFormatDay.format(last)+":";
			}else{
				result+="XX:";
			}
			//Hour
			if (	dateFormatHour.format(first)
					.compareTo(
							dateFormatHour.format(last))
							==0
					){
				result+=dateFormatHour.format(last)+":";
			}else{
				result+="XX:";
			}				
			//Minute
			if (	dateFormatMinute.format(first)
					.compareTo(
							dateFormatMinute.format(last))
							==0
					){
				result+=dateFormatMinute.format(last)+":";
			}else{
				result+="XX:";
			}
			//Second
			if (	dateFormatSecond.format(first)
					.compareTo(
							dateFormatSecond.format(last))
							==0
					){
				result+=dateFormatSecond.format(last)+":";
			}else{
				result+="XX:";
			}
			//Milli
			if (	dateFormatMilli.format(first)
					.compareTo(
							dateFormatMilli.format(last))
							==0
					){
				result+=dateFormatMilli.format(last)+":";
			}else{
				result+="XXX";
			}
			return result;
		}
		return null;
	}
	public static List<GpxTrack> createMoreWaypointsOnTracks(Collection<GpxTrack> tracks, double maxdistance){
		List<GpxTrack> newTracks= new LinkedList<GpxTrack>();
		for (GpxTrack gpxTrack : tracks) {
			newTracks.add(MergeGPS.createMoreWaypointsOnTrack(gpxTrack, maxdistance));
		}
		return newTracks;
	}
	public static GpxTrack createMoreWaypointsOnTrack(GpxTrack gpxTrack, double maxdistance) {
		LinkedList<WayPoint> mergedSegments=new LinkedList<WayPoint>();
		for (GpxTrackSegment gpxTrackSegment : gpxTrack.getSegments()) {
			mergedSegments.addAll(gpxTrackSegment.getWayPoints());
		}
		LinkedList<WayPoint> tempWaypoints = mergedSegments;
		for (int i = 1; i < tempWaypoints.size(); i++) {
			WayPoint firstWP = tempWaypoints.get(i-1);
			WayPoint secondWP = tempWaypoints.get(i);
			LatLon firstLL = tempWaypoints.get(i-1).getCoor();
			LatLon secondLL = tempWaypoints.get(i).getCoor();
			if (firstLL
					.greatCircleDistance(
							secondLL)
							>maxdistance){
				WayPoint tempWayPoint = new WayPoint(
						new LatLon(
								secondLL
								.getCenter(
										firstLL)
										)
						);
				tempWayPoint.time=(firstWP.time+secondWP.time)/2;
				tempWaypoints.add(i, tempWayPoint);
				i--;
			}
		}
		LinkedList<WayPoint> resultseq = tempWaypoints;
		LinkedList<Collection<WayPoint>> tracklist= new LinkedList<Collection<WayPoint>>();
		tracklist.add(resultseq);
		return new ImmutableGpxTrack(tracklist, new HashMap<String, Object>());
	}
	/**
	 * Simple approach
	 * @param waypoints
	 * @param accuracy
	 * @return
	 */
	static public LinkedList<MergedWayPoint> mergeTrackSegments(LinkedList<WayPoint> trackSeqs1,LinkedList<WayPoint> trackSeqs2, double hausDorffAccuracy, double pointAccuracy) {
		if (hausDorffDistance(trackSeqs1, trackSeqs2)<=hausDorffAccuracy){
			return mergeTrackSegments(trackSeqs1, trackSeqs2,pointAccuracy);
		}
	
		return null;//mergedWaypoints;
	
	}
	public static List<MergedWayPoint> mergeWithKMeans(List<WayPoint> list, int k){
		List<WayPoint> oldClusterPoints,clusterPoints=getRandomPoints(list,k);
		List<WayPoint> cluster;
		//DistanceMatrix distanceMartix = new DistanceMatrix(list, list);
		do{
			cluster= makeCluster(clusterPoints, list);
			oldClusterPoints=clusterPoints;
			clusterPoints=cluster;
		}while(!haveSameCoord(oldClusterPoints,clusterPoints));
		return makeMergeCluster(clusterPoints,list);
				
	}
	/**
	 * Gives better clusters of the waypoints of list back
	 * starting by clusterPoints (if clusterPoints is not the best)
	 * NOTE: for faster calculations {@link MergeGPS.makeCluster}
	 * @param clusterPoints starting clusterPoints
	 * @param list list of all points
	 * @param distanceMartix distancematrix for all distances
	 * @return new clusterPoints with sourcePoints in it 
	 */
	private static List<MergedWayPoint> makeMergeCluster(
			List<WayPoint> clusterPoints, List<WayPoint> list) {
		List<MergedWayPoint> result= new LinkedList<MergedWayPoint>();
		//initialize result
		for (int i = 0; i < clusterPoints.size(); i++) {
			result.add(new MergedWayPoint(clusterPoints.get(i)));
		}
		for (WayPoint wayPoint : list) {
			//for each waypoint find nearest cluster point
			int index =findNearestPointIndex(wayPoint,clusterPoints);
			MergedWayPoint resultPoint = result.get(index);
			resultPoint.addWayPoint(wayPoint, true);
		}
		return result;
	}
	private static List<WayPoint> getRandomPoints(List<WayPoint> list,
			int clusterNumber) {
		if (clusterNumber<list.size()){
			List<WayPoint> result = new LinkedList<WayPoint>();
			Random generator= new Random();
			while(result.size()< clusterNumber) {
				int index =generator.nextInt(list.size());
				WayPoint randomWayPoint = list.get(index);
				if(!result.contains(randomWayPoint)){
					result.add(list.get(index));
				}

			}
			return result;
		}else{
			return null;
		}
	}
	/**
	 * Gives better clusters of the waypoints of list back
	 * starting by clusterPoints (if clusterPoints is not the best)
	 * @param clusterPoints starting clusterPoints
	 * @param list list of all points
	 * @param distanceMartix distancematrix for all distances
	 * @return new clusterPoints 
	 */
	private static List<WayPoint> makeCluster(
			List<WayPoint> clusterPoints, List<WayPoint> list) {
		List<List<WayPoint>> clusterGroups= new LinkedList<List<WayPoint>>();
		//initialize result
		for (int i = 0; i < clusterPoints.size(); i++) {
			clusterGroups.add(new LinkedList<WayPoint>());
		}
		for (WayPoint wayPoint : list) {
			//for each waypoint find nearest cluster point
			int index =findNearestPointIndex(wayPoint,clusterPoints);
			List<WayPoint> cluster = clusterGroups.get(index);
			cluster.add(wayPoint);
		}
		LinkedList<WayPoint> result = new LinkedList<WayPoint>();
		//for each clusterGroup find new centroid
		for (List<WayPoint> cluster : clusterGroups) {
			if(!cluster.isEmpty()){
				result.add(new WayPoint(MergeGPS.calculateCentroid(cluster)));
			}
		}
		return result;
	}
	private static int findNearestPointIndex(WayPoint wayPoint, List<WayPoint> list) {
		double distance=Double.MAX_VALUE;
		int result=-1;
		for (int i = 0; i < list.size(); i++) {
			Double currentDistance;
			/*
			if (distanceMartix!=null){
				currentDistance = distanceMartix.getValue(list.get(i), wayPoint);
				if(currentDistance==null){
					currentDistance = list.get(i).getCoor().greatCircleDistance(wayPoint.getCoor());
				}
			}else{*/
				currentDistance = list.get(i).getCoor().greatCircleDistance(wayPoint.getCoor());
			/*}*/
			if (currentDistance<distance){
				distance=currentDistance;
				result=i;
			}
		}
		return result;
	}
	private static boolean haveSameCoord(List<WayPoint> oldClusterPoints,
			List<WayPoint> clusterPoints) {
		if(clusterPoints.size()==oldClusterPoints.size()){
			for (int i = 0; i < clusterPoints.size(); i++) {
				LatLon point=clusterPoints.get(i).getCoor();
				LatLon point2=oldClusterPoints.get(i).getCoor();
				if (!point.equals(point2)){
					return false;
				}
			}
		}
			
		return true;
	}
	private static LinkedList<MergedWayPoint> mergeTrackSegments(
			LinkedList<WayPoint> trackSeqs1,
			LinkedList<WayPoint> trackSeqs2, double pointAccuracy) {
		LinkedList<WayPoint> mergedTrackSegments= new LinkedList<WayPoint>(trackSeqs1);
		LinkedList<WayPoint> tempTrackSegments= new LinkedList<WayPoint>(trackSeqs2);
		MergedWayPoint mwp;
		//sorting content of second List
		WayPointComparator wpc = new WayPointComparator();
		for (int j = 0; j < mergedTrackSegments.size(); j++) {
			mwp = new MergedWayPoint(mergedTrackSegments.get(j));
			wpc.setReferencePoint(mergedTrackSegments.get(j));
			Collections.sort(tempTrackSegments, wpc);
			for (int i = 0; i < tempTrackSegments.size() && (tempTrackSegments.get(i).getCoor()
					.greatCircleDistance(
							mergedTrackSegments.getFirst().getCoor()
							)<=pointAccuracy) ; i++) {
				mwp.addWayPoint(mergedTrackSegments.get(i));
				tempTrackSegments.remove(i);
				i--;
			}
			mergedTrackSegments.set(j, mwp);
		}
		//TODO
		return null;
	}
	static public Double hausDorffDistance(Collection<? extends WayPoint> trackSeqs1, Collection<? extends WayPoint> trackSeqs2) {
		
		LinkedList<WayPoint> tempList1= new LinkedList<WayPoint>(trackSeqs1);
		LinkedList<WayPoint> tempList2= new LinkedList<WayPoint>(trackSeqs2);
		//get min
		LinkedList<Double> allDiffs1 = new LinkedList<Double>();
		LinkedList<Double> allDiffs2 = new LinkedList<Double>();
		//FIXME: here dynamic programming? maybe only for bigger segments
		for (WayPoint wayPoint : tempList1) {
			allDiffs1.add(getMaxDifference(wayPoint,tempList2));
		}
		for (WayPoint wayPoint : tempList2) {
			allDiffs2.add(getMaxDifference(wayPoint,tempList1));
		}
		double max1=getMax(allDiffs1);
		double max2=getMax(allDiffs2);
		return Math.max(max1,max2);
	}
	
	private static double getMax(LinkedList<Double> allDiffs1) {
		double result=0;
		for (Double double1 : allDiffs1) {
			if(double1>result){
				result=double1;
			}
		}
		return result;
	}
	private static Double getMaxDifference(WayPoint wayPoint,
			LinkedList<WayPoint> tempList2) {
		double result=0;
		for (WayPoint wayPoint2 : tempList2) {
			double distance = wayPoint.getCoor().greatCircleDistance(wayPoint2.getCoor());
			if(distance>result){
				result=distance;
			}
		}
		return result;
	}
	static private void sort(WayPoint wayPoint, LinkedList<WayPoint> tempList2) {
		WayPointComparator wpc = new WayPointComparator();
		wpc.setReferencePoint(wayPoint);
		Collections.sort(tempList2, wpc);
		
	}
	/**
	 * Simple approach
	 * @param waypoints
	 * @param accuracy
	 * @return
	 */
	static public LinkedList<MergedWayPoint> rasterMergeWaypoints(LinkedList<WayPoint> waypoints, double gridSize) {
		LinkedList<WayPoint> tempWaypoints = new LinkedList<WayPoint>(waypoints);
		LinkedList<MergedWayPoint> mergedWaypoints = new LinkedList<MergedWayPoint>();
		Bounds border = getBounds(waypoints);
		getBoundsGrid(border);
		
	
		return mergedWaypoints;
	
	}
	private static void getBoundsGrid(Bounds border) {
		
		
	}
	private static Bounds getBounds(List<WayPoint> list) {
		if (list!=null){
		Bounds result = new Bounds(list.get(0).getCoor());
		for (WayPoint wayPoint : list) {
			result.extend(wayPoint.getCoor());
		}
		return result;
		}else
			return null;
	}
	public static List<List<MergedWayPoint>> createSegments(List<MergedWayPoint> mergedWayPoints, int k) {
		List<List<MergedWayPoint>> segs= new LinkedList<List<MergedWayPoint>>();
		LinkedList<MergedWayPoint> mergedWayPointsList = new LinkedList<MergedWayPoint>(mergedWayPoints);
		List<MergedWayPoint> list = new LinkedList<MergedWayPoint>();
		MergedWayPoint temp = mergedWayPointsList.getFirst();
		MergedWayPoint neighbor;
		while(!mergedWayPointsList.isEmpty()){
			list.add(temp);
			neighbor = temp.getOneNotMarkedNeighbor(k);
			if(neighbor==null){
				mergedWayPointsList.remove(temp);
				if(list.size()>1){//no one point seqs
					segs.add(list);
				}
				if(mergedWayPointsList.isEmpty()){
					break;
				}else{
					temp=mergedWayPointsList.getFirst();
					list = new LinkedList<MergedWayPoint>();
				}
			}else{
				temp.colorConnection(neighbor);
				temp=neighbor;
			}
		}
		return segs;
	}
	public static List<GpxTrack> buildTracks(List<MergedWayPoint> mergedWayPointsList, int k) {
		List<List<MergedWayPoint>> segs;
		LinkedList<GpxTrack> tracks = new LinkedList<GpxTrack>();
		Collection<Collection<WayPoint>> virtualSeq;
		segs=createSegments(mergedWayPointsList, k);
		for (List<MergedWayPoint> seg : segs) {
			virtualSeq = new LinkedList<Collection<WayPoint>>();
			//FIXME: not type safe but should be fast
			virtualSeq.add((List<WayPoint>)(List)seg);
			tracks.add(new ImmutableGpxTrack(virtualSeq,new HashMap<String, Object>()));
		}
		return tracks;
		
	}
	public static boolean isHausDorffDistanceShorter(
			Collection<? extends WayPoint> trackSeqs1, Collection<? extends WayPoint> trackSeqs2,
			double trackDistance) {
		LinkedList<WayPoint> tempList1= new LinkedList<WayPoint>(trackSeqs1);
		LinkedList<WayPoint> tempList2= new LinkedList<WayPoint>(trackSeqs2);
		//get min
		LinkedList<Double> allDiffs1 = new LinkedList<Double>();
		LinkedList<Double> allDiffs2 = new LinkedList<Double>();
		//FIXME: here dynamic programming? maybe only for bigger segments
		for (WayPoint wayPoint : tempList1) {
			double wpMax =getMaxDifference(wayPoint,tempList2);
			if(wpMax>trackDistance){
				return false;
			}
			allDiffs1.add(wpMax);
		}
		for (WayPoint wayPoint : tempList2) {
			double wpMax = getMaxDifference(wayPoint,tempList2);
			if (wpMax>trackDistance){
				return false;
			}
			allDiffs2.add(wpMax);
		}
		double max1=getMax(allDiffs1);
		double max2=getMax(allDiffs2);
		return Math.max(max1,max2)<trackDistance;
	}
	public static Bounds getBoundsWithSpace(Bounds bounds, double distance) {
		return new Bounds(addDistance(bounds.getMin(), -distance, -distance),
				addDistance(bounds.getMax(), distance, distance));
	}
	public static LatLon addDistance(LatLon p, double northernDistance, double westernDistance){
		double y = p.getY()+(180/Math.PI)*(northernDistance/6378135);
		double x = p.getX()+(180/Math.PI)*(westernDistance/6378135)/Math.cos(Math.toRadians(p.getY()));
		return new LatLon(y,x);
	}
	
	

}
