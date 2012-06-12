package org.gpsanonymity.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class CliqueCloakExtendedCloud extends CliqueCloakCloud {

	private int intolerance;
	private double minimalDistance;
	public CliqueCloakExtendedCloud(List<GpxTrack> tracks, int k,
			Statistician statistician) {
		super(tracks, k, statistician);
	}
	protected void initialize() {
		intolerance=2;
		minimalDistance=4;
		findWholeBounds();
		System.out.println("Status: Build MergedWayPoints.");
		buildMergedWayPoint();
		System.out.println("Status: Find Bounds");
		allBounds=new HashMap<Bounds, List<MergedWayPoint>>();
		makeBounds(wholeBounds,mergedWayPoints);
		System.out.println("Status: Merge WayPoints");
		mergeWayPoints();
		statistician.setFromMergedWayPoints(mergedWayPoints);
		System.out.println("Status: Check Neighborhood");
		checkNeighborHood();
		System.out.println("Status: Build tracks!!");
		buildTracks();
		statistician.setFromMergedTracks(tracks);
		System.out.println("Status: Done!!");
	}
	protected boolean makeBounds(Bounds bounds,List<MergedWayPoint> mwps) {
		if(bounds.getMin().greatCircleDistance(bounds.getMax())<minimalDistance){
			allBounds.put(bounds,mwps);
			return true;
		}
		LinkedList<Bounds> halfBounds = getKMeansBounds(bounds,mwps);
		assert(halfBounds.size()==2);
		LinkedList<MergedWayPoint> mwp0s = new LinkedList<MergedWayPoint>();
		LinkedList<MergedWayPoint> mwp1s = new LinkedList<MergedWayPoint>();
		for (MergedWayPoint mwp : mwps) {
			if(halfBounds.get(0).contains(mwp.getCoor())){
				mwp0s.add(mwp);
			}else if (halfBounds.get(1).contains(mwp.getCoor())){
				mwp1s.add(mwp);
			}else{
				System.out.println("WHAT!!!");
				throw new Error();
			}
		}
		boolean higher0=trackGradeHigherOrEqualThen(mwp0s,k);
		boolean higher1=trackGradeHigherOrEqualThen(mwp1s,k);
		if(higher0 && higher1){
			return makeBounds(halfBounds.get(0),mwp0s) && makeBounds(halfBounds.get(1),mwp1s);
		}else if(higher0 && !trackGradeHigherOrEqualThen(mwp1s,intolerance+1)){
			return makeBounds(halfBounds.get(0),mwp0s);
		}else if(higher1 && !trackGradeHigherOrEqualThen(mwp0s,intolerance+1)){
			return makeBounds(halfBounds.get(1),mwp1s);
		}else{
			if(bounds.getMin().greatCircleDistance(bounds.getMax())>1000){
				System.out.println("How much is the Fish!");
			}
			allBounds.put(bounds,mwps);
			return true;
		}
	}
	private LinkedList<Bounds> getKMeansBounds(Bounds bounds,
			List<MergedWayPoint> mwps) {
		LatLon downRightCorner = new LatLon(bounds.getMin().getY(), bounds.getMax().getX());
		LinkedList<Bounds> result = new LinkedList<Bounds>();
		/*HashMap<GpxTrack, List<MergedWayPoint>> trackCluster = new HashMap<GpxTrack, List<MergedWayPoint>>(); 
		for (MergedWayPoint mwp : mwps) {
			//Assumption: only one Track in each mwp
			for(GpxTrack track :mwp.getTracks()){
				if(trackCluster.containsKey(track)){
					trackCluster.get(track).add(mwp);
				}else{
					List<MergedWayPoint> newList = new LinkedList<MergedWayPoint>();
					newList.add(mwp);
					trackCluster.put(track, newList);
				}
			}
		}
		LinkedList<WayPoint> centroids = new LinkedList<WayPoint>();
		for (List<MergedWayPoint> mwps1 : trackCluster.values()) {
			centroids.add(new WayPoint(MergeGPS.calculateCentroid((List)mwps1)));
		}
		LatLon centroid=MergeGPS.calculateCentroid(centroids);
		*/
		LatLon centroid=MergeGPS.calculateCentroid((MergeGPS.findKMeansCluster((List)mwps, k)));
		//LatLon centroid=MergeGPS.calculateCentroid((List)mwps);
		LatLon seperator;
		if(downRightCorner.greatCircleDistance(bounds.getMin())//width
				>downRightCorner.greatCircleDistance(bounds.getMax())){//height
			//left right tiling
			seperator=new LatLon(
					bounds.getMin().getY(),
					centroid.getX());
			result.add(new Bounds(
					bounds.getMin(),
					new LatLon(bounds.getMax().getY()
						,seperator.getX())));
		}else{//top down tiling
			seperator=new LatLon(
					centroid.getY()
					,bounds.getMin().getX());
			result.add(new Bounds(
					bounds.getMin(),
					new LatLon(seperator.getY()
						,bounds.getMax().getX())
					));
		}
		result.add(new Bounds(seperator,bounds.getMax()));
		return result;
	}
}