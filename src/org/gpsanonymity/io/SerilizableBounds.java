package org.gpsanonymity.io;

import java.io.Serializable;

import org.openstreetmap.josm.data.Bounds;

public class SerilizableBounds implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1582459782395120329L;
	private double maxLon;
	private double maxLat;
	private double minLon;
	private double minLat;
	public SerilizableBounds(Bounds bounds) {
		minLat=bounds.getMin().getY();
		minLon=bounds.getMin().getX();
		maxLat=bounds.getMax().getY();
		maxLon=bounds.getMax().getX();
	}
	public Bounds getBounds(){
		return new Bounds(minLat,minLon,maxLat,maxLon);
	}

	

}
