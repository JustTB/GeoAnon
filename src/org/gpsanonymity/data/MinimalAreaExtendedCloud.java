package org.gpsanonymity.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;

public class MinimalAreaExtendedCloud extends MinimalAreaCloud {

	private int intolerance;
	private double minimalDiagonalLength;
	public MinimalAreaExtendedCloud(List<GpxTrack> tracks, int k, int intolerance, double minimalAreaDistance
			,Statistician statistician) {
		super();
		this.sourceTracks=new LinkedList<GpxTrack>(tracks);
		this.k = k;
		this.intolerance = intolerance;
		this.statistician = statistician;
		this.minimalDiagonalLength=minimalAreaDistance;
		initializeStatistician();
		initialize();
	}
	protected void initialize() {
		
		findWholeBounds();
		System.out.println("Status: Build MergedWayPoints.");
		buildMergedWayPoint();
		System.out.println("Status: Find Bounds");
		allBounds=new HashMap<Bounds, List<MergedWayPoint>>();
		makeBounds(wholeBounds,mergedWaypoints);
		System.out.println("Status: Merge WayPoints");
		mergeWayPoints();
		statistician.setFromMergedWayPoints(mergedWaypoints);
		System.out.println("Status: Check Neighborhood");
		checkNeighborHood();
		System.out.println("Status: Build tracks!!");
		buildTracks();
		statistician.setFromMergedTracks(tracks);
		System.out.println("Status: Done!!");
	}
	public void initAgainWithHigherK(int k, Statistician newStatistician){
		if(k<=this.k){
			return;
		}
		newStatistician.copyFrom(statistician);
		statistician=newStatistician;
		this.k=k;
		statistician.setk(k);
		System.out.println("Status: Build MergedWayPoints.");
		buildMergedWayPoint();
		System.out.println("Status: Find Bounds");
		allBounds=new HashMap<Bounds, List<MergedWayPoint>>();
		makeBounds(wholeBounds,mergedWaypoints);
		System.out.println("Status: Merge WayPoints");
		mergeWayPoints();
		statistician.setFromMergedWayPoints(mergedWaypoints);
		System.out.println("Status: Check Neighborhood");
		checkNeighborHood();
		System.out.println("Status: Build tracks!!");
		buildTracks();
		statistician.setFromMergedTracks(tracks);
		System.out.println("Status: Done!!");
	}
	protected boolean makeBounds(Bounds bounds,List<MergedWayPoint> mwps) {
		if(bounds.getMin().greatCircleDistance(bounds.getMax())<minimalDiagonalLength){
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
		@SuppressWarnings({ "unchecked", "rawtypes" })
		LatLon centroid=MergeGPS.calculateCentroid((MergeGPS.findKMeansCluster((List)mwps, k)));
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