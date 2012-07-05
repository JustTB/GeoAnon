package org.gpsanonymity.data.methods;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.data.Cloud;
import org.gpsanonymity.data.MergedWayPoint;
import org.gpsanonymity.data.comparator.CoordinateWayPointComparator;
import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.merge.MergeGPS;
import org.gpsanonymity.stats.Statistician;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class SegmentCloud extends Cloud{

	protected double trackDistance;
	static protected int segmentLength=2;
	protected IdentityHashMap<GpxTrackSegment,HashSet<GpxTrackSegment>> similarSegments=new IdentityHashMap<GpxTrackSegment, HashSet<GpxTrackSegment>>();
	protected List<GpxTrackSegment> segments=new LinkedList<GpxTrackSegment>();
	protected IdentityHashMap<GpxTrackSegment,HashSet<GpxTrack>> tracksOfSimilarSegments= new IdentityHashMap<GpxTrackSegment, HashSet<GpxTrack>>();
	protected boolean ignoreDirection;
	protected double angelAllowance;
	
	protected SegmentCloud(){}

	public SegmentCloud(List<GpxTrack> morePointTracks
			,int k
			,double trackDistance
			,boolean ignoreDirection
			,double angelAllowance
			,Statistician statistician) {
		super(morePointTracks,k,statistician);
		this.trackDistance=trackDistance;
		this.angelAllowance=angelAllowance;
		this.ignoreDirection=ignoreDirection;
		initialize();
	}

	protected void initialize() {
		System.out.println("Status: Build segments.");
		buildSegments();
		IOFunctions.exportTrackSegments(segments, "output/BuildedSegments.gpx");
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

	protected void mergeNearWayPoints() {
		for (MergedWayPoint mwp : mergedWaypoints) {
			Bounds mwpsBounds = MergeGPS.getBoundsWithSpace(new Bounds(mwp.getCoor()), trackDistance);
			for (MergedWayPoint mwp2 : mergedWaypoints) {
				if(mwp!=mwp2
						&& mwpsBounds.contains(mwp2.getCoor())){
					mwp.mergeWith(mwp2);
				}
			}
		}
		
	}

	@SuppressWarnings("unused")
	private void deleteBigDistances() {
		for(MergedWayPoint mwp : mergedWaypoints){
			mwp.deleteDistantNeighbors(trackDistance);
		}
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void mergeSimilarSegments() {
		while(!similarSegments.isEmpty()) {
			LinkedList<HashSet<GpxTrackSegment>> hashSetList = new LinkedList<HashSet<GpxTrackSegment>>(similarSegments.values());
			LinkedList<GpxTrackSegment> list = new LinkedList<GpxTrackSegment>(hashSetList.get(0));
			//this only works with segmentsLength=2
			GpxTrackSegment seg=MergeGPS.calculateMergeSegmentCentroid(list);
			for (GpxTrackSegment gpxTrackSegment : list) {
				similarSegments.remove(gpxTrackSegment);
			}
			for(WayPoint wp : seg.getWayPoints()){
				mergedWaypoints.add((MergedWayPoint)wp);
			}
		}
	}

	protected void eliminateLowerGradeSegments() {
		List<GpxTrackSegment> keySet = new LinkedList<GpxTrackSegment>(similarSegments.keySet());
		int size=keySet.size();
		int count=0,grade=0,highestGrade=0;
		double averageGrade=0;
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
		statistician.setRemovedConnectionNumber(count);
		System.out.println("Segments removed: " + count+"\\" +size);
		System.out.println("Average Grade: " + averageGrade/size);
		System.out.println("Highest Grade: " + highestGrade);
		
	}

	protected void findSimilarSegments() {
		for (Iterator<GpxTrackSegment> gpxIterator = segments.iterator(); gpxIterator.hasNext();) {
			GpxTrackSegment seg = (GpxTrackSegment) gpxIterator.next();
			Bounds areaAroundSeg = MergeGPS.getBoundsWithSpace(seg.getBounds(),trackDistance);
			for (Iterator<GpxTrackSegment> gpxIterator2 = segments.iterator(); gpxIterator2.hasNext();) {
				GpxTrackSegment seg2 = (GpxTrackSegment) gpxIterator2.next();
				Bounds seg2Bounds = seg2.getBounds();
				if (seg!=seg2 
						&& (areaAroundSeg.intersects(seg2Bounds) 
								|| (areaAroundSeg.contains(seg2Bounds.getMax())
										&& areaAroundSeg.contains(seg2Bounds.getMin())
									)
							)
//						&& MergeGPS.haveNotTheSamePoints(seg,seg2)
						&& (MergeGPS.calculateAngle(seg, seg2)<angelAllowance *(0.5*Math.PI)//90°*angelAllowance 
								|| (MergeGPS.calculateAngle(seg, seg2)>(1-angelAllowance)*Math.PI
										&& ignoreDirection)) //90*(1-angelAllowance) if ignoreDirection==true
						&& haveNoTrackInCommon(seg,seg2)
						){
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
//		if(tracksOfSimilarSegments.get(seg).size()<2 && tracksOfSimilarSegments.get(seg2).size()<2){
			for (GpxTrack track :tracksOfSimilarSegments.get(seg)){
				for (GpxTrack track2 :tracksOfSimilarSegments.get(seg2)){
					if(track==track2){
						return false;
					}
				}
			}
//		}
		return true;
	}

	private void addSimilarSegments(GpxTrackSegment seg, GpxTrackSegment seg2) {
		HashSet<GpxTrackSegment> segEntry = similarSegments.get(seg);
		HashSet<GpxTrackSegment> seg2Entry = similarSegments.get(seg2);
		if(segEntry==null && seg2Entry==null){
			HashSet<GpxTrackSegment> set = new HashSet<GpxTrackSegment>();
			set.add(seg);
			set.add(seg2);
			similarSegments.put(seg, set);
			similarSegments.put(seg2, set);
		}else if(segEntry==null && seg2Entry!=null){
			if (!seg2Entry.contains(seg)){
				seg2Entry.add(seg);
				similarSegments.put(seg, seg2Entry);
				//similarSegments.put(seg, getSimilarSegments(seg,seg2Entry));
			}
		}else if(segEntry!=null && seg2Entry==null){
			if (!segEntry.contains(seg2)){
				segEntry.add(seg2);
				similarSegments.put(seg2, segEntry);
				//similarSegments.put(seg2, getSimilarSegments(seg2,segEntry));
			}
		}else{
			segEntry.addAll(seg2Entry);
			similarSegments.put(seg2, segEntry);
//			if (!segEntry.contains(seg2)){
//				segEntry.add(seg2);
//				similarSegments.put(seg, getSimilarSegments(seg,seg2Entry));
//			}
//			if (!seg2Entry.contains(seg)){
//				seg2Entry.add(seg);
//				similarSegments.put(seg2, getSimilarSegments(seg2,segEntry));
//			}
		}
		HashSet<GpxTrack> trackSeg = tracksOfSimilarSegments.get(seg);
		HashSet<GpxTrack> trackSeg2 = tracksOfSimilarSegments.get(seg2);
		trackSeg.addAll(trackSeg2);
		trackSeg2.addAll(trackSeg);
	}

	@SuppressWarnings("unused")
	private List<GpxTrackSegment> getSimilarSegments(GpxTrackSegment seg,
			List<GpxTrackSegment> seglist) {
		List<GpxTrackSegment> result = new LinkedList<GpxTrackSegment>();
		for (GpxTrackSegment gpxTrackSegment : seglist) {
			if (isDistanceShorter(seg,gpxTrackSegment,trackDistance)){
				result.add(gpxTrackSegment);
			}
		}
		return result;
	}

	protected void buildSegments() {
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
			overlappingSegments= new LinkedList<List<WayPoint>>();
			for(int n=0;n<segmentLength;n++){
				overlappingSegments.add(new LinkedList<WayPoint>());
			}
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
					for (int index=0;index<overlappingSegments.size();index++) {
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
							if(!currentSegment.get(0).getCoor().equals(currentSegment.get(1).getCoor())){
								GpxTrackSegment newSeg =new ImmutableGpxTrackSegment(currentSegment);
								//double length = newSeg.length();
								//System.out.println(length);
								segments.add(newSeg);
								HashSet<GpxTrack> trackList = new HashSet<GpxTrack>();
								trackList.add(track);
								tracksOfSimilarSegments.put(newSeg,trackList);
							}
							int i=overlappingSegments.indexOf(currentSegment);
							overlappingSegments.set(i, new LinkedList<WayPoint>());
						}
					}
					
					
				}
			}
		}
	}

}
