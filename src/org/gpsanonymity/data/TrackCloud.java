package org.gpsanonymity.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.comparator.CoordinateWayPointComparator;
import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class TrackCloud {

	private List<GpxTrack> sourceTracks;
	private int k;
	private double trackDistance;
	private int segmentLength;
	private HashMap<GpxTrackSegment,List<GpxTrackSegment>> similarSegments;
	private List<GpxTrackSegment> segments;
	private List<GpxTrack> tracks;
	private List<MergedWayPoint> mergedWayPoints;
	private HashMap<GpxTrackSegment,HashSet<GpxTrack>> tracksOfSimilarSegments;

	public TrackCloud(List<GpxTrack> morePointTracks, int k, double trackDistance, int segmentLength) {
		this.sourceTracks=new LinkedList<GpxTrack>(morePointTracks);
		this.k = k;
		this.trackDistance=trackDistance;
		this.segmentLength=segmentLength;
		segments = new LinkedList<GpxTrackSegment>();
		similarSegments= new HashMap<GpxTrackSegment, List<GpxTrackSegment>>();
		mergedWayPoints = new LinkedList<MergedWayPoint>();
		tracksOfSimilarSegments = new HashMap<GpxTrackSegment, HashSet<GpxTrack>>();
		initialize();
	}

	private void initialize() {
		System.out.println("Status:Build segments.");
		buildSegments();
		System.out.println("Status:Find Similar Segments");
		findSimilarSegments();
		System.out.println("Status:Eliminate similar segments with grade<"+k);
		eliminateLowerGrades();
		System.out.println("Status:Merge similar Segments");
		mergeSimilarSegments();
		System.out.println("Status:Delete big distances");
		//deleteBigDistances();
		System.out.println("Status:Build tracks!!");
		mergeNearWayPoints();
		buildTracks();
		System.out.println("Status:Delete short tracks");
		//deleteShortTracks();
	}

	private void mergeNearWayPoints() {
		for (MergedWayPoint mwp : mergedWayPoints) {
			Bounds mwpsBounds = MergeGPS.getBoundsWithSpace(new Bounds(mwp.getCoor()), trackDistance);
			for (MergedWayPoint mwp2 : mergedWayPoints) {
				if(mwp!=mwp2
						&& mwpsBounds.contains(mwp2.getCoor())){
					mwp.mergeWith(mwp2);
				}
			}
		}
		
	}

	private void deleteBigDistances() {
		for(MergedWayPoint mwp : mergedWayPoints){
			mwp.deleteDistantNeighbors(trackDistance);
		}
		
	}

	private void deleteShortTracks() {
		LinkedList<GpxTrack> tempTracks = new LinkedList<GpxTrack>(tracks);
		for(GpxTrack track : tempTracks){
			if(track.length()<6){
				tracks.remove(track);
			}
		}
		
	}

	private void mergeSimilarSegments() {
		List<WayPoint> maxWPs,minWPs;
		for (List<GpxTrackSegment> list : similarSegments.values()) {
			maxWPs =new LinkedList<WayPoint>();
			minWPs =new LinkedList<WayPoint>();
			//this only works with segmentsLength=2
			CoordinateWayPointComparator cwpc= new CoordinateWayPointComparator();
			for (GpxTrackSegment gpxTrackSegment : list) {
				List<WayPoint> wps = new LinkedList<WayPoint>(gpxTrackSegment.getWayPoints());
				Collections.sort(wps,cwpc);
				maxWPs.add(wps.get(0));
				minWPs.add(wps.get(1));
			}
			List<MergedWayPoint> newWps = MergeGPS.mergeWithKMeans(maxWPs, 1);
			newWps.addAll(MergeGPS.mergeWithKMeans(minWPs, 1));
			//TODO:
			//newWps = MergeGPS.eliminateLowerGradesMerged(newWps, k);
			MergedWayPoint ancessor=null;
			for (Iterator iterator = newWps.iterator(); iterator.hasNext();) {
				MergedWayPoint mwp = (MergedWayPoint) iterator
						.next();
				if (ancessor!=null){
					mwp.connect(ancessor,list.size());
				}
				ancessor=mwp;
				
			}
			System.out.println("NeighborGrade: "+newWps.get(0).getNeighborGrade(newWps.get(newWps.size()-1)));
			mergedWayPoints.addAll(newWps);
		}
	}

	private void buildTracks() {
		tracks=MergeGPS.buildTracks(mergedWayPoints , k);
	}

	private void eliminateLowerGrades() {
		List<GpxTrackSegment> keySet = new LinkedList<GpxTrackSegment>(similarSegments.keySet());
		int size=keySet.size();
		int count=0,grade=0,averageGrade=0,highestGrade=0;
		for (GpxTrackSegment segment : keySet) {
			try{
				grade=similarSegments.get(segment).size();
				averageGrade+=grade;
				if(highestGrade<grade){
					highestGrade=grade;
				}
				if(grade<k){
					similarSegments.remove(segment);
					count++;
				}
			}catch (Exception e) {
				System.out.println();
			}
		}
		System.out.println("Segments removed: " + count+"\\" +size);
		System.out.println("Average Grade: " + averageGrade/size);
		System.out.println("Highest Grade: " + averageGrade/size);
		
	}

	private void findSimilarSegments() {
		for (Iterator<GpxTrackSegment> gpxIterator = segments.iterator(); gpxIterator.hasNext();) {
			GpxTrackSegment seg = (GpxTrackSegment) gpxIterator.next();
			Bounds areaAroundSeg = MergeGPS.getBoundsWithSpace(seg.getBounds(),trackDistance);
			for (Iterator<GpxTrackSegment> gpxIterator2 = segments.iterator(); gpxIterator2.hasNext();) {
				GpxTrackSegment seg2 = (GpxTrackSegment) gpxIterator2.next();
				if (seg!=seg2 
						&& areaAroundSeg.intersects(seg2.getBounds())
						&& MergeGPS.haveNotTheSamePoints(seg,seg2)
						&& MergeGPS.differenceInAngleIsLowerThan(seg,seg2,0.3*Math.PI)
						&& haveNoTrackInCommon(seg,seg2)){
					Double distance = getDistance(seg, seg2);
					//boolean similarAngles=haveSimilarVectors(seg,seg2);
					if(distance<trackDistance){
						addSimilarSegments(seg,seg2);
					}
				}
			}
		}
		
	}
	private boolean haveNoTrackInCommon(GpxTrackSegment seg, GpxTrackSegment seg2) {
		for (GpxTrack track :tracksOfSimilarSegments.get(seg)){
			for (GpxTrack track2 :tracksOfSimilarSegments.get(seg2)){
				if(track==track2){
					return false;
				}
			}
		}
		return true;
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
			if (!seg2Entry.contains(seg)){
				seg2Entry.add(seg);
				similarSegments.put(seg, getSimilarSegments(seg,seg2Entry));
			}
		}else if(segEntry!=null && seg2Entry==null){
			if (!segEntry.contains(seg2)){
				segEntry.add(seg2);
				similarSegments.put(seg2, getSimilarSegments(seg2,segEntry));
			}
		}else{
			if (!segEntry.contains(seg2)){
				segEntry.add(seg2);
				similarSegments.put(seg, getSimilarSegments(seg,seg2Entry));
			}
			if (!seg2Entry.contains(seg)){
				seg2Entry.add(seg);
				similarSegments.put(seg2, getSimilarSegments(seg2,segEntry));
			}
		}
		HashSet<GpxTrack> trackSeg = tracksOfSimilarSegments.get(seg);
		HashSet<GpxTrack> trackSeg2 = tracksOfSimilarSegments.get(seg2);
		trackSeg.addAll(trackSeg2);
		trackSeg2.addAll(trackSeg);
	}

	private List<GpxTrackSegment> getSimilarSegments(GpxTrackSegment seg,
			List<GpxTrackSegment> seglist) {
		List<GpxTrackSegment> result = new LinkedList<GpxTrackSegment>();
		for (GpxTrackSegment gpxTrackSegment : seglist) {
			if (isDistanceShorter(seg,gpxTrackSegment,trackDistance)){
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
		Double distance =null;//segmentDistanceMatrix.getValue(seg, seg2);
		if (distance==null){
			distance=MergeGPS.hausDorffDistance(seg.getWayPoints()
							,seg2.getWayPoints());
			//segmentDistanceMatrix.put(seg, seg2,distance		);
		}
		return distance;
	}
	private boolean isDistanceShorter(GpxTrackSegment seg,
			GpxTrackSegment seg2,double trackDistance) {
		return MergeGPS.isHausDorffDistanceShorter(seg.getWayPoints()
				,seg2.getWayPoints()
				,trackDistance);
	}

	private void buildSegments() {
		//initalize overlappingSegments
		List<List<WayPoint>> overlappingSegments= new LinkedList<List<WayPoint>>();
		for(int n=0;n<segmentLength;n++){
			overlappingSegments.add(new LinkedList<WayPoint>());
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
					//
					MergedWayPoint antecessor = mwp;
					wp = (WayPoint) wpiter.next();
					mwp = new MergedWayPoint(wp);
					mwp.addSegment(seg, wp);
					mwp.addTrack(track, wp);
					if(antecessor!=null && !antecessor.containsNeighbor(mwp)){
						antecessor.connect(mwp);
					}
					for (int index=0;index<1;index++) {
					//for (int index=0;index<overlappingSegments.size();index++) {
						List<WayPoint> currentSegment = overlappingSegments.get(index);
						//in first run dont feed all lists
						//example: segmentlength=3
						//wp 0:-> [1,,][,,][,,] not [1,,][1,,]
						//wp 1:-> [1,2,][2,,][,,]
						//wp 2:-> [1,2,3][2,3,][3,,]
						if(index>0 
								&& currentSegment.isEmpty()
								&& (overlappingSegments.get(index-1).size())%segmentLength!=2%segmentLength){
							//do not add
						}else{
							currentSegment.add(mwp);
						}
						if(currentSegment.size()>=segmentLength){
							GpxTrackSegment newSeg =new ImmutableGpxTrackSegment(currentSegment); 
							segments.add(newSeg);
							HashSet<GpxTrack> trackList = new HashSet<GpxTrack>();
							trackList.add(track);
							tracksOfSimilarSegments.put(newSeg,trackList);
							int i=overlappingSegments.indexOf(currentSegment);
							overlappingSegments.set(i, new LinkedList<WayPoint>());
						}
					}
					
					
				}
			}
		}
	}

	public List<GpxTrack> getMergedTracks() {
		return tracks;
	}

}
