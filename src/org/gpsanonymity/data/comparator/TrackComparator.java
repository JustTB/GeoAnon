package org.gpsanonymity.data.comparator;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class TrackComparator implements Comparator<Collection<? extends WayPoint>> {

	Collection<WayPoint> referenceTrack;;
	public TrackComparator() {
		super();
		referenceTrack=new LinkedList<WayPoint>();
	}
	public TrackComparator(Collection<WayPoint> referenceTrack) {
		super();
		this.referenceTrack = new LinkedList<WayPoint>(referenceTrack);
	}
	public int compare(Collection<? extends WayPoint> o1, Collection<? extends WayPoint> o2) {
		double diff =	MergeGPS.hausDorffDistance(o1, referenceTrack) - 
						MergeGPS.hausDorffDistance(o2, referenceTrack);
		if (diff>0){
			return 1;
		}else if (diff<0){
			return -1;
		}else{
			return 0;
		}
	}
	public void setReferenceTrack(Collection<WayPoint> referenceTrack){
		this.referenceTrack = new LinkedList<WayPoint>(referenceTrack);
	}

}
