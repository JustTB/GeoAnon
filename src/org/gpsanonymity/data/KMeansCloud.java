package org.gpsanonymity.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class KMeansCloud extends Cloud{

	public KMeansCloud(List<GpxTrack> morePointTracks, int k,
			Statistician statistician) {
		super();
		this.sourceTracks=new LinkedList<GpxTrack>(tracks);
		this.k = k;
		this.statistician = statistician;
		initializeStatistician();
		initialize();
	}
	@Override
	protected void initialize() {
		System.out.println("Status: Build MergedWayPoints.");
		buildMergedWayPoint();
		System.out.println("Status: Find Cluster");
		findCluster();
		System.out.println("Status: Eliminate wayPoints with grade<"+k);
		statistician.setFromMergedWayPoints(mergedWayPoints);
		eliminateLowerGradeWayPoints();
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
		System.out.println("Status: Find Cluster");
		findCluster();
		System.out.println("Status: Eliminate wayPoints with grade<"+k);
		statistician.setFromMergedWayPoints(mergedWayPoints);
		eliminateLowerGradeWayPoints();
		System.out.println("Status: Check Neighborhood");
		checkNeighborHood();
		System.out.println("Status: Build tracks!!");
		buildTracks();
		statistician.setFromMergedTracks(tracks);
		System.out.println("Status: Done!!");
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void findCluster() {
		mergedWayPoints = MergeGPS.mergeWithKMeans((List)mergedWayPoints, mergedWayPoints.size()/k);
		
	}
	private void buildMergedWayPoint() {
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
					mergedWayPoints.add(mwp);
				}
			}
		}
	}

}
