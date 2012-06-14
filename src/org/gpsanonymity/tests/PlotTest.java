package org.gpsanonymity.tests;

import org.junit.Test;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.layout.AutoGraphLayout;
import com.panayotis.gnuplot.terminal.PostscriptTerminal;

public class PlotTest {
	private static final String gnuplotpath = "/usr/bin/gnuplot";
	private static final String outputPath = "output/plotTest.ps";
	@Test
	public void generalTest(){
		EPSTerminal(outputPath);
		defaultTerminal(gnuplotpath,outputPath);
	}
    private static JavaPlot defaultTerminal(String gnuplotpath, String outputPath) {
        JavaPlot gp = new JavaPlot();
        gp.setTerminal(new PostscriptTerminal(outputPath));
    	gp.setTitle("Default Terminal Title");
        gp.newGraph();
        gp.addPlot("sin(x+2)");
        gp.addPlot("sin(x+1)");
        gp.setMultiTitle("Global test title");
        AutoGraphLayout lo = new AutoGraphLayout();
        gp.getPage().setLayout(lo);
        gp.plot();
        return gp;
    }
    private static JavaPlot EPSTerminal(String outputPath) {
        JavaPlot p = new JavaPlot();

        PostscriptTerminal epsf = new PostscriptTerminal(System.getProperty("user.home") +
                System.getProperty("file.separator") + "output.eps");
        epsf.setColor(true);
        p.setTerminal(epsf);

        p.setTitle("Postscript Terminal Title");
        p.addPlot("sin (x)");
        p.addPlot("sin(x)*cos(x)");
        p.newGraph();
        p.addPlot("cos(x)");
        p.setTitle("Trigonometric functions -1");
        p.setMultiTitle("Trigonometric functions");
        p.plot();
        return p;
    }
}
