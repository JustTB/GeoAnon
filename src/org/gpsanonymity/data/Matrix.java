package org.gpsanonymity.data;
import java.util.Collection;
import java.util.HashMap;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.WayPoint;


public abstract class Matrix<E,F> {
	protected class Tupel<E>{
		private E w1,w2;
		public Tupel(E w1, E w2) {
			this.w1=w1;
			this.w2=w2;
		}
		@Override
		public int hashCode() {
			return w1.hashCode()+w2.hashCode();
		}
		@Override
		public boolean equals(Object o) {
			if(o==this){
				return true;
			}else if (o!=null
					&& getClass()==o.getClass() 
					&& ((Tupel<E>)o).w1.equals(w1)
					&& ((Tupel<E>)o).w2.equals(w2)){
				return true;
			}else{
				return false;
			}
		}
	}
	protected HashMap<Tupel<E>, F> matrix;
	public Matrix() {
		matrix = new HashMap<Tupel<E>, F>();
	}
	public boolean containsKey(E w1, E w2){
		return matrix.containsKey(new Tupel<E>(w1,w2));
	}
	public boolean containsValue(F value){
		return matrix.containsValue(value);
	}
	public F getValue(E w1,E w2){
		return matrix.get(new Tupel<E>(w1,w2));
	}
	public void put(E w1,E w2, F value){
		matrix.put(new Tupel <E>(w1,w2), value);
	}
}
