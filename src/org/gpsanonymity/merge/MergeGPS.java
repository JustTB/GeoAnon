package org.gpsanonymity.merge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.DebugGraphics;

import org.gpsanonymity.data.MergedWayPoint;
import org.gpsanonymity.data.comparator.ReferenceWayPointComparator;
import org.gpsanonymity.io.IOFunctions;
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
		double procentBlocks = tempWaypoints.size()/100;
		double procents=100;
		while(!tempWaypoints.isEmpty()){
			if(Math.round(tempWaypoints.size()/procentBlocks)==procents){
				System.out.println("WayPoints Merged: "+ procents + "%");
				procents--;
			}
			MergedWayPoint mwp = new MergedWayPoint(tempWaypoints.getFirst());
			ReferenceWayPointComparator wpc = new ReferenceWayPointComparator();
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
			//mwp.calculateNewCoordinates();
			//mwp.calculateNewDate();
			tempWaypoints.removeFirst();
			mergedWaypoints.add(mwp);
		}

		return mergedWaypoints;

	}

	public static LatLon calculateCentroid(Collection<WayPoint> cluster) {
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

	public static String simpleGeneralizeDate(Collection<WayPoint> sourceWaypoints) {
		//FIXME:something goes wrong
		//sort by Date
//		if (!sourceWaypoints.isEmpty()){
//			Collections.sort(sourceWaypoints);
//			Date first = sourceWaypoints.getFirst().getTime();
//			Date last = sourceWaypoints.getLast().getTime();
//			SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
//			SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM");
//			SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd");
//			SimpleDateFormat dateFormatHour = new SimpleDateFormat("HH");
//			SimpleDateFormat dateFormatMinute = new SimpleDateFormat("mm");
//			SimpleDateFormat dateFormatSecond = new SimpleDateFormat("ss");
//			SimpleDateFormat dateFormatMilli = new SimpleDateFormat("SSS");
//			String result = new String();
//			//Year
//			if (	dateFormatYear.format(first)
//					.compareTo(
//							dateFormatYear.format(last))
//							==0
//					){
//				result+=dateFormatYear.format(last)+":";
//			}else{
//				result+="XXXX:";
//			}
//			//Month
//			if (	dateFormatMonth.format(first)
//					.compareTo(
//							dateFormatMonth.format(last))
//							==0
//					){
//				result+=dateFormatMonth.format(last)+":";
//			}else{
//				result+="XX:";
//			}
//			//Day
//			if (	dateFormatDay.format(first)
//					.compareTo(
//							dateFormatDay.format(last))
//							==0
//					){
//				result+=dateFormatDay.format(last)+":";
//			}else{
//				result+="XX:";
//			}
//			//Hour
//			if (	dateFormatHour.format(first)
//					.compareTo(
//							dateFormatHour.format(last))
//							==0
//					){
//				result+=dateFormatHour.format(last)+":";
//			}else{
//				result+="XX:";
//			}				
//			//Minute
//			if (	dateFormatMinute.format(first)
//					.compareTo(
//							dateFormatMinute.format(last))
//							==0
//					){
//				result+=dateFormatMinute.format(last)+":";
//			}else{
//				result+="XX:";
//			}
//			//Second
//			if (	dateFormatSecond.format(first)
//					.compareTo(
//							dateFormatSecond.format(last))
//							==0
//					){
//				result+=dateFormatSecond.format(last)+":";
//			}else{
//				result+="XX:";
//			}
//			//Milli
//			if (	dateFormatMilli.format(first)
//					.compareTo(
//							dateFormatMilli.format(last))
//							==0
//					){
//				result+=dateFormatMilli.format(last)+":";
//			}else{
//				result+="XXX";
//			}
//			return result;
//		}
		return "";
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
	public static List<GpxTrackSegment> mergeSegmentsWithKMeans(List<GpxTrackSegment> list, int k, boolean ignoreDirection, double angleWeight, double distanceWeight){
		List<GpxTrackSegment> oldClusterSegs,clusterSegs=getRandomEntrys(list,k);
		List<GpxTrackSegment> cluster;
		//DistanceMatrix distanceMartix = new DistanceMatrix(list, list);
		int count=0;
		do{
			count++;
			cluster= makeSegmentCluster(clusterSegs, list,angleWeight, distanceWeight);
			oldClusterSegs=clusterSegs;
			clusterSegs=cluster;
			IOFunctions.exportTrackSegments(cluster, "output/a_cluster.gpx");
			//IOFunctions.exportTrackSegments(oldClusterSegs, "output/a_ocluster.gpx");
		}while(!areSameSegments(oldClusterSegs,clusterSegs));
		return makeMergeSegmentCluster(clusterSegs,list, ignoreDirection, angleWeight, distanceWeight);
				
	}
	private static boolean areSameSegments(
			List<GpxTrackSegment> oldClusterSegs,
			List<GpxTrackSegment> clusterSegs) {
		SegmentComparator comp = new SegmentComparator();
		//Collections.sort(oldClusterSegs,comp);
		//Collections.sort(clusterSegs,comp);
		int count=-1;
		Iterator<GpxTrackSegment> newOneIter =clusterSegs.iterator();
		if(clusterSegs.size()==oldClusterSegs.size()){
			while (newOneIter.hasNext()) {
				count++;
				GpxTrackSegment newOne = newOneIter.next();
				if(!oldClusterSegs.contains(newOne)){
					System.out.println("KMeans cluster: "+count);
					return false;
				}
			}
		}else{
			return false;
		}
		return true;
	}
	public static List<MergedWayPoint> mergeWithKMeans(List<WayPoint> list, int k){
		return makeMergeCluster(findKMeansCluster(list,k),list);
				
	}
	public static List<WayPoint> findKMeansCluster(List<WayPoint> list, int k) {
		List<WayPoint> oldClusterPoints,clusterPoints=getRandomEntrys(list,k);
		List<WayPoint> cluster;
		int count=0;
		do{
			count++;
			cluster= makeCluster(clusterPoints, list);
			oldClusterPoints=clusterPoints;
			clusterPoints=cluster;
		}while(!haveSameCoord(oldClusterPoints,clusterPoints));
		return clusterPoints;
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
			resultPoint.addWayPoint(wayPoint);
		}
		for (int i = 0; i < clusterPoints.size(); i++) {
			result.get(i).removeIfExist(clusterPoints.get(i));
		}
		return result;
	}
	private static List getRandomEntrys(List list,
			int clusterNumber) {
		if (clusterNumber<list.size()){
			List result = new LinkedList();
			Random generator= new Random(1);
			while(result.size()< clusterNumber) {
				int index =generator.nextInt(list.size());
				Object randomWayPoint = list.get(index);
				if(!result.contains(randomWayPoint)){
					result.add(list.get(index));
				}

			}
			return result;
		}else{
			return new LinkedList(list);
		}
	}
	private static List<GpxTrackSegment> makeMergeSegmentCluster(
			List<GpxTrackSegment> clusterSegs, List<GpxTrackSegment> list, boolean ignoreDirection, double angleWeight, double distanceWeight) {
		List<List<GpxTrackSegment>> clusterGroups= new LinkedList<List<GpxTrackSegment>>();
		//initialize result
		for (int i = 0; i < clusterSegs.size(); i++) {
			clusterGroups.add(new LinkedList<GpxTrackSegment>());
		}
		for (GpxTrackSegment seg : list) {
			//for each waypoint find nearest cluster point
			if(seg.length()!=0){
				int index =findNearestSegmentIndex(seg,clusterSegs,clusterGroups,angleWeight,distanceWeight);
				List<GpxTrackSegment> cluster = clusterGroups.get(index);
				cluster.add(seg);
			}
		}
		LinkedList<GpxTrackSegment> result = new LinkedList<GpxTrackSegment>();
		//for each clusterGroup find new centroid
		for (List<GpxTrackSegment> cluster : clusterGroups) {
			if(!cluster.isEmpty()){
				result.add(MergeGPS.calculateMergeSegmentCentroid(cluster, ignoreDirection));
			}
		}
		return result;
	}
	private static GpxTrackSegment calculateMergeSegmentCentroid(
			List<GpxTrackSegment> cluster, boolean ignoreDirection) {
		List<WayPoint> minWps = new LinkedList<WayPoint>();
		List<WayPoint> maxWps = new LinkedList<WayPoint>();
		for (GpxTrackSegment gpxTrackSegment : cluster) {
			List<WayPoint> wps = new LinkedList<WayPoint>(gpxTrackSegment.getWayPoints());
			if (wps.get(0).getCoor().equals(gpxTrackSegment.getBounds().getMin()) 
				|| ignoreDirection){
				minWps.add(wps.get(0));
				maxWps.add(wps.get(1));
			}else{
				minWps.add(wps.get(1));
				maxWps.add(wps.get(0));
			}
		}
		MergedWayPoint minWp= new MergedWayPoint(minWps);
		MergedWayPoint maxWp= new MergedWayPoint(maxWps);
		LinkedList<WayPoint> newWps = new LinkedList<WayPoint>();
		newWps.add(minWp);
		newWps.add(maxWp);
		return new ImmutableGpxTrackSegment(newWps);
	}
	private static List<GpxTrackSegment> makeSegmentCluster(
			List<GpxTrackSegment> clusterSegs, List<GpxTrackSegment> list, double angleWeight, double distanceWeight) {
		List<GpxTrackSegment> syncClusterSegs = Collections.synchronizedList(clusterSegs);
		List<GpxTrackSegment> synList = Collections.synchronizedList(list);
		List<List<GpxTrackSegment>> clusterGroups= Collections.synchronizedList(new LinkedList<List<GpxTrackSegment>>());
		//initialize result
		for (int i = 0; i < clusterSegs.size(); i++) {
			clusterGroups.add(Collections.synchronizedList(new LinkedList<GpxTrackSegment>()));
		}
		ExecutorService threadPool = Executors.newCachedThreadPool();
		for (GpxTrackSegment seg : synList) {
			if(seg.length()!=0){
				int index =MergeGPS.findNearestSegmentIndex(seg,clusterSegs,clusterGroups,angleWeight, distanceWeight);
				if(index!=-1){
					List<GpxTrackSegment> cluster = clusterGroups.get(index);
					cluster.add(seg);
				}
			}
			//threadPool.execute(new ClusterFinder(seg,syncClusterSegs,clusterGroups,angleWeight, distanceWeight));
		}
		threadPool.shutdown();
		while(!threadPool.isTerminated()){
			try {
				threadPool.awaitTermination(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LinkedList<GpxTrackSegment> result = new LinkedList<GpxTrackSegment>();
		//for each clusterGroup find new centroid
		for (List<GpxTrackSegment> cluster : clusterGroups) {
			if(!cluster.isEmpty()){
				//IOFunctions.exportTrackSegments(cluster, "output/cluster.gpx");
				GpxTrackSegment clusterSegment = MergeGPS.calculateSegmentCentroid(cluster);
				result.add(clusterSegment);
				LinkedList<GpxTrackSegment> clusterSegmentList = new LinkedList<GpxTrackSegment>();
				clusterSegmentList.add(clusterSegment);
				//IOFunctions.exportTrackSegments(clusterSegmentList, "output/resultingCluster.gpx");
			}
		}
		return result;
	}
	private static GpxTrackSegment calculateSegmentCentroid(
			List<GpxTrackSegment> cluster) {
		double length=0,vectorLon=0, vectorLat=0;
		List<WayPoint> all1Wps = new LinkedList<WayPoint>();
		List<WayPoint> all2Wps = new LinkedList<WayPoint>();
		LatLon reference1=null;
		LatLon reference2=null;
		for (GpxTrackSegment gpxTrackSegment : cluster) {
			List<WayPoint> wps = new LinkedList<WayPoint>(gpxTrackSegment.getWayPoints());
			if(reference1==null || reference2==null){
				reference1=wps.get(0).getCoor();
				reference2=wps.get(1).getCoor();
				all1Wps.add(wps.get(0));
				all2Wps.add(wps.get(1));
			}else if(reference1.distance(wps.get(0).getCoor())//if 0 is closer to reference1
					<reference1.distance(wps.get(1).getCoor())){
				all1Wps.add(wps.get(0));
				all2Wps.add(wps.get(1));
			}else{
				all1Wps.add(wps.get(1));
				all2Wps.add(wps.get(0));
			}
		}
		WayPoint wp1= new WayPoint(calculateCentroid(all1Wps));
		WayPoint wp2= new WayPoint(calculateCentroid(all2Wps));
		LinkedList<WayPoint> newWps = new LinkedList<WayPoint>();
		newWps.add(wp1);
		newWps.add(wp2);
		return new ImmutableGpxTrackSegment(newWps);
	}
	public static int findNearestSegmentIndex(GpxTrackSegment seg,
			List<GpxTrackSegment> list,List<List<GpxTrackSegment>> noSameTracks, double angleWeight, double distanceWeight) {
		double distance=Double.MAX_VALUE;
		int result=-1;
		for (int i = 0; i < list.size(); i++) {
			double currentDistance;
			if(!MergeGPS.haveSameTracks(seg.getWayPoints(), list.get(i).getWayPoints())){
				currentDistance = segmentDistance(list.get(i),seg,angleWeight,distanceWeight);
			}else{
				currentDistance=Double.MAX_VALUE;
			}
			if (currentDistance<distance){
				List<WayPoint> notSameTrackWps = new LinkedList<WayPoint>();
				for (GpxTrackSegment seg2 : noSameTracks.get(i)) {
					notSameTrackWps.addAll(seg2.getWayPoints());
				}

				if(!MergeGPS.haveSameTracks(seg.getWayPoints(),notSameTrackWps)){
				distance=currentDistance;
				result=i;
				}
			}
		}
		return result;
	}
	@SuppressWarnings("unused")
	private static Double segmentAnglePercentDistance(GpxTrackSegment seg1,
			GpxTrackSegment seg2) {
		double distance = hausDorffDistance(seg1.getWayPoints(), seg2.getWayPoints());
		double angle = calculateAngle(seg1, seg2);
		double angleFactor=(Math.PI/2)/Math.abs(Math.PI/2-angle);
		//angle/(pi/2) * distance
		double result=distance*angleFactor;
		return result;
		
	}
	private static Double segmentDistance(GpxTrackSegment seg1,
			GpxTrackSegment seg2, double angleWeight, double distanceWeight) {
		assert(seg1.getWayPoints().size()==2 && seg2.getWayPoints().size()==2);
		double distance = additiveMinDistance(seg1.getWayPoints(), seg2.getWayPoints());
		double lonDiff1=Double.NaN;
		for (WayPoint wp1 : seg1.getWayPoints()) {
			if(Double.isNaN(lonDiff1)){
				lonDiff1=wp1.getCoor().getY();
			}else{
				lonDiff1-=wp1.getCoor().getY();
			}
		}
		double lonDiff2=Double.NaN;
		for (WayPoint wp2 : seg2.getWayPoints()) {
			if(Double.isNaN(lonDiff2)){
				lonDiff2=wp2.getCoor().getY();
			}else{
				lonDiff2-=wp2.getCoor().getY();
			}
		}
		double lonDistance=Math.abs(Math.abs(lonDiff1)-Math.abs(lonDiff2));
		double result=distance*(distanceWeight)+lonDistance*(angleWeight);
		return result;
		
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
		ReferenceWayPointComparator wpc = new ReferenceWayPointComparator();
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
		for (WayPoint wayPoint : tempList1) {
			allDiffs1.add(getMinDifference(wayPoint,tempList2));
		}
		for (WayPoint wayPoint : tempList2) {
			allDiffs2.add(getMinDifference(wayPoint,tempList1));
		}
		double max1=getMax(allDiffs1);
		double max2=getMax(allDiffs2);
		return Math.max(max1,max2);
	}
static public Double additiveMinDistance(Collection<? extends WayPoint> trackSeqs1, Collection<? extends WayPoint> trackSeqs2) {
		
		LinkedList<WayPoint> tempList1= new LinkedList<WayPoint>(trackSeqs1);
		LinkedList<WayPoint> tempList2= new LinkedList<WayPoint>(trackSeqs2);
		//get min
		double result=0;
		for (WayPoint wayPoint : tempList1) {
			result+=getMinDifference(wayPoint,tempList2);
		}
		return result;
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
	private static Double getMinDifference(WayPoint wayPoint,
			LinkedList<WayPoint> tempList2) {
		double result=Double.MAX_VALUE;
		for (WayPoint wayPoint2 : tempList2) {
			double distance = wayPoint.getCoor().greatCircleDistance(wayPoint2.getCoor());
			if(distance<result){
				result=distance;
			}
		}
		return result;
	}
	static private void sort(WayPoint wayPoint, LinkedList<WayPoint> tempList2) {
		ReferenceWayPointComparator wpc = new ReferenceWayPointComparator();
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
			neighbor = temp.getHighestNotMarkedNeighbor(k);
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
			double wpMin =getMinDifference(wayPoint,tempList2);
			if(wpMin>trackDistance){
				return false;
			}
			allDiffs1.add(wpMin);
		}
		for (WayPoint wayPoint : tempList2) {
			double wpMin = getMinDifference(wayPoint,tempList2);
			if (wpMin>trackDistance){
				return false;
			}
			allDiffs2.add(wpMin);
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
	public static boolean haveNotTheSamePoints(GpxTrackSegment seg,
			GpxTrackSegment seg2) {
		for(WayPoint wp1 : seg.getWayPoints()){
			for(WayPoint wp2 : seg2.getWayPoints()){
				if (wp1.equals(wp2)){
					return false;
				}
			}
		}
		return true;
	}
	public static double calculateAngle(GpxTrackSegment seg,
			GpxTrackSegment seg2) {
		LinkedList<WayPoint> wps1 = new LinkedList<WayPoint>(seg.getWayPoints());
		LinkedList<WayPoint> wps2 = new LinkedList<WayPoint>(seg2.getWayPoints());
		double latVector1=wps1.get(0).getCoor().getY()
					-wps1.get(wps1.size()-1).getCoor().getY();
		double lonVector1=wps1.get(0).getCoor().getX()
					-wps1.get(wps1.size()-1).getCoor().getX();
		double latVector2=wps2.get(0).getCoor().getY()
					-wps2.get(wps2.size()-1).getCoor().getY();
		double lonVector2=wps2.get(0).getCoor().getX()
					-wps2.get(wps2.size()-1).getCoor().getX();
		double cosinusAlpha=(latVector1*latVector2+lonVector1*lonVector2)
					/(Math.sqrt(latVector1*latVector1+lonVector1*lonVector1)
							*(Math.sqrt(latVector2*latVector2+lonVector2*lonVector2)));
		if(cosinusAlpha>1){
			cosinusAlpha=1;
		}
		double alpha = Math.acos(cosinusAlpha);
		return alpha;
	}
	public static boolean differenceInAngleIsLowerThan(GpxTrackSegment seg,
			GpxTrackSegment seg2, double maxAngle) {
		return calculateAngle(seg,seg2)<maxAngle;
	}
	public static boolean haveSameTracks(Collection<WayPoint> wayPoints,
			Collection<WayPoint> wayPoints2) {
		for (WayPoint wp1 : wayPoints) {
			if(MergedWayPoint.class.isInstance(wp1)){
				for (WayPoint wp2 : wayPoints2) {
					if(MergedWayPoint.class.isInstance(wp2)){
						MergedWayPoint mwp1 = (MergedWayPoint)wp1;
						MergedWayPoint mwp2 = (MergedWayPoint)wp2;
						if (mwp1.hasSameTracks(mwp2)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	public static int getWayPointNumber(List<GpxTrack> tracks) {
		int result=0;
		for (GpxTrack gpxTrack : tracks) {
			for (GpxTrackSegment seg : gpxTrack.getSegments()) {
				result+=seg.getWayPoints().size();
			}
		}
		return result;
	}
	
	

}
