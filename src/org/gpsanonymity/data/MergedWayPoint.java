package org.gpsanonymity.data;

import java.util.LinkedList;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class MergedWayPoint extends org.openstreetmap.josm.data.gpx.WayPoint{
	private LinkedList<WayPoint> sourceWaypoints;
	private LinkedList<GpxTrackSegment> sourceSegments;
	private LinkedList<GpxTrack> sourceTracks;
	private LinkedList<WayPoint> connections;
	private LinkedList<GpxTrackSegment> mergedSegments;
	private String gpxDate;
	public MergedWayPoint(LinkedList<WayPoint> lp) {
		super(new LatLon(0,0));
		sourceWaypoints = new LinkedList<WayPoint>(lp);
		sourceSegments = new LinkedList<GpxTrackSegment>();
		sourceTracks = new LinkedList<GpxTrack>();
		connections = new LinkedList<WayPoint>();
		mergedSegments = new LinkedList<GpxTrackSegment>();
		calculateNewCoordinates();
		calculateNewDate();
	}
	
	public MergedWayPoint(WayPoint p) {
		super(p);
		sourceWaypoints = new LinkedList<WayPoint>();
		sourceSegments = new LinkedList<GpxTrackSegment>();
		sourceTracks = new LinkedList<GpxTrack>();
		connections = new LinkedList<WayPoint>();
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
	public void addConnection(MergedWayPoint neighbor){
		connections.add(neighbor);
	}

	

}
