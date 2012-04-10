package org.gpsanonymity.data;

import java.util.Collection;
import java.util.HashMap;
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
	private LinkedList<WayPoint> sourceWaypoints;
	private LinkedList<GpxTrackSegment> sourceSegments;
	private LinkedList<GpxTrack> sourceTracks;
	private HashMap<MergedWayPoint,Boolean> connections;
	private LinkedList<GpxTrackSegment> mergedSegments;
	private String gpxDate;
	public MergedWayPoint(LinkedList<WayPoint> lp) {
		super(new LatLon(0,0));
		sourceWaypoints = new LinkedList<WayPoint>(lp);
		sourceSegments = new LinkedList<GpxTrackSegment>();
		sourceTracks = new LinkedList<GpxTrack>();
		connections = new HashMap<MergedWayPoint,Boolean>();
		mergedSegments = new LinkedList<GpxTrackSegment>();
		calculateNewCoordinates();
		calculateNewDate();
	}
	
	public MergedWayPoint(WayPoint p) {
		super(p);
		sourceWaypoints = new LinkedList<WayPoint>();
		sourceSegments = new LinkedList<GpxTrackSegment>();
		sourceTracks = new LinkedList<GpxTrack>();
		connections = new HashMap<MergedWayPoint,Boolean>();
		mergedSegments = new LinkedList<GpxTrackSegment>();
		addWayPoint(p);
	}

	public void addSegment(GpxTrackSegment s) {
		sourceSegments.add(s);
	}
	public void addTrack(GpxTrack t) {
		sourceTracks.add(t);
	}
	public void addWayPoint(WayPoint p) {
		sourceWaypoints.add(p);
	}
	public void addWayPoints(LinkedList<WayPoint> wp) {
		sourceWaypoints.addAll(wp);
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
	public boolean connectSameTracks(MergedWayPoint neighbor){
		boolean establish=false;
		for (Iterator<GpxTrack> trackIter = sourceTracks.iterator(); trackIter.hasNext();) {
			GpxTrack thisTrack = (GpxTrack) trackIter.next();
			for (Iterator<GpxTrack> trackIter2 = neighbor.sourceTracks.iterator(); trackIter2.hasNext();) {
				GpxTrack neigborTrack = (GpxTrack) trackIter2.next();
				if (neigborTrack.equals(thisTrack)){
					establish=true;
					break;
				}
			}
			if(establish){
				break;
			}
		}
		if (establish){		
			connections.put(neighbor,false);
			neighbor.connections.put(this,false);
		}
		return establish;
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
		boolean result;
		if (result=connections.remove(neighbor)){
			neighbor.connections.remove(this);
		}
		return result;
	}
	public Collection<MergedWayPoint> getNeighbors(){
		return connections.keySet();
	}

	public MergedWayPoint getOneNotMarkedNeighbor() {
		Set<MergedWayPoint> keys = connections.keySet();
		for (MergedWayPoint mwp : keys) {
			if (!connections.get(mwp)){
				return mwp;
			}
		}
		return null;
		
	}

	public int getNeighborCount() {
		return connections.size();
	}

	

}
