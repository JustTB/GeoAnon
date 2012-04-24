package org.gpsanonymity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.HalfMatrix;
import org.gpsanonymity.data.MergedWayPoint;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class TrackCloud {

	private List<GpxTrack> sourceTracks;
	private int k;
	private double pointRadius;
	private double trackDistance;
	private int segmentLength;
	private HashMap<GpxTrackSegment,List<GpxTrackSegment>> similarSegments;
	private List<GpxTrackSegment> segments;
	private HalfMatrix<GpxTrackSegment, Double> segmentDistanceMatrix;
	private Object tracks;
	private List<MergedWayPoint> mergedWayPoints;

	public TrackCloud(List<GpxTrack> morePointTracks, int k,
			double pointRadius, double trackDistance, int segmentLength) {
		this.sourceTracks=new LinkedList<GpxTrack>(morePointTracks);
		this.k = k;
		this.pointRadius=pointRadius;
		this.trackDistance=trackDistance;
		this.segmentLength=segmentLength;
		segments = new LinkedList<GpxTrackSegment>();
		similarSegments= new HashMap<GpxTrackSegment, List<GpxTrackSegment>>();
		segmentDistanceMatrix = new HalfMatrix<GpxTrackSegment, Double>();
		initialize();
	}

	private void initialize() {
		buildSegments();
		findSimilarSegments();
		eliminateLowerGrades();
		mergeSimilarSegments();
		buildTracks();
	}

	private void mergeSimilarSegments() {
		List<WayPoint> tempWPs=new LinkedList<WayPoint>();
		for (List<GpxTrackSegment> list : similarSegments.values()) {
			for (GpxTrackSegment gpxTrackSegment : list) {
				tempWPs.addAll(gpxTrackSegment.getWayPoints());
			}
			List<MergedWayPoint> newWps = MergeGPS.mergeWithKMeans(tempWPs, segmentLength);
			newWps = MergeGPS.eliminateLowerGradesMerged(newWps, k);
			mergedWayPoints.addAll(newWps);
		}
	}

	private void buildTracks() {
		tracks=MergeGPS.buildTracks(mergedWayPoints , k);
	}

	private void eliminateLowerGrades() {
		for (GpxTrackSegment segment : similarSegments.keySet()) {
			if(similarSegments.get(segment).size()<k){
				similarSegments.remove(segment);
			}
		}
		
	}

	private void findSimilarSegments() {
		
		for (Iterator<GpxTrackSegment> gpxIterator = segments.iterator(); gpxIterator.hasNext();) {
			GpxTrackSegment seg = (GpxTrackSegment) gpxIterator.next();
			for (Iterator<GpxTrackSegment> gpxIterator2 = segments.iterator(); gpxIterator2.hasNext();) {
				GpxTrackSegment seg2 = (GpxTrackSegment) gpxIterator2.next();
				if (seg!=seg2){
					Double distance = getDistance(seg, seg2);
					//boolean similarAngles=haveSimilarVectors(seg,seg2);
					System.out.println("Distance:"+distance+" m");
					if(distance<trackDistance){
						addSimilarSegments(seg,seg2);
					}
				}
			}
		}
		
	}
	@Deprecated
	private boolean haveSimilarVectors(GpxTrackSegment seg, GpxTrackSegment seg2) {
		if (seg!=null && seg2!=null){
		List<WayPoint> wps1 = new LinkedList<WayPoint>(seg.getWayPoints());
		List<WayPoint> wps2 = new LinkedList<WayPoint>(seg2.getWayPoints());
		//find same end
		if(wps1
				.get(0)
				.getCoor()
				.greatCircleDistance(
						wps2.get(0).getCoor()
						)
				<wps1
				.get(0)
				.getCoor()
				.greatCircleDistance(
						wps2.get(wps2.size()-1).getCoor()
						)
				){
			//wps1 and wps2 have near beginnings at index 0
			for (int i = 0; i < wps1.size() && i<wps2.size(); i++) {
				if (i>0) {
					double latVector,lonVector,latVector2,lonVector2;
					latVector=wps1.get(i-1).getCoor().getY()-wps1.get(i).getCoor().getY();
					lonVector=wps1.get(i-1).getCoor().getX()-wps1.get(i).getCoor().getX();
					latVector2=wps2.get(i-1).getCoor().getY()-wps2.get(i).getCoor().getY();
					lonVector2=wps2.get(i-1).getCoor().getX()-wps2.get(i).getCoor().getX();
					//TODO
				}
			}
			}
		return true;//TODO
		}else{
			return false;
		}
	}

	private void addSimilarSegments(GpxTrackSegment seg, GpxTrackSegment seg2) {
		List<GpxTrackSegment> segEntry = similarSegments.get(seg);
		List<GpxTrackSegment> seg2Entry = similarSegments.get(seg2);
		if(segEntry==null && seg2Entry==null){
			List<GpxTrackSegment> list = new LinkedList<GpxTrackSegment>();
			list.add(seg);
			list.add(seg2);
			similarSegments.put(seg, list);
			similarSegments.put(seg2, list);
		}else if(segEntry==null && seg2Entry!=null){
			seg2Entry.add(seg);
			similarSegments.put(seg, getSimilarSegments(seg,seg2Entry));
		}else if(segEntry!=null && seg2Entry==null){
			segEntry.add(seg2);
			similarSegments.put(seg2, getSimilarSegments(seg2,segEntry));
		}else{
			similarSegments.put(seg, getSimilarSegments(seg,seg2Entry));
			similarSegments.put(seg2, getSimilarSegments(seg2,segEntry));
		}
		
		
	}

	private List<GpxTrackSegment> getSimilarSegments(GpxTrackSegment seg,
			List<GpxTrackSegment> seglist) {
		List<GpxTrackSegment> result = new LinkedList<GpxTrackSegment>();
		for (GpxTrackSegment gpxTrackSegment : seglist) {
			if (getDistance(seg,gpxTrackSegment)<trackDistance){
				result.add(gpxTrackSegment);
			}
		}
		if (result.size()>0){
			return result;
		}else{
			return null;
		}
	}

	private double getDistance(GpxTrackSegment seg,
			GpxTrackSegment seg2) {
		Double distance =segmentDistanceMatrix.getValue(seg, seg2);
		if (distance==null){
			distance=MergeGPS.hausDorffDistance(seg.getWayPoints()
							,seg2.getWayPoints());
			segmentDistanceMatrix.put(seg, seg2,distance		);
		}
		return distance;
	}

	private void buildSegments() {
		//initalize tempWps
		List<List<WayPoint>> tempWps= new LinkedList<List<WayPoint>>();
		for(int n=0;n<segmentLength;n++){
			tempWps.add(new LinkedList<WayPoint>());
		}
		//build all linear tracksegments:
		//example: segmentlength=3
		//-> [1,2,3][2,3,4][3,4,5] not only [1,2,3][4,5,6]
		WayPoint wp;
		MergedWayPoint mwp;
		for (Iterator<GpxTrack> trackiter = sourceTracks.iterator(); trackiter.hasNext();) {
			GpxTrack track = (GpxTrack) trackiter.next();
			/*
			for (List<WayPoint> list : tempWps) {
				if (list.size()==segmentLength){
					segments.add(new ImmutableGpxTrackSegment(list));
				}
				list=new LinkedList<WayPoint>();
			}
			
			*/
			wp=null;
			mwp=null;
			for (Iterator<GpxTrackSegment> segiter = track.getSegments().iterator(); segiter.hasNext();) {
				GpxTrackSegment seg = (GpxTrackSegment) segiter.next();
				for (Iterator<WayPoint> wpiter = seg.getWayPoints().iterator(); wpiter
						.hasNext();) {
					MergedWayPoint beforWp = mwp;
					if(beforWp!=null){
						beforWp.connect(mwp);
					}
					wp = (WayPoint) wpiter.next();
					mwp = new MergedWayPoint(wp);
					mwp.addSegment(seg, wp);
					mwp.addTrack(track, wp);
					for (int index=0;index<tempWps.size();index++) {
						List<WayPoint> list = tempWps.get(index);
						//in first run dont feed all lists
						//example: segmentlength=3
						//index 0:-> [1,,][,,][,,] not [1,,][1,,]
						//index 1:-> [1,2,][2,,][,,]
						//index 2:-> [1,2,3][2,3,][3,,]
						if(index>0 
								&& list.isEmpty()
								&& tempWps.get(index-1).size()>index){
							//do not add
							
						}else{
							list.add(mwp);
						}
						if(list.size()>=segmentLength){
							segments.add(new ImmutableGpxTrackSegment(list));
							int i=tempWps.indexOf(list);
							list=new LinkedList<WayPoint>();
							tempWps.set(i, new LinkedList<WayPoint>());
							list.add(mwp);
						}
					}
					
					
				}
			}
		}
	}

	public List<GpxTrack> getMergedTracks() {
		// TODO Auto-generated method stub
		return null;
	}

}
