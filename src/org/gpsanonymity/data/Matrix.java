package org.gpsanonymity.data;
import java.util.Collection;


public abstract class Matrix<E,F> {
	public class Tupel<G>{
		private G x,y;
		public G getX() {
			return x;
		}
		public void setX(G w1) {
			this.x = w1;
		}
		public G getY() {
			return y;
		}
		public void setY(G w2) {
			this.y = w2;
		}
		public Tupel(G w1, G w2){
			this.x=w1;
			this.y=w2;
		}
		public boolean equals(Tupel<G> g){
			if(g!=null && g.x==x && g.y==y){
				return true;
			}else{
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return x.hashCode()*5+y.hashCode()*7;
		}
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o) {
			if(o==this){
				return true;
			}else if (o!=null
					&& getClass().equals(o.getClass()) 
					&& ((Tupel<E>)o).x.equals(x)
					&& ((Tupel<E>)o).y.equals(y)){
				return true;
			}else{
				return false;
			}
		}
	}
	protected BiMap<Tupel<E>, F> matrix;
	public Matrix() {
		matrix = new BiMap<Tupel<E>, F>();
	}
	public boolean containsKey(E w1, E w2){
		return matrix.containsKey(new Tupel<E>(w1,w2));
	}
	public boolean containsValue(F value){
		return matrix.containsValue(value);
	}
	public F getValue(E w1,E w2){
		return matrix.getValue(new Tupel<E>(w1,w2));
	}
	public Tupel<E> getKey(F value){
		return matrix.getKey(value);
	}
	public Collection<F> values(){
		return matrix.values();
	}
	public void put(E w1,E w2, F value){
		matrix.putKeyValue(new Tupel <E>(w1,w2), value);
	}
	public void printTupel(F value){
		System.out.println(matrix.getKey(value).x);
		System.out.println(matrix.getKey(value).y);
	}
}
