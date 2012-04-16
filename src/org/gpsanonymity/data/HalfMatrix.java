package org.gpsanonymity.data;
import java.util.Collection;
import java.util.HashMap;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class HalfMatrix<E,F> extends Matrix<E, F>{
	public void put(E w1,E w2, F value){
		matrix.putKeyValue(new Tupel <E>(w1,w2), value);
		matrix.putKeyValue(new Tupel <E>(w2,w1), value);
	}
}
