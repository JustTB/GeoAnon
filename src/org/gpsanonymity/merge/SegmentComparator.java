package org.gpsanonymity.merge;

import java.util.Comparator;

import org.openstreetmap.josm.data.gpx.GpxTrackSegment;

public class SegmentComparator implements Comparator<GpxTrackSegment> {

	@Override
	public int compare(GpxTrackSegment arg0, GpxTrackSegment arg1) {
		double result = arg0.length()-arg1.length();
		if(result>0){
			return 1;
		}else if (result<0){
			return -1;
		}else{
			return 0; 
		}
	}

}
