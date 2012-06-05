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

public class SegmentCloud extends Cloud{

	protected List<GpxTrack> sourceTracks;
	protected int k;
	protected double trackDistance;
	protected int segmentLength;
	protected IdentityHashMap<GpxTrackSegment,HashSet<GpxTrackSegment>> similarSegments=new IdentityHashMap<GpxTrackSegment, HashSet<GpxTrackSegment>>();
	protected List<GpxTrackSegment> segments=new LinkedList<GpxTrackSegment>();
	protected List<GpxTrack> tracks;
	protected List<MergedWayPoint> mergedWayPoints=new LinkedList<MergedWayPoint>();
	protected IdentityHashMap<GpxTrackSegment,HashSet<GpxTrack>> tracksOfSimilarSegments= new IdentityHashMap<GpxTrackSegment, HashSet<GpxTrack>>();
	protected boolean ignoreDirection;
	protected double angelAllowance;
	protected Statistician statistician;
	
	protected SegmentCloud(){}

	public SegmentCloud(List<GpxTrack> morePointTracks
			,int k
			,double trackDistance
			,int segmentLength
			,boolean ignoreDirection
			,double angelAllowance
			,Statistician statistician) {
		super(morePointTracks,k,statistician);
		this.trackDistance=trackDistance;
		this.segmentLength=segmentLength;
		this.angelAllowance=angelAllowance;
		this.ignoreDirection=ignoreDirection;
		initialize();
	}

	protected void initialize() {
		System.out.println("Status: Build segments.");
		buildSegments();
		IOFunctions.exportTrackSegments(segments, "output/BuildedSegments.gpx");
		//System.out.println("Status: Find Segmentcluster");
		//findSegmentCluster();
		statistician.setSourceConnectionNumber(segments.size());
		System.out.println("Status: Find Similar Segments");
		findSimilarSegments();
		System.out.println("Status: Eliminate similar segments with grade<"+k);
		eliminateLowerGradeSegments();
		IOFunctions.exportTrackSegments(similarSegments.keySet(), "output/SimilarSegments.gpx");
		System.out.println("Status: Merge similar Segments");
		mergeSimilarSegments();
		for (MergedWayPoint mwp : mergedWayPoints) {
			if(!mwp.isConnectionGradeCorrect()){
				System.out.println("its here");
			}
		}
		IOFunctions.exportWayPoints((List)mergedWayPoints, "output/MergedWayPoints.gpx");
		System.out.println("Status: Eliminate wayPoints with grade<"+k);
		eliminateLowerGradeWayPoints();
		System.out.println("Status: Check Neighborhood");
		for (MergedWayPoint mwp : mergedWayPoints) {
			if(!mwp.isConnectionGradeCorrect()){
				System.out.println("its here");
			}
		}
		checkNeighborHood();
		//System.out.println("Status:Delete big distances");
		//deleteBigDistances();
		System.out.println("Status: Build tracks!!");
		//mergeNearWayPoints();
		for (MergedWayPoint mwp : mergedWayPoints) {
			if(!mwp.isConnectionGradeCorrect()){
				System.out.println("its here");
			}
		}
		IOFunctions.exportWayPoints((List)mergedWayPoints, "output/EliminatedMergedWayPoints.gpx");
		buildTracks();
		//System.out.println("Status:Delete short tracks");
		//deleteShortTracks();
		System.out.println("Status: Done!!");
	}

	protected void mergeNearWayPoints() {
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

	protected void mergeSimilarSegments() {
		List<WayPoint> maxWPs,minWPs;
		while(!similarSegments.isEmpty()) {
			LinkedList<HashSet<GpxTrackSegment>> hashSetList = new LinkedList<HashSet<GpxTrackSegment>>(similarSegments.values());
			HashSet<GpxTrackSegment> list = hashSetList.get(0);
			maxWPs =new LinkedList<WayPoint>();
			minWPs =new LinkedList<WayPoint>();
			//this only works with segmentsLength=2
			boolean maxMin = false;
			List<MergedWayPoint> newMwps;
			if(maxMin){
				CoordinateWayPointComparator cwpc= new CoordinateWayPointComparator();
				for (GpxTrackSegment gpxTrackSegment : list) {
					similarSegments.remove(gpxTrackSegment);
					List<WayPoint> wps = new LinkedList<WayPoint>(gpxTrackSegment.getWayPoints());
					Collections.sort(wps,cwpc);
					maxWPs.add(wps.get(0));
					minWPs.add(wps.get(1));
				}
				newMwps = MergeGPS.mergeWithKMeans(maxWPs, 1);
				newMwps.addAll(MergeGPS.mergeWithKMeans(minWPs, 1));
			}else{
				LinkedList<WayPoint> wps = new LinkedList<WayPoint>();
				for (GpxTrackSegment gpxTrackSegment : list) {
					similarSegments.remove(gpxTrackSegment);
					wps.addAll(gpxTrackSegment.getWayPoints());
				}
				LinkedList<WayPoint> newWps = new LinkedList<WayPoint>();
				for (WayPoint wp : wps) {
					MergedWayPoint mwp = ((MergedWayPoint) wp).current();
					newWps.add(mwp);
				}
				newMwps=MergeGPS.mergeWithKMeans(newWps, segmentLength);
			}
			MergedWayPoint ancessor=null;
			for (Iterator iterator = newMwps.iterator(); iterator.hasNext();) {
				MergedWayPoint mwp = (MergedWayPoint) iterator
						.next();
				if (ancessor!=null && mwp.getNeighborGrade(ancessor)==0){
					mwp.connect(ancessor,list.size());
				}
				ancessor=mwp;
				
			}
			//System.out.println("NeighborGrade: "+newWps.get(0).getNeighborGrade(newWps.get(newWps.size()-1)));
			mergedWayPoints.addAll(newMwps);
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
