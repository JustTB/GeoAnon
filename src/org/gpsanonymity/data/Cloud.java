package org.gpsanonymity.data;

import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;

public abstract class Cloud {

	protected List<GpxTrack> sourceTracks;
	protected int k;
	protected List<GpxTrack> tracks;
	protected List<MergedWayPoint> mergedWaypoints=new LinkedList<MergedWayPoint>();
	protected Statistician statistician;
	
	
	protected Cloud(){}

	public Cloud(List<GpxTrack> tracks
			,int k
			,Statistician statistician) {
		this.sourceTracks=new LinkedList<GpxTrack>(tracks);
		this.k = k;
		this.statistician = statistician;
		initializeStatistician();
	}

	protected void initializeStatistician() {
		statistician.setFromSourceTracks(k,sourceTracks);
	}

	protected void initialize(){
		
	}

	protected void checkNeighborHood() {
		List<MergedWayPoint> mwps = new LinkedList<MergedWayPoint>(mergedWaypoints);
		int count=0;
		for (MergedWayPoint mwp : mwps) {
			List<MergedWayPoint> neighbors = new LinkedList<MergedWayPoint>(mwp.getNeighbors());
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
		statistician.addRemovedConnectionNumber(count);
		
	}

	protected void eliminateLowerGradeWaypoints() {
		List<MergedWayPoint> mwps = new LinkedList<MergedWayPoint>(mergedWaypoints);
		int count=0, removedConnectionCounter=0;
		for (MergedWayPoint mwp : mwps) {
			if(mwp.getTrackGrade()<k){
				mergedWaypoints.remove(mwp);
				removedConnectionCounter+=mwp.disconnectAll();
				count++;
			}
		}
		statistician.setRemovedWaypointsNumber(count);
		statistician.setRemovedConnectionNumber(removedConnectionCounter);
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
		tracks=MergeGPS.buildTracks(mergedWaypoints , k);
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
	public void initAgainWithHigherK(int k, Statistician newStatistician){
		if(k<=this.k){
			return;
		}
		newStatistician.copyFrom(statistician);
		statistician=newStatistician;
		this.k=k;
		statistician.setk(k);
		System.out.println("Status: Eliminate wayPoints with grade<"+k);
		eliminateLowerGradeWaypoints();
		System.out.println("Status: Check Neighborhood");
		checkNeighborHood();
		System.out.println("Status: Build tracks!!");
		buildTracks();
		statistician.setFromMergedTracks(tracks);
		System.out.println("Status: Done!!");
	}

}
