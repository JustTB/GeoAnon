package org.gpsanonymity.tests;

import org.junit.Test;

public class HashTests {
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
	}
	
	@Test
	public void testHashes() {
		System.out.println(new Tupel<Integer>(3,4).hashCode());
		System.out.println(new Tupel<Integer>(3,4).hashCode());
	}

}
