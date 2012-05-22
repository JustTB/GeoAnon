package org.gpsanonymity.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gpsanonymity.merge.MergeGPS;
import org.junit.Assume;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class MergedWayPoint extends org.openstreetmap.josm.data.gpx.WayPoint{
	private HashSet<WayPoint> sourceWaypoints;
	private IdentityHashMap<GpxTrackSegment,List<WayPoint>> sourceSegments;
	private IdentityHashMap<GpxTrack,List<WayPoint>> sourceTracks;
	private IdentityHashMap<MergedWayPoint,Boolean> connections;
	private LinkedList<GpxTrackSegment> mergedSegments;
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
		mergedSegments = new LinkedList<GpxTrackSegment>();
		addWayPoints(lp);
	}
	
	public MergedWayPoint(WayPoint p) {
		super(p);
		sourceWaypoints = new HashSet<WayPoint>();
		sourceSegments = new IdentityHashMap<GpxTrackSegment, List<WayPoint>>();
		sourceTracks = new IdentityHashMap<GpxTrack, List<WayPoint>>();
		connections = new IdentityHashMap<MergedWayPoint,Boolean>();
		connectionGrades = new IdentityHashMap<MergedWayPoint, Integer>();
		mergedSegments = new LinkedList<GpxTrackSegment>();
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
		updateMergedWayPoint =mergedWayPoint;	
	}
	public boolean needsUpdate(){
		return updateMergedWayPoint!=this;
	}
	public WayPoint update(){
		return updateMergedWayPoint;
	}

	private void mergeConnectionGrades(
			IdentityHashMap<MergedWayPoint,Integer> connectionGrades2) {
		for(MergedWayPoint mwp : connectionGrades2.keySet()){
			if(connectionGrades.containsKey(mwp)){
				connectionGrades.put(mwp, connectionGrades.get(mwp)+connectionGrades2.get(mwp));
				//System.out.println("Connection Grade:" +connectionGrades.get(mwp));
			}else{
				connectionGrades.put(mwp, connectionGrades2.get(mwp));
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
		this.lat=result.lat();
		this.lon=result.lon();
	}
	public void calculateNewDate() {
		gpxDate=MergeGPS.simpleGeneralizeDate(sourceWaypoints);
		//TODO
	}
	public int getGrade(){
		return sourceWaypoints.size();
	}
	public String toString() {
		return "WayPoint {"+ new LatLon(this.lat, this.lon).toString() + "time="+gpxDate+" grade:"+getGrade()+"}";
	}
	public void connect(MergedWayPoint neighbor){
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
		connections.put(neighbor,false);
		connectionGrades.put(neighbor, grade);
		neighbor.connections.put(this,false);
		neighbor.connectionGrades.put(this, grade);
	}
	public boolean connectSameTracks(MergedWayPoint neighbor, double distance, double minimalSpeed){
		return connectSameTracks(neighbor, distance, minimalSpeed, 0);
	}
	public boolean connectSameTracks(MergedWayPoint neighbor, double distance, double minimalSpeed, int k){
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

		return establish;
	}
	private boolean isRealNeighbor(MergedWayPoint neighbor, GpxTrack onTrack, double distance, double minimalSpeed) {
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



	public void colorConnection(MergedWayPoint neighbor){
		if(connections.containsKey(neighbor)){
			connections.put(neighbor, true);
			neighbor.connections.put(neighbor, true);
		}
	}
	public Boolean getColor(MergedWayPoint neighbor){
		if(connections.containsKey(neighbor)){
			return connections.get(neighbor);
		}else{
			return null;
		}
	}
	
	public boolean disconnect(MergedWayPoint neighbor){
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
		return connections.keySet();
	}

	public MergedWayPoint getOneNotMarkedNeighbor(int k) {
		Set<MergedWayPoint> keys = connections.keySet();
		for (MergedWayPoint mwp : keys) {
			Boolean color = connections.get(mwp);
			Integer grade = connectionGrades.get(mwp);
			if(color==null || grade==null){
				System.out.println("This should not happen!!!!" + color + " " + grade);
			}
			if (!color && grade>=k){
				return mwp;
			}
		}
		return null;
		
	}

	public int getNeighborCount() {
		return connections.size();
	}

	public void removeIfExist(WayPoint wayPoint) {
		if (sourceWaypoints.contains(wayPoint)) {
			sourceWaypoints.remove(wayPoint);
			calculateNewCoordinates();
			calculateNewDate();
		}
		
	}

	public boolean containsNeighbor(MergedWayPoint mwp) {
		return connections.containsKey(mwp);
	}

	public int getNeighborGrade(MergedWayPoint mergedWayPoint) {
		if(mergedWayPoint!=null){
			Integer entry = connectionGrades.get(mergedWayPoint);
			if (entry!=null){
				return entry;
			}
		}
		return 0;
	}

	public boolean hasSameTracks(MergedWayPoint mwp2) {
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
		LinkedList<MergedWayPoint> keySet = new LinkedList<MergedWayPoint>(connections.keySet());
		for(MergedWayPoint neighbor: keySet){
			if(neighbor.getCoor().greatCircleDistance(this.getCoor())>distance){
				disconnect(neighbor);
			}
		}
		
	}

	public void disconnectAll() {
		LinkedList<MergedWayPoint> connectionCopy = new LinkedList<MergedWayPoint>(connections.keySet());
		for (MergedWayPoint mwp : connectionCopy) {
			disconnect(mwp);
		}
		
	}
	

}
