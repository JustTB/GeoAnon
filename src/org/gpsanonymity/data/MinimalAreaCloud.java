package org.gpsanonymity.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class MinimalAreaCloud extends Cloud {
	HashMap<Bounds,List<MergedWayPoint>> allBounds;
	Bounds wholeBounds;
	protected MinimalAreaCloud(){
		super();
	}
	public MinimalAreaCloud(List<GpxTrack> tracks, int k,
			Statistician statistician) {
		super(tracks, k, statistician);
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
	protected void mergeWayPoints() {
		mergedWaypoints=new LinkedList<MergedWayPoint>();
		for (List<MergedWayPoint> mwpList : allBounds.values()) {
			MergedWayPoint tempMwp=mwpList.get(0);
			for (MergedWayPoint mwp : mwpList) {
				tempMwp.mergeWith(mwp);
			}
			mergedWaypoints.add(tempMwp);
		}
	}
	protected boolean makeBounds(Bounds bounds,List<MergedWayPoint> mwps) {
		LinkedList<Bounds> halfBounds = getHalfBounds(bounds);
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
		if(trackGradeHigherOrEqualThen(mwp0s,k) && trackGradeHigherOrEqualThen(mwp1s,k)){
			return makeBounds(halfBounds.get(0),mwp0s) && makeBounds(halfBounds.get(1),mwp1s);
		}else if(trackGradeHigherOrEqualThen(mwp0s,k) && mwp1s.size()==0){
			return makeBounds(halfBounds.get(0),mwp0s);
		}else if(trackGradeHigherOrEqualThen(mwp1s,k) && mwp0s.size()==0){
			return makeBounds(halfBounds.get(1),mwp1s);
		}else{
			allBounds.put(bounds,mwps);
			return true;
		}
	}
	protected boolean trackGradeHigherOrEqualThen(LinkedList<MergedWayPoint> mwp0s, int k) {
		HashSet<GpxTrack> differentTracks=new HashSet<GpxTrack>(); 
		for (MergedWayPoint mwp : mwp0s) {
			differentTracks.addAll(mwp.getTracks());
			if(differentTracks.size()>=k){
				return true;
			}
		}
		return false;
	}
	protected LinkedList<Bounds> getHalfBounds(Bounds bounds) {
		LatLon downRightCorner = new LatLon(bounds.getMin().getY(), bounds.getMax().getX());
		LinkedList<Bounds> result = new LinkedList<Bounds>();
		LatLon seperator;
		if(downRightCorner.greatCircleDistance(bounds.getMin())//width
				>downRightCorner.greatCircleDistance(bounds.getMax())){//height
			//left right tiling
			seperator=new LatLon(
					bounds.getMin().getY(),
					(bounds.getMin().getX()+bounds.getMax().getX())/2);
			result.add(new Bounds(
					bounds.getMin(),
					new LatLon(bounds.getMax().getY()
						,seperator.getX())));
		}else{//top down tiling
			seperator=new LatLon(
					(bounds.getMin().getY()+bounds.getMax().getY())/2
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
	protected void findWholeBounds() {
		Bounds resultBounds=null;
		for (GpxTrack track : sourceTracks) {
			if(track.length()!=0){
				if(resultBounds==null){
					resultBounds=new Bounds(track.getBounds());
				}else{
					resultBounds.extend(track.getBounds());
				}
			}
		}
		wholeBounds=resultBounds;
	}
	protected void buildMergedWayPoint() {
		WayPoint wp;
		MergedWayPoint mwp;
		for (Iterator<GpxTrack> trackiter = sourceTracks.iterator(); trackiter.hasNext();) {
			GpxTrack track = (GpxTrack) trackiter.next();
			wp=null;
			mwp=null;
			for (Iterator<GpxTrackSegment> segiter = track.getSegments().iterator(); segiter.hasNext();) {
				GpxTrackSegment seg = (GpxTrackSegment) segiter.next();
				for (Iterator<WayPoint> wpiter = seg.getWayPoints().iterator(); wpiter
						.hasNext();) {
					//
					MergedWayPoint antecessor = mwp;
					wp = (WayPoint) wpiter.next();
					mwp = new MergedWayPoint(wp);
					mwp.addSegment(seg, wp);
					mwp.addTrack(track, wp);
					if(antecessor!=null){
						mwp.connect(antecessor);
					}
					mergedWaypoints.add(mwp);
				}
			}
		}
	}
	public void plotData(String path){
		
	}
}
