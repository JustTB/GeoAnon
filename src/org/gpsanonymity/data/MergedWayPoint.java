package org.gpsanonymity.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class MergedWayPoint extends org.openstreetmap.josm.data.gpx.WayPoint{
	private HashSet<WayPoint> sourceWaypoints;
	private IdentityHashMap<GpxTrackSegment,List<WayPoint>> sourceSegments;
	private IdentityHashMap<GpxTrack,List<WayPoint>> sourceTracks;
	private IdentityHashMap<MergedWayPoint,Boolean> connections;
	private String gpxDate;
	private IdentityHashMap<MergedWayPoint,Integer> connectionGrades;
	private MergedWayPoint updateMergedWayPoint=this;
	public MergedWayPoint(List<WayPoint> lp) {
		super(new LatLon(0,0));
		sourceWaypoints = new HashSet<WayPoint>();
		sourceSegments = new IdentityHashMap<GpxTrackSegment, List<WayPoint>>();
		sourceTracks = new IdentityHashMap<GpxTrack, List<WayPoint>>();
		connections = new IdentityHashMap<MergedWayPoint,Boolean>();
		connectionGrades = new IdentityHashMap<MergedWayPoint, Integer>();
		addWayPoints(lp);
	}
	
	public MergedWayPoint(WayPoint p) {
		super(p);
		sourceWaypoints = new HashSet<WayPoint>();
		sourceSegments = new IdentityHashMap<GpxTrackSegment, List<WayPoint>>();
		sourceTracks = new IdentityHashMap<GpxTrack, List<WayPoint>>();
		connections = new IdentityHashMap<MergedWayPoint,Boolean>();
		connectionGrades = new IdentityHashMap<MergedWayPoint, Integer>();
		addWayPoint(p);
	}

	public boolean addSegment(GpxTrackSegment s, WayPoint wp) {
		if(s.getWayPoints().contains(wp)
				&& sourceWaypoints.contains(wp)){
			if (sourceSegments.containsKey(s)){
				List<WayPoint> entry =sourceSegments.get(s);
				if(!entry.contains(wp)){
					entry.add(wp);
				}
				return true;
			}else{
				List<WayPoint> newOne = new LinkedList<WayPoint>();
				newOne.add(wp);
				sourceSegments.put(s,newOne);
			}
			return true;
		}else{
			return false;
		}
	}
	public boolean addTrack(GpxTrack t, WayPoint wp) {
		if(sourceWaypoints.contains(wp)){
			if (sourceTracks.containsKey(t)){
				List<WayPoint> entry =sourceTracks.get(t);
				if(!entry.contains(wp)){
					entry.add(wp);
				}
				return true;
			}else{
				List<WayPoint> newOne = new LinkedList<WayPoint>();
				newOne.add(wp);
				sourceTracks.put(t,newOne);
			}
			return true;
		}else{
			return false;
		}
	}
	public void addWayPoints(List<WayPoint> wps){
		for (WayPoint wayPoint : wps) {
			addWayPoint(wayPoint, false);
		}
		
	}
	public void addWayPoint(WayPoint p){
		addWayPoint(p, false);
	}
	public void addWayPoint(WayPoint p, boolean forceAsWayPoint) {
		if(MergedWayPoint.class.isInstance(p) && !forceAsWayPoint){
			mergeWith((MergedWayPoint)p);
		}else{
			if (!sourceWaypoints.contains(p)){
				sourceWaypoints.add(p);
				calculateNewCoordinates();
				calculateNewDate();
			}
		}
	}
	public void mergeWith(MergedWayPoint mwp){
		if(mwp==this){
			return;
		}
		mwp=mwp.current();
		assert(connections.size()==connectionGrades.size());
//		addWayPoint((WayPoint)mwp,true);
		sourceWaypoints.addAll(mwp.sourceWaypoints);
		mergeSourceSegments(mwp.sourceSegments);
		mergeSourceTracks(mwp.sourceTracks);
		connections.putAll(mwp.connections);
		mergeConnectionGrades(mwp.connectionGrades);
		LinkedList<MergedWayPoint> keySet = new LinkedList<MergedWayPoint>(mwp.connections.keySet());
		for (MergedWayPoint mwpNeighbor : keySet) {
			mwpNeighbor.disconnect(mwp);
			connect(mwpNeighbor);
		}
		if(getNeighborGrade(mwp)!=0){
			disconnect(mwp);
		}
		mwp.update(this);
		calculateNewCoordinates();
		calculateNewDate();
		assert(connections.size()==connectionGrades.size());
	}
	private void update(MergedWayPoint mergedWayPoint) {
		MergedWayPoint mwp = mergedWayPoint;
		while(mwp.needsUpdate()){
			mwp=mwp.current();
		}
		updateMergedWayPoint =mwp;
	}
	public boolean needsUpdate(){
		return updateMergedWayPoint!=this;
	}
	public MergedWayPoint current(){
		return updateMergedWayPoint;
	}

	private void mergeConnectionGrades(
			IdentityHashMap<MergedWayPoint,Integer> connectionGrades2) {
		for(MergedWayPoint newNeighbor : connectionGrades2.keySet()){
			if(connectionGrades.containsKey(newNeighbor)){
				HashSet<GpxTrack> commonTracks= new HashSet<GpxTrack>(this.sourceTracks.keySet());
				commonTracks.retainAll(newNeighbor.sourceTracks.keySet());
				connectionGrades.put(newNeighbor, commonTracks.size());
				//System.out.println("Connection Grade:" +connectionGrades.get(newNeighbor));
			}else{
				connectionGrades.put(newNeighbor, connectionGrades2.get(newNeighbor));
			}
		}
		
	}

	private void mergeSourceTracks(
			IdentityHashMap<GpxTrack,List<WayPoint>> sourceTracks2) {
		//for each other key segment ...
		for (GpxTrack track : sourceTracks2.keySet()) {
			// ... check if already in sourcetracks ...
			if(sourceTracks.containsKey(track)){
				List<WayPoint> value = sourceTracks.get(track);
				//... if true for each wp in the wplist add only new one 
				for (WayPoint wp : sourceTracks2.get(track)){
					if (value.contains(wp)){
						;//do nothing
					}else{
						value.add(wp);
					}
				}
			}else{
				sourceTracks.put(track, sourceTracks2.get(track));
			}
		}

	}

	private void mergeSourceSegments(
			IdentityHashMap<GpxTrackSegment, List<WayPoint>> sourceSegments2) {
		//for each other key segment ...
		for (GpxTrackSegment seg : sourceSegments2.keySet()) {
			// ... check if already in sourcesegments ...
			if(sourceSegments.containsKey(seg)){
				List<WayPoint> value = sourceSegments.get(seg);
				//... if true for each wp in the wplist add only new one 
				for (WayPoint wp : sourceSegments2.get(seg)){
					if (value.contains(wp)){
						;//do nothing
					}else{
						value.add(wp);
					}
				}
			}else{
				sourceSegments.put(seg, sourceSegments2.get(seg));
			}
		}
		
	}

	/**
	 * Calculates the new coordinates from the sourceWaypoints
	 * 
	 */
	public void calculateNewCoordinates() {
		LatLon result = MergeGPS.calculateCentroid(sourceWaypoints);
		if (result!=null){
			this.lat=Math.round(result.lat()*10000000)/10000000.0;
			this.lon=Math.round(result.lon()*10000000)/10000000.0;
		}
	}
	public void calculateNewDate() {
		gpxDate=MergeGPS.simpleGeneralizeDate(sourceWaypoints);
		//TODO
	}
	public int getPointGrade(){
		return sourceWaypoints.size();
	}
	public String toString() {
		return "WayPoint {"+ new LatLon(this.lat, this.lon).toString() + "time="+gpxDate+" grade:"+getTrackGrade()+"}";
	}
	public void connect(MergedWayPoint neighbor){
		neighbor=neighbor.current();
		assert(connections.size()==connectionGrades.size());
		if(neighbor==this){
			return;
		}
		if (connectionGrades.containsKey(neighbor)){
			connect(neighbor,connectionGrades.get(neighbor)+1);
		}else{
			connect(neighbor, 1);
		}
		assert(connections.size()==connectionGrades.size());
	}
	public void connect(MergedWayPoint neighbor, int grade){
		neighbor=neighbor.current();
		assert(connections.size()==connectionGrades.size());
		connections.put(neighbor,false);
		connectionGrades.put(neighbor, grade);
		neighbor.connections.put(this,false);
		neighbor.connectionGrades.put(this, grade);
		assert(connections.size()==connectionGrades.size());
	}
	public boolean connectSameTracks(MergedWayPoint neighbor, double distance, double minimalSpeed){
		return connectSameTracks(neighbor, distance, minimalSpeed, 0);
	}
	public boolean connectSameTracks(MergedWayPoint neighbor, double distance, double minimalSpeed, int k){
		neighbor=neighbor.current();
		boolean establish=false;
		int count=0;
		for (Iterator<GpxTrack> trackIter = sourceTracks.keySet().iterator(); trackIter.hasNext();) {
			GpxTrack thisTrack = (GpxTrack) trackIter.next();
			for (Iterator<GpxTrack> trackIter2 = neighbor.sourceTracks.keySet().iterator(); trackIter2.hasNext();) {
				GpxTrack neigborTrack = (GpxTrack) trackIter2.next();
				if (neigborTrack.equals(thisTrack)&&(isRealNeighbor(neighbor,thisTrack, distance, minimalSpeed))){
					//assures that we have k connections to a neighbor
					count++;
					if(count>=k && !establish){
						establish=true;
					}
				}
			}
			if(establish){
				break;
			}
		}
		if (establish){		
			connections.put(neighbor,false);
			connectionGrades.put(neighbor,count);
			neighbor.connections.put(this,false);
			neighbor.connectionGrades.put(this,count);
		}
		assert(connections.size()==connectionGrades.size());
		return establish;
	}
	private boolean isRealNeighbor(MergedWayPoint neighbor, GpxTrack onTrack, double distance, double minimalSpeed) {
		neighbor=neighbor.current();
		List<WayPoint> sourcePoint = sourceTracks.get(onTrack);
		List<WayPoint> neighborSourcePoint = neighbor.sourceTracks.get(onTrack);
		//heuristic example for minimalSpeed=0.5:
		//0.5m/s -->for two fields maximum: 2*distance/0.5=maxTimedifference in s
		double maxTimeDifference = (2*distance)/minimalSpeed;
		for (WayPoint wayPoint : neighborSourcePoint) {
			for (WayPoint wayPoint2 : sourcePoint) {
				if (0<Math.abs(wayPoint.time-wayPoint2.time)
						&&Math.abs(wayPoint.time-wayPoint2.time)<maxTimeDifference){
					return true;
				}
			}
		}
		return false;
	}



	public void markConnection(MergedWayPoint neighbor){
		neighbor=neighbor.current();
		if(connections.containsKey(neighbor)){
			connections.put(neighbor, true);
			neighbor.connections.put(this, true);
		}
		assert(connections.size()==connectionGrades.size());
	}
	public Boolean isMarked(MergedWayPoint neighbor){
		neighbor=neighbor.current();
		if(connections.containsKey(neighbor)){
			assert(connections.size()==connectionGrades.size());
			return connections.get(neighbor);
		}else{
			assert(connections.size()==connectionGrades.size());
			return null;
		}
	}
	
	public boolean disconnect(MergedWayPoint neighbor){
		neighbor=neighbor.current();
		assert(connections.size()==connectionGrades.size());
		if (null!=connections.remove(neighbor)){
			connectionGrades.remove(neighbor);
			neighbor.connections.remove(this);
			neighbor.connectionGrades.remove(this);
			assert(connections.size()==connectionGrades.size());
			return true;
		}
		assert(connections.size()==connectionGrades.size());
		return false;
	}
	public Collection<MergedWayPoint> getNeighbors(){
		assert(connections.size()==connectionGrades.size());
		return connections.keySet();
	}

	public MergedWayPoint getHighestNotMarkedNeighbor(int k) {
		assert(connections.size()==connectionGrades.size());
		Set<MergedWayPoint> keys = connections.keySet();
		MergedWayPoint highestMwp=null;
		int highestGrade=0;
		for (MergedWayPoint mwp : keys) {
			Boolean color = connections.get(mwp);
			Integer grade = connectionGrades.get(mwp);
			if(color==null || grade==null){
				System.out.println("This should not happen!!!!" + color + " " + grade);
			}
			if (!color && grade>=k && grade>highestGrade){
				highestGrade=grade;
				highestMwp=mwp;
			}
		}
		return highestMwp;
	}

	public int getNeighborCount() {
		return connections.size();
	}

	public void removeIfExist(WayPoint wayPoint) {
		if (sourceWaypoints.contains(wayPoint) && wayPoint!=null) {
			sourceWaypoints.remove(wayPoint);
			calculateNewCoordinates();
			calculateNewDate();
		}
		
	}

	public boolean containsNeighbor(MergedWayPoint mwp) {
		mwp=mwp.current();
		return connections.containsKey(mwp);
	}

	public int getNeighborGrade(MergedWayPoint mergedWayPoint) {
		mergedWayPoint=mergedWayPoint.current();
		if(mergedWayPoint!=null){
			Integer entry = connectionGrades.get(mergedWayPoint);
			if (entry!=null){
				return entry;
			}
		}
		return 0;
	}

	public boolean hasSameTracks(MergedWayPoint mwp2) {
		mwp2=mwp2.current();
		for(GpxTrack track : sourceTracks.keySet()){
			for(GpxTrack track2 : mwp2.sourceTracks.keySet()){
				if(track==track2){
					return true;
				}
			}
		}
		return false;
	}

	public void deleteDistantNeighbors(double distance) {
		assert(connections.size()==connectionGrades.size());
		LinkedList<MergedWayPoint> keySet = new LinkedList<MergedWayPoint>(connections.keySet());
		for(MergedWayPoint neighbor: keySet){
			if(neighbor.getCoor().greatCircleDistance(this.getCoor())>distance){
				disconnect(neighbor);
			}
		}
		
	}

	public int disconnectAll() {
		LinkedList<MergedWayPoint> connectionCopy = new LinkedList<MergedWayPoint>(connections.keySet());
		for (MergedWayPoint mwp : connectionCopy) {
			disconnect(mwp);
		}
		return connectionCopy.size();
		
	}

	public Collection<GpxTrackSegment> getSegments() {
		return sourceSegments.keySet();
	}

	public boolean isConnectionGradeCorrect() {
		return connectionGrades.size()==connections.size();
	}

	public int getTrackGrade() {
		return sourceTracks.size();
	}

	public Collection<? extends GpxTrack> getTracks() {
		return sourceTracks.keySet();
	}

	public Collection<WayPoint> getWayPoints() {
		return sourceWaypoints;
	}
	

}
