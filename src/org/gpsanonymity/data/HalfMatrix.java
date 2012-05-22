package org.gpsanonymity.data;


public class HalfMatrix<E,F> extends Matrix<E, F>{
	public void put(E w1,E w2, F value){
		matrix.putKeyValue(new Tupel <E>(w1,w2), value);
		matrix.putKeyValue(new Tupel <E>(w2,w1), value);
	}
}
