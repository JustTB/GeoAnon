package org.gpsanonymity.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.comparator.CoordinateWayPointComparator;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public abstract class Cloud {

	protected List<GpxTrack> sourceTracks;
	protected int k;
	protected List<GpxTrack> tracks;
	protected List<MergedWayPoint> mergedWayPoints=new LinkedList<MergedWayPoint>();
	protected Statistician statistician;
	
	
	protected Cloud(){}

	public Cloud(List<GpxTrack> tracks
			,int k
			,Statistician statistician) {
		this.sourceTracks=new LinkedList<GpxTrack>(tracks);
		this.k = k;
		this.statistician = statistician;
		initializeStatistician();
		initialize();
	}

	protected void initializeStatistician() {
		statistician.setSourceTrackNumber(sourceTracks.size());
		statistician.setSourceWaypointNumber(MergeGPS.getWayPointNumber(sourceTracks));
	}

	protected void initialize(){
		
	}

	protected void checkNeighborHood() {
		List<MergedWayPoint> mwps = new LinkedList<MergedWayPoint>(mergedWayPoints);
		int count=0;
		for (MergedWayPoint mwp : mwps) {
			List<MergedWayPoint> neighbors = new LinkedList(mwp.getNeighbors());
			for (MergedWayPoint neighbor : neighbors) {
				if(neighbor.getTrackGrade()<k){
					mwp.disconnect(neighbor);
					count++;
				}else if(mwp.getNeighborGrade(neighbor)<k){
					mwp.disconnect(neighbor);
					count++;
				}
			}
		}
		System.out.println("Disconnections: "+count);
		
	}

	protected void eliminateLowerGradeWayPoints() {
		List<MergedWayPoint> mwps = new LinkedList<MergedWayPoint>(mergedWayPoints);
		int count=0;
		for (MergedWayPoint mwp : mwps) {
			if(mwp.getGrade()<k || mwp.getTrackGrade()<k){
				mergedWayPoints.remove(mwp);
				mwp.disconnectAll();
				count++;
			}
		}
		
	}

	protected void deleteShortTracks() {
		LinkedList<GpxTrack> tempTracks = new LinkedList<GpxTrack>(tracks);
		for(GpxTrack track : tempTracks){
			if(track.length()<6){
				tracks.remove(track);
			}
		}
		
	}

	protected void buildTracks() {
		for (MergedWayPoint mwp : mergedWayPoints) {
			if(!mwp.isConnectionGradeCorrect()){
				System.out.println("its here");
			}
		}
		tracks=MergeGPS.buildTracks(mergedWayPoints , k);
	}

	protected double getDistance(GpxTrackSegment seg,
			GpxTrackSegment seg2) {
		Double distance =null;
		if (distance==null){
			distance=MergeGPS.hausDorffDistance(seg.getWayPoints()
							,seg2.getWayPoints());
		}
		return distance;
	}
	protected boolean isDistanceShorter(GpxTrackSegment seg,
			GpxTrackSegment seg2,double trackDistance) {
		return MergeGPS.isHausDorffDistanceShorter(seg.getWayPoints()
				,seg2.getWayPoints()
				,trackDistance);
	}

	public List<GpxTrack> getMergedTracks() {
		return tracks;
	}

}
