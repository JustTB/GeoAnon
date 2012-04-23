package org.gpsanonymity.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class MergedTrackSegment extends ImmutableGpxTrackSegment {

	private HashMap<GpxTrackSegment, Boolean> connections;
	private List<GpxTrackSegment> sourceSegments;
	
	public MergedTrackSegment(Collection<WayPoint> wayPoints) {
		super(wayPoints);
		connections = new HashMap<GpxTrackSegment, Boolean>();
		sourceSegments= new LinkedList<GpxTrackSegment>();
		sourceSegments.add(this);
	}
	public MergedTrackSegment(List<GpxTrackSegment> content){
		super(content.get(0).getWayPoints());
		connections = new HashMap<GpxTrackSegment, Boolean>();
		sourceSegments = new LinkedList<GpxTrackSegment>(content);
		calculateWaypoints();
	}
	private void calculateWaypoints() {
		mergeSegments();
		//TODO
	}
	private void mergeSegments() {
		List<MergedWayPoint> newWaypoints = new LinkedList<MergedWayPoint>();
		for (GpxTrackSegment seg : sourceSegments) {
			for(GpxTrackSegment seg2 : sourceSegments) {
				if(seg!=seg2 ){
					seg.getWayPoints();
				}
			}
		}
		
	}
	public void addConnection(GpxTrackSegment neighbor) {
		connections.put(neighbor, false);

	}
	public void colorConnection(MergedTrackSegment neighbor) {
		connections.put(neighbor,true);
		neighbor.connections.put(this,true);
		
	}
	public Boolean getColor(MergedTrackSegment neighbor) {
		return connections.get(neighbor);
	}
	
	public boolean disconnect(MergedTrackSegment neighbor){
		boolean result;
		if (result=connections.remove(neighbor)){
			neighbor.connections.remove(this);
		}
		return result;
	}
}
