package org.gpsanonymity.tests;

import java.io.FileNotFoundException;

import org.gpsanonymity.stats.Statistician;
import org.junit.Test;

public class StatisticianTest {
	
	@Test
	public void writeTest(){
		Statistician stater = new Statistician();
		stater.write("output/testStats.dat");
		Statistician stater2;
		try {
			stater2 = new Statistician("output/testStats.dat");
			System.out.println(stater.equals(stater2));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
