// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.data.gpx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.openstreetmap.josm.data.Bounds;

public class ImmutableGpxTrackSegment implements GpxTrackSegment {

    protected Collection<WayPoint> wayPoints;
    private final Bounds bounds;
    private final double length;

    public ImmutableGpxTrackSegment(Collection<WayPoint> wayPoints) {
        this.wayPoints = Collections.unmodifiableCollection(new ArrayList<WayPoint>(wayPoints));
        this.bounds = calculateBounds();
        this.length = calculateLength();
    }
    @Override
    public boolean equals(Object obj) {
    	if(obj==null){
    		return false;
    	}else if(!obj.getClass().equals(this.getClass())){
    		return false;
    	}else{
    		ImmutableGpxTrackSegment igts =(ImmutableGpxTrackSegment)obj;
    		if(wayPoints.size()!=igts.wayPoints.size()){
    			return false;
    		}else{
    			Iterator<WayPoint> iterThis = wayPoints.iterator();
    			Iterator<WayPoint> iterThat = igts.wayPoints.iterator();
    			while(iterThis.hasNext() && iterThat.hasNext()){
    				if(!iterThis.next().equals(iterThat.next())){
    					return false;
    				}
    			}
    		}
    	}
    	return true;
    }
    private Bounds calculateBounds() {
        Bounds result = null;
        for (WayPoint wpt: wayPoints) {
            if (result == null) {
                result = new Bounds(wpt.getCoor());
            } else {
                result.extend(wpt.getCoor());
            }
        }
        return result;
    }

    private double calculateLength() {
        double result = 0.0; // in meters
        WayPoint last = null;
        for (WayPoint tpt : wayPoints) {
            if(last != null){
                Double d = last.getCoor().greatCircleDistance(tpt.getCoor());
                if(!d.isNaN() && !d.isInfinite()) {
                    result += d;
                }
            }
            last = tpt;
        }
        return result;
    }

    public Bounds getBounds() {
        if (bounds == null)
            return null;
        else
            return new Bounds(calculateBounds());
    }

    public Collection<WayPoint> getWayPoints() {
        return wayPoints;
    }
    public void setWayPoints(Collection<WayPoint> wayPoints) {
        this.wayPoints=wayPoints;
    }

    public double length() {
        return length;
    }

    public int getUpdateCount() {
        return 0;
    }

}
