package org.gpsanonymity.stats;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.CloseAction;

import org.gpsanonymity.Main;
import org.gpsanonymity.data.methods.MinimalAreaCloud;
import org.gpsanonymity.io.Importer;
import org.openstreetmap.josm.actions.NewAction;

import ac.essex.graphing.charts.discrete.BarChartPlot;
import ac.essex.graphing.charts.discrete.CandleStickPlot;
import ac.essex.graphing.plotting.Graph;
import ac.essex.graphing.plotting.PlotSettings;
import ac.essex.graphing.swing.GraphApplication;


public class StatisticianCollector {
	Statistician current;
	int allCounter=0;
	int allNotEmptyCounter=0;
	private Double ks=0.0;
	private Double sourceTrackNumbersMean=0.0;
	private Double sourceLengthMean=0.0;
	private Double sourceWaypointNumbersMean=0.0;
	private Double sourceTrackNumbersHigh=0.0;
	private Double sourceTrackNumbersLow=0.0;
	private Double sourceLengthHigh=0.0;
	private Double sourceLengthLow=0.0;
	private Double sourceWaypointNumbersHigh=0.0;
	private Double sourceWaypointNumbersLow=0.0;
	private Double sourceConnectionNumbers=0.0;
	private Double mergedWaypointNumbers=0.0;
	private Double mergedConnectionNumbers=0.0;
	private Double mergedTrackNumbers=0.0;
	private Double usedTrackNumbersMean=0.0;
	private Double usedWaypointNumbersMean=0.0;
	private Double mergedLengthMean=0.0;
	private Double usedTrackNumbersHigh=0.0;
	private Double usedTrackNumbersLow=0.0;
	private Double usedWaypointNumbersHigh=0.0;
	private Double usedWaypointNumbersLow=0.0;
	private Double mergedLengthHigh=0.0;
	private Double mergedLengthLow=0.0;
	private HashMap<Integer,Double> mergedWaypointGradeMeans = new HashMap<Integer,Double>();//grade to number
	private HashMap<Integer,Double> neighborGradeMeans = new HashMap<Integer,Double>();//grade to number
	private Double removedConnectionNumbers=0.0;
	private Double removedWaypointsNumbers=0.0;
	private Double removedTracksNumbers=0.0;
	private HashMap<Integer, Integer> mergedWaypointGradeHighs = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> neighborGradeLows = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> neighborGradeHighs = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> mergedWaypointGradeLows = new HashMap<Integer, Integer>();
	private static Object lock = new Object();
	private static GraphApplication ga;
	public static void main(String[] args) {
		StatisticianCollector sc = new StatisticianCollector("input/Berlin/Berlin.dat", "Berlin");
	}
	public StatisticianCollector(String pathOfDatFile, String region) {
		Importer importer = new Importer(pathOfDatFile);
		int coorMax = importer.getCoorMax();
		Main.initialize();
		getFromGridMerge(coorMax,region);
		getFromKMeansMerge(coorMax,region);
		getFromSegmentCloud(coorMax,region);
		getFromMinimalArea(coorMax,region);
		getFromMinimalAreaExtended(coorMax,region);
	}
	private void getFromMinimalAreaExtended(int coorMax, String region) {
		for(Double pointDensity :Main.pointDensityList){
			for(Integer intolerance : Main.intoleranceList){
				for(Double minimalAreaDistance : Main.minimalAreaDistanceList){
					for(Integer k: Main.kList){
						if (k>intolerance){
							for(int x=0; x<coorMax; x++){
								for(int y=0; y<coorMax; y++){
									String currentFilePath = "output/stats/"
											+ "CliqueCloakExtendedMerge"
											+ region
											+ x
											+ "_"
											+ y
											+ "_"
											+ "k"+k
											+ "_"
											+ "pD" +pointDensity
											+".ps";
									try {
										current= new Statistician(currentFilePath);
										addValues();
										allNotEmptyCounter++;
									} catch (FileNotFoundException e) {
									}
									allCounter++;
								}
							}
							calculateAverage(allNotEmptyCounter);
							String writePath="output/stats/"
									+ "CliqueCloakMerge"
									+ region
									+ "_"
									+ "k"+k
									+ "_"
									//+ "pD" +pointDensity
									+".ps";
							plotSourceUsed("KMeansMerge-Methode " + region +" K:" +k +" Punktdichte:"+ 0.5);
							//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
						}
					}
				}
			}
		}
	}
	private void getFromMinimalArea(int coorMax, String region) {
		for(Double pointDensity :Main.pointDensityList){
			MinimalAreaCloud cliqueCloakCloud=null;
			Statistician statistician = new Statistician();
			for(Integer k: Main.kList){
				for(int x=0; x<coorMax; x++){
					for(int y=0; y<coorMax; y++){
						String currentFilePath = "output/stats/"
								+ "CliqueCloakMerge"
								+ region
								+ x
								+ "_"
								+ y
								+ "_"
								+ "k"+k
								+ "_"
								+ "pD" +pointDensity
								+".ps";
						try {
							current= new Statistician(currentFilePath);
							addValues();
							allNotEmptyCounter++;
						} catch (FileNotFoundException e) {
						}
						allCounter++;
					}
				}
				calculateAverage(allNotEmptyCounter);
				String writePath="output/stats/"
						+ "CliqueCloakMerge"
						+ region
						+ "_"
						+ "k"+k
						+ "_"
						//+ "pD" +pointDensity
						+".ps";
				plotSourceUsed("KMeansMerge-Methode " + region +" K:" +k +" Punktdichte:"+ 0.5);
				//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
			}
		}
	}
	private void getFromSegmentCloud(int coorMax, String region) {
		for(Double trackDistance : Main.distanceList){
			for(Double pointDensity :Main.pointDensityList){
				for(Double angelAllowance: Main.angelAllowanceList){
					for(Integer k: Main.kList){
						if(trackDistance<pointDensity && pointDensity>=2){
							for(int x=0; x<coorMax; x++){
								for(int y=0; y<coorMax; y++){
									String currentFilePath = "output/stats/"
											+ "SegmentCloudMerge"
											+ region
											+ x
											+ "_"
											+ y
											+ "_"
											+ "k"+k
											+ "_"
											+ "tD" +trackDistance
											+ "_"
											+ "pD" +pointDensity
											+ "_"
											+ "iD" +true
											+ "_"
											+ "aA" +angelAllowance
											+".ps";
									try {
										current= new Statistician(currentFilePath);
										addValues();
										allNotEmptyCounter++;
									} catch (FileNotFoundException e) {
									}
									allCounter++;
								}
							}
							calculateAverage(allNotEmptyCounter);
							String writePath="output/stats/"
									+ "CliqueCloakMerge"
									+ region
									+ "_"
									+ "k"+k
									+ "_"
									//+ "pD" +pointDensity
									+".ps";
							plotSourceUsed("KMeansMerge-Methode " + region +" K:" +k +" Punktdichte:"+ 0.5);
							//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
						}
					}

				}
			}
		}
		
	}
	private void getFromKMeansMerge(int coorMax, String region) {
		for(Integer k :Main.kList){
			for(Double pointDensity :Main.pointDensityList){
				if(pointDensity>7 || pointDensity==0){
					for(int x=0; x<coorMax; x++){
						for(int y=0; y<coorMax; y++){
							String currentFilePath="output/stats/"
									+ "KMeansMerge"
									+ region
									+ x
									+ "_"
									+ y
									+ "_"
									+ "k"+k
									+ "_"
									+ "pD" +pointDensity
									+".ps";
							try {
								current= new Statistician(currentFilePath);
								addValues();
								allNotEmptyCounter++;
							} catch (FileNotFoundException e) {
							}
							allCounter++;
						}
					}
					calculateAverage(allNotEmptyCounter);
					String writePath="output/stats/"
							+ "CliqueCloakMerge"
							+ region
							+ "_"
							+ "k"+k
							+ "_"
							//+ "pD" +pointDensity
							+".ps";
					plotSourceUsed("KMeansMerge-Methode " + region +" K:" +k +" Punktdichte:"+ 0.5);
					//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
				}
			}	
		}
		
	}
	private void getFromGridMerge(int coorMax, String region) {
		for(Integer k :Main.kList){
			for(Double gridSize :Main.distanceList){
				for(int x=0; x<coorMax; x++){
					for(int y=0; y<coorMax; y++){
						String currentFilePath="output/stats/"
								+ "GridMerge"
								+ region
								+ x
								+ "_"
								+ y
								+ "_"
								+ "k"+k
								+ "_"
								+ "gS" +gridSize
								+ "_"
								+ "mS" + Main.minSpeed
								+".ps";
						try {
							current= new Statistician(currentFilePath);
							addValues();
							allNotEmptyCounter++;
						} catch (FileNotFoundException e) {
						}
						allCounter++;
					}
				}
				calculateAverage(allNotEmptyCounter);
				String writePath="output/stats/"
						+ "CliqueCloakMerge"
						+ region
						+ "_"
						+ "k"+k
						+ "_"
						//+ "pD" +pointDensity
						+".ps";
				plotSourceUsed("MinimalArea-Methode " + region +" K:" +k +" Punktdichte:"+ 0.5);
				//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
			}
		}
		
	}
	private void plotSourceUsed(String title) {
		PlotSettings trackSettings = new PlotSettings();
		trackSettings.setMinX(0);
		trackSettings.setMinY(0);
		trackSettings.setMaxX(3);
		trackSettings.setGridSpacingX(0.5);
		trackSettings.setMaxX(2);
		trackSettings.setMarginLeft(70);
		trackSettings.setGridSpacingY(100);
		trackSettings.setMaxY(sourceTrackNumbersHigh.intValue()+sourceTrackNumbersHigh.intValue()/10);
		String[] trackLabels = new String[]{"urspr. Tracks"
				, "genutzte Tracks"};
		double[] trackLows= new double[]{sourceTrackNumbersLow
				,usedTrackNumbersLow};
		double[] trackHighs= new double[]{sourceTrackNumbersHigh
				,usedTrackNumbersHigh};
		double[] trackMeans= new double[]{sourceTrackNumbersMean
				,usedTrackNumbersMean};
		trackSettings.setTitle(title);
		Graph trackGraph = new Graph(trackSettings);
		trackGraph.functions.add(new BarChartPlot2(trackLabels, trackHighs, trackLows, trackMeans));
		drawIt(trackGraph);
		/*
		PlotSettings lengthSettings = trackSettings;
		trackSettings.setMarginLeft(100);
		trackSettings.setGridSpacingY(100);
		trackSettings.setMaxY(sourceLengthHigh.intValue()/1000+(sourceLengthHigh.intValue()/1000)/10);
		String[] lengthLabels = new String[]{"ursprüngliche Gesamtlänge"
				,"Endlänge"};
		double[] lengthLows= new double[]{sourceLengthLow/1000
				,mergedLengthLow/1000};
		double[] lengthHighs= new double[]{sourceLengthHigh/1000
				,mergedLengthHigh/1000};
		double[] lengthMeans= new double[]{sourceLengthMean/1000
				,mergedLengthMean/1000};
		lengthSettings.setTitle(title);
		Graph lengthGraph = new Graph(lengthSettings);
		lengthGraph.functions.add(new BarChartPlot2(lengthLabels, lengthHighs, lengthLows, lengthMeans));
		drawIt(lengthGraph);
		*/
		/*
		PlotSettings wpSettings = trackSettings;
		trackSettings.setGridSpacingY(5);
		wpSettings.setMaxY(sourceWaypointNumbersHigh.intValue()/1000+(sourceWaypointNumbersHigh.intValue()/1000)/10);

		String[] wpLabels = new String[]{"ursprüngliche WP"
				,"genutzte Wegpunkte"};
		double[] wpLows= new double[]{sourceWaypointNumbersLow/1000
				,usedWaypointNumbersLow/1000
				};
		double[] wpHighs= new double[]{sourceWaypointNumbersHigh/1000
				,usedWaypointNumbersHigh/1000
				};
		double[] wpMeans= new double[]{sourceWaypointNumbersMean/1000
				,usedWaypointNumbersMean/1000
				};
		wpSettings.setTitle(title);
		Graph wpGraph = new Graph(wpSettings);
		wpGraph.functions.add(new BarChartPlot2(wpLabels, wpHighs, wpLows, wpMeans));
		drawIt(wpGraph);
		*/
	}
	private void drawIt(Graph trackGraph) {
		ga = new GraphApplication(trackGraph){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==exit){
					this.setVisible(false);
					synchronized(lock){
						lock.notify();
					}
				}else{
					super.actionPerformed(e);
				}
			}
		};
		ga.setBounds(0, 0, 500, 500);
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void plotGrades(String title) {
		PlotSettings settings = new PlotSettings();
		settings.setMinX(0);
		settings.setMinY(0);
		LinkedList<Integer> grades = new LinkedList<Integer>(mergedWaypointGradeMeans.keySet());
		LinkedList<Double> values = new LinkedList<Double>(mergedWaypointGradeMeans.values());
		String[] labels = new String[grades.size()];
		double[] lows= new double[grades.size()];
		double[] highs= new double[grades.size()];
		double[] means= new double[grades.size()];
		Collections.sort(grades);
		Collections.sort(values);
		for(int i=0;i<grades.size();i++){
			Integer grade = grades.get(i);
			labels[i]=(grade.toString());
			lows[i]=(mergedWaypointGradeLows.get(grade).doubleValue());
			highs[i]=(mergedWaypointGradeHighs.get(grade).doubleValue());
			means[i]=(mergedWaypointGradeMeans.get(grade).doubleValue());
		}
		settings.setMaxX(grades.getLast()+5);
		settings.setMaxY(values.getLast()+5);
		settings.setTitle(title);
		Graph graph = new Graph(settings);
		graph.functions.add(new BarChartPlot(labels, highs, lows, means));
		GraphApplication ga = new GraphApplication(graph);
	}
	private void calculateAverage(int counter) {
		ks/=counter;
		sourceTrackNumbersMean/=counter;
		sourceLengthMean/=counter;
		sourceWaypointNumbersMean/=counter;
		sourceConnectionNumbers/=counter;
		mergedWaypointNumbers/=counter;
		mergedConnectionNumbers/=counter;
		mergedTrackNumbers/=counter;
		usedTrackNumbersMean/=counter;
		usedWaypointNumbersMean/=counter;
		mergedLengthMean/=counter;
		removedConnectionNumbers/=counter;
		removedWaypointsNumbers/=counter;
		removedTracksNumbers/=counter;
		for(Integer grade : mergedWaypointGradeMeans.keySet()){
			if(mergedWaypointGradeMeans.get(grade)!=null){
				Double temp=mergedWaypointGradeMeans.get(grade)/counter;
				mergedWaypointGradeMeans.put(grade,temp);
			}
		}
		for(Integer grade : neighborGradeMeans.keySet()){
			if(neighborGradeMeans.get(grade)!=null){
				Double temp=neighborGradeMeans.get(grade)/counter;
				neighborGradeMeans.put(grade,temp);
			}
		}
	}
	private void addValues() {
		ks+=current.getk();
		sourceTrackNumbersMean+=current.getSourceTrackNumber();
		sourceLengthMean+=current.getSourceLength();
		sourceWaypointNumbersMean+=current.getSourceWaypointNumber();
		sourceConnectionNumbers+=current.getSourceConnectionNumber();
		mergedWaypointNumbers+=current.getMergedWayPointNumber();
		mergedConnectionNumbers+=current.getMergedConnectionNumber();
		mergedTrackNumbers+=current.getMergedTrackNumber();
		usedTrackNumbersMean+=current.getUsedTrackNumbers();
		usedWaypointNumbersMean+=(current.getUsedWaypointNumbers());
		mergedLengthMean+=current.getMergedLength();
		removedConnectionNumbers+=current.getRemovedConnectionNumber();
		removedWaypointsNumbers+=current.getRemovedWaypointsNumber();
		removedTracksNumbers+=current.getRemovedTracksNumber();
		for(Integer grade : current.getMergedWayPointGrade().keySet()){
			if(mergedWaypointGradeMeans.get(grade)==null){
				mergedWaypointGradeMeans.put(grade,current.getMergedWayPointGrade().get(grade).doubleValue());
			}else{
				mergedWaypointGradeMeans.put(grade,current.getMergedWayPointGrade().get(grade).doubleValue()+mergedWaypointGradeMeans.get(grade));
			}
		}
		for(Integer grade : current.getNeighborGrade().keySet()){
			if(neighborGradeMeans.get(grade)==null){
				neighborGradeMeans.put(grade,current.getNeighborGrade().get(grade).doubleValue());
			}else{
				neighborGradeMeans.put(grade,current.getNeighborGrade().get(grade).doubleValue()+neighborGradeMeans.get(grade));
			}
		}
		if(current.getSourceTrackNumber()>sourceTrackNumbersHigh){
			sourceTrackNumbersHigh=(double) current.getSourceTrackNumber();
		}
		if(current.getSourceTrackNumber()<sourceTrackNumbersLow){
			sourceTrackNumbersLow=(double) current.getSourceTrackNumber();
		}
		if(current.getSourceLength()>sourceLengthHigh){
			sourceLengthHigh=current.getSourceLength();
		}
		if(current.getSourceLength()<sourceLengthLow){
			sourceLengthLow=current.getSourceLength();
		}
		if(current.getSourceWaypointNumber()>sourceWaypointNumbersHigh){
			sourceWaypointNumbersHigh=(double) current.getSourceWaypointNumber();
		}
		if(current.getSourceWaypointNumber()<sourceWaypointNumbersLow){
			sourceWaypointNumbersLow=(double) current.getSourceWaypointNumber();
		}
		if(current.getUsedTrackNumbers()>usedTrackNumbersHigh){
			usedTrackNumbersHigh=(double) current.getUsedTrackNumbers();
		}
		if(current.getUsedTrackNumbers()<usedTrackNumbersLow){
			usedTrackNumbersLow=(double) current.getUsedTrackNumbers();
		}
		if(current.getUsedWaypointNumbers()>usedWaypointNumbersHigh){
			usedWaypointNumbersHigh=(double) current.getUsedWaypointNumbers();
		}
		if(current.getUsedWaypointNumbers()<usedWaypointNumbersLow){
			usedWaypointNumbersLow=(double) current.getUsedWaypointNumbers();
		}
		if(current.getMergedLength()>mergedLengthHigh){
			mergedLengthHigh=(double) current.getMergedLength();
		}
		if(current.getMergedLength()<mergedLengthLow){
			mergedLengthLow=(double) current.getMergedLength();
		}
		for(Integer grade : current.getMergedWayPointGrade().keySet()){
			if(mergedWaypointGradeHighs.get(grade)==null){
				mergedWaypointGradeHighs.put(grade,current.getMergedWayPointGrade().get(grade));
			}else if(current.getMergedWayPointGrade().get(grade)>mergedWaypointGradeHighs.get(grade)){
				mergedWaypointGradeHighs.put(grade,current.getMergedWayPointGrade().get(grade));
			}
		}
		for(Integer grade : current.getMergedWayPointGrade().keySet()){
			if(mergedWaypointGradeLows.get(grade)==null){
				mergedWaypointGradeLows.put(grade,current.getMergedWayPointGrade().get(grade));
			}else if(current.getMergedWayPointGrade().get(grade)<mergedWaypointGradeLows.get(grade)){
				mergedWaypointGradeLows.put(grade,current.getMergedWayPointGrade().get(grade));
			}
		}
		for(Integer grade : current.getNeighborGrade().keySet()){
			if(neighborGradeLows.get(grade)==null){
				neighborGradeLows.put(grade,current.getNeighborGrade().get(grade));
			}else if(current.getNeighborGrade().get(grade)<neighborGradeLows.get(grade)){
				neighborGradeLows.put(grade,neighborGradeLows.get(grade));
			}
		}
		for(Integer grade : current.getNeighborGrade().keySet()){
			if(neighborGradeHighs.get(grade)==null){
				neighborGradeHighs.put(grade,current.getNeighborGrade().get(grade));
			}else if(current.getNeighborGrade().get(grade)>neighborGradeHighs.get(grade)){
				neighborGradeHighs.put(grade,neighborGradeHighs.get(grade));
			}
		}
		
	}

}
