package org.gpsanonymity.io;

import java.io.Serializable;

import org.openstreetmap.josm.data.Bounds;

public class SerilizableBounds implements Serializable {
	private transient Bounds bounds;

	public SerilizableBounds(Bounds bounds) {
		this.bounds=bounds;
	}

	private static final long serialVersionUID = 6507479822177319284L;
	public Bounds getBounds() {
		return bounds;
	}

}
