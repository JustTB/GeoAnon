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
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.preferences.display.GPXSettingsPanel;
import org.openstreetmap.josm.io.GpxWriter;


public class MergedWayPoint extends org.openstreetmap.josm.data.gpx.WayPoint{
	private LinkedList<WayPoint> sourceWaypoints;
	private HashMap<GpxTrackSegment,WayPoint> sourceSegments;
	private HashMap<GpxTrack,WayPoint> sourceTracks;
	private HashMap<MergedWayPoint,Boolean> connections;
	private LinkedList<GpxTrackSegment> mergedSegments;
	private String gpxDate;
	public MergedWayPoint(LinkedList<WayPoint> lp) {
		super(new LatLon(0,0));
		sourceWaypoints = new LinkedList<WayPoint>(lp);
		sourceSegments = new HashMap<GpxTrackSegment, WayPoint>();
		sourceTracks = new HashMap<GpxTrack, WayPoint>();
		connections = new HashMap<MergedWayPoint,Boolean>();
		mergedSegments = new LinkedList<GpxTrackSegment>();
		calculateNewCoordinates();
		calculateNewDate();
	}
	
	public MergedWayPoint(WayPoint p) {
		super(p);
		sourceWaypoints = new LinkedList<WayPoint>();
		sourceSegments = new HashMap<GpxTrackSegment, WayPoint>();
		sourceTracks = new HashMap<GpxTrack, WayPoint>();
		connections = new HashMap<MergedWayPoint,Boolean>();
		mergedSegments = new LinkedList<GpxTrackSegment>();
		addWayPoint(p);
	}

	public boolean addSegment(GpxTrackSegment s, WayPoint wp) {
		if(s.getWayPoints().contains(wp)
				&& sourceWaypoints.contains(wp)){
			sourceSegments.put(s,wp);
			return true;
		}else{
			return false;
		}
	}
	public boolean addTrack(GpxTrack t, WayPoint wp) {
		if(sourceWaypoints.contains(wp)){
			sourceTracks.put(t,wp);
			return true;
		}else{
			return false;
		}
	}
	public void addWayPoint(WayPoint p) {
		if (!sourceWaypoints.contains(p)){
			sourceWaypoints.add(p);
			calculateNewCoordinates();
			calculateNewDate();
		}
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
	public boolean connectSameTracks(MergedWayPoint neighbor, double distance, double minimalSpeed){
		boolean establish=false;

		for (Iterator<GpxTrack> trackIter = sourceTracks.keySet().iterator(); trackIter.hasNext();) {
			GpxTrack thisTrack = (GpxTrack) trackIter.next();
			for (Iterator<GpxTrack> trackIter2 = neighbor.sourceTracks.keySet().iterator(); trackIter2.hasNext();) {
				GpxTrack neigborTrack = (GpxTrack) trackIter2.next();
				if (neigborTrack.equals(thisTrack)&&(isRealNeighbor(neighbor,thisTrack, distance, minimalSpeed))){
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
	private boolean isRealNeighbor(MergedWayPoint neighbor, GpxTrack onTrack, double distance, double minimalSpeed) {
		WayPoint sourcePoint = sourceTracks.get(onTrack);
		WayPoint neighborSourcePoint = neighbor.sourceTracks.get(onTrack);
		//heuristic example for minimalSpeed=0.5:
		//0.5m/s -->for two fields maximum: 2*distance/0.5=maxTimedifference in s
		double maxTimeDifference = (2*distance)/minimalSpeed;
		if (0<Math.abs(sourcePoint.time-neighborSourcePoint.time)&&Math.abs(sourcePoint.time-neighborSourcePoint.time)<maxTimeDifference){
			return true;
		}else{
			return false;
		}
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

	public void removeIfExist(WayPoint wayPoint) {
		if (sourceWaypoints.contains(wayPoint)) {
			sourceWaypoints.remove(wayPoint);
			calculateNewCoordinates();
			calculateNewDate();
		}
		
	}
	

}
