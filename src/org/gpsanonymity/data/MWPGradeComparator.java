package org.gpsanonymity.data;

import java.util.Comparator;

public class MWPGradeComparator implements Comparator<MergedWayPoint> {

	@Override
	public int compare(MergedWayPoint arg0, MergedWayPoint arg1) {
		return arg0.getTrackGrade()-arg1.getTrackGrade();
	}

}
