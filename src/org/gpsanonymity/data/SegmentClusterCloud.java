package org.gpsanonymity.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class SegmentClusterCloud extends SegmentCloud {

	private double angleWeight;
	private double distanceWeight;
	public SegmentClusterCloud(List<GpxTrack> morePointTracks, int k,
			double trackDistance, boolean ignoreDirection,
			double angelAllowance, Statistician statistician, double angleWeight, double distanceWeight) {
		this.sourceTracks=new LinkedList<GpxTrack>(morePointTracks);
		this.k = k;
		this.trackDistance=trackDistance;
		this.angelAllowance=angelAllowance;
		this.ignoreDirection=ignoreDirection;
		this.statistician = statistician;
		this.angleWeight=angleWeight;
		this.distanceWeight=distanceWeight;
		initializeStatistician();
		initialize();
	}
	@Override
	protected void findSimilarSegments() {
		List<GpxTrackSegment> clusterSegments = MergeGPS.mergeSegmentsWithKMeans(segments, segments.size()/k, ignoreDirection,angleWeight, distanceWeight);
		for (GpxTrackSegment gpxTrackSegment : clusterSegments) {
			HashSet<GpxTrackSegment> joinedSegments=null;
			for (WayPoint wp : gpxTrackSegment.getWayPoints()) {
				MergedWayPoint mwp = (MergedWayPoint) wp;
				if (joinedSegments==null){
					joinedSegments= new HashSet<GpxTrackSegment>();
					joinedSegments.addAll(mwp.getSegments());
				}else{
					joinedSegments.retainAll(mwp.getSegments());
				}
			}
			similarSegments.put(gpxTrackSegment, joinedSegments);
		}
		
	}
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void mergeSimilarSegments() {
		for (GpxTrackSegment seg : similarSegments.keySet()) {
			mergedWaypoints.addAll((Collection<MergedWayPoint>)(Collection)seg.getWayPoints());
		}
	}
	@Override
	protected void initialize() {
		System.out.println("Status: Build segments.");
		buildSegments();
		System.out.println("Status: Find Similar Segments");
		findSimilarSegments();
		//System.out.println("Status: Eliminate similar segments with grade<"+k);
		//eliminateLowerGradeSegments();
		System.out.println("Status: Merge similar Segments");
		mergeSimilarSegments();
		updateMergedWaypoints();
		statistician.setFromMergedWayPoints(mergedWaypoints);
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
