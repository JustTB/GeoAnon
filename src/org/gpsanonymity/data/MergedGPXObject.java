package org.gpsanonymity.data;

public interface MergedGPXObject {
	public void colorConnection(MergedGPXObject neighbor);
	public Boolean getColor(MergedGPXObject neighbor);

}
