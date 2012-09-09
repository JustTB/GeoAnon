package org.gpsanonymity.stats;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.Main;
import org.gpsanonymity.data.Statistician;
import org.gpsanonymity.data.methods.MinimalAreaCloud;
import org.gpsanonymity.io.Importer;

import ac.essex.graphing.charts.discrete.BarChartPlot;
import ac.essex.graphing.plotting.Graph;
import ac.essex.graphing.plotting.PlotSettings;
import ac.essex.graphing.plotting.Plotter;
import ac.essex.graphing.swing.GraphApplication;


public class StatisticianCollector {
	Statistician current;
	int allCounter=0;
	int allNotEmptyCounter=0;
	private Integer k=0;
	private Double sourceTrackNumbersMean=0.0;
	private Double sourceLengthMean=0.0;
	private Double sourceWaypointNumbersMean=0.0;
	private Double sourceTrackNumbersHigh=0.0;
	private Double sourceTrackNumbersLow=Double.MAX_VALUE;
	private Double sourceLengthHigh=0.0;
	private Double sourceLengthLow=Double.MAX_VALUE;
	private Double sourceWaypointNumbersHigh=0.0;
	private Double sourceWaypointNumbersLow=Double.MAX_VALUE;
	private Double sourceConnectionNumbers=0.0;
	private Double mergedWaypointNumbers=0.0;
	private Double mergedConnectionNumbers=0.0;
	private Double mergedTrackNumbers=0.0;
	private Double usedTrackNumbersMean=0.0;
	private Double usedWaypointNumbersMean=0.0;
	private Double mergedLengthMean=0.0;
	private Double usedTrackNumbersHigh=0.0;
	private Double usedTrackNumbersLow=Double.MAX_VALUE;
	private Double usedWaypointNumbersHigh=0.0;
	private Double usedWaypointNumbersLow=Double.MAX_VALUE;
	private Double mergedLengthHigh=0.0;
	private Double mergedLengthLow=Double.MAX_VALUE;
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
	
	
	private List<String> trackLabels=null;
	private List<Double> trackLows=null;;
	private List<Double> trackHighs=null;
	private List<Double> trackMeans=null;
	private List<String> lengthLabels=null;
	private List<Double> lengthLows=null;
	private List<Double> lengthHighs=null;
	private List<Double> lengthMeans=null;
	private List<String> waypointLabels=null;
	private List<Double> waypointLows=null;
	private List<Double> waypointHighs=null;
	private List<Double> waypointMeans=null;
	public static void main(String[] args) {
		Main.initialize();
		for(String path:Main.inputFileList){
				StatisticianCollector sc = new StatisticianCollector(path, path.substring(path.lastIndexOf("/")+1).replace(".dat", ""));
		}
	}
	public StatisticianCollector(String pathOfDatFile, String region) {
		Importer importer = new Importer(pathOfDatFile);
		int coorMax = importer.getCoorMax();
		//getFromGridMerge(coorMax,region);
		//getFromKMeansMerge(coorMax,region);
		//getFromSegmentCloud(coorMax,region);
		//getFromMinimalArea(coorMax,region);
		getFromMinimalAreaExtended(coorMax,region);
	}
	private Integer intolerance;
	private LinePlot waypointPlot = new LinePlot(new String[1],new double[1],new double[1],new double[1]);
	private void getFromMinimalAreaExtended(int coorMax, String region) {
		for(Integer k: Main.kList){
			double sourcetemp=0;
			for(Integer intolerance : Main.intoleranceList){	
				this.intolerance=intolerance;
				//for(Double pointDensity :Main.pointDensityList){
					Double pointDensity = 0.0;
					//for(Double minimalAreaDistance : Main.minimalAreaDistanceList){
						Double minimalAreaDistance=0.0;
						clearValues();
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
											//+ "pD0.0"
											+ "_"
											+ "mAD" +minimalAreaDistance
											+ "_"
											+ "i" + intolerance
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
							
							addPlotSourceUsed();
						
							//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
							sourcetemp=sourceWaypointNumbersHigh;
						}
						if(usedWaypointNumbersHigh==0){
							usedWaypointNumbersHigh=null;
						}
						if (sourceWaypointNumbersHigh==0){
							sourceWaypointNumbersHigh=sourcetemp;
						}
						addWayPointPlots(k.toString(), intolerance.doubleValue());
					//}
				}
			//}
			/*
			this.k=k;
			plotGrades("MinimalAreaExtend-Methode " + region +" K:" +k +" Intoleranz:"+0);
			plotNeighborGrades("MinimalAreaExtend-Methode " + region +" K:" + k +" Intoleranz:"+0);
			clearValues();
			clearWPNG();
			*/
		}
		if (!region.contains("McPomm")){
			plotIt("MinimalAreaExtend-Methode " + region);
		}else{
			plotIt("MinimalAreaExtend-Methode Mecklenburg-Vorpommern");
		}
	}
	private void getFromMinimalArea(int coorMax, String region) {
		for(Integer k: Main.kList){
			for(Double pointDensity :Main.pointDensityList){
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
				addPlotSourceUsed();
				//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
			}
			this.k=k;
			plotGrades("MinimalArea-Methode " + region +" K:" +k);
			plotNeighborGrades("MinimalArea-Methode " + region +" K:" +k);
			clearValues();
		}
		
		if (!region.contains("McPomm")){
			plotIt("MinimalArea-Methode " + region);
		}else{
			plotIt("MinimalArea-Methode Mecklenburg-Vorpommern");
		}
	}
	private void getFromSegmentCloud(int coorMax, String region) {
		for(Double trackDistance : Main.distanceList){
			if(trackDistance==1 
					|| trackDistance==2
					|| trackDistance==4
					|| trackDistance==8
					|| trackDistance==16){
			for(Integer k: Main.kList){
				for(Double pointDensity :Main.pointDensityList){		
					//for(Double angelAllowance: Main.angelAllowanceList){
					clearValues();
						if(trackDistance<pointDensity && pointDensity>=2 || pointDensity==0){
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
											+ "aA" +0.0
											+".ps";
									try {
										current= new Statistician(currentFilePath);
										addValues();
										allNotEmptyCounter++;
									} catch (FileNotFoundException e) {
										System.out.println("File not found " + currentFilePath);
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
							addPlotSourceUsed();
							addWayPointPlots(k.toString(), pointDensity);
							//plotGrades("MinimalArea-Methode " + region +" K:" +k +" Punktdichte"+ pointDensity);
						}
					//}

				}
				/*
				plotGrades("SegmentMerge-Methode " + region +" Distanz: " +trackDistance+"m ");
				plotNeighborGrades("SegmentMerge-Methode " + region +" Distanz: " +trackDistance+"m ");
				clearValues();
				clearWPNG();
				*/
			}
			
			if (!region.contains("McPomm")){
				plotIt("SegmentMerge-Methode Distanz: "+trackDistance+"m  "+region);
			}else{
				plotIt("SegmentMerge-Methode Distanz: "+trackDistance+"m  "+"Mecklenburg-Vorpommern");
			}
			
			clearPlots();
			}
		}
	}
	private void clearPlots() {
		trackLabels=null;
		trackLows=null;;
		trackHighs=null;
		trackMeans=null;
		lengthLabels=null;
		lengthLows=null;
		lengthHighs=null;
		lengthMeans=null;
		waypointLabels=null;
		waypointLows=null;
		waypointHighs=null;
		waypointMeans=null;
		waypointPlot=new LinePlot(new String[1],new double[1],new double[1],new double[1]);
		
	}
	private void getFromKMeansMerge(int coorMax, String region) {
		for(Integer k :Main.kList){
			
			for(Double pointDensity :Main.pointDensityList){
					clearValues();
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
								System.out.println("File not Found:" +currentFilePath);
							}
							allCounter++;
						}
					}
					if (allNotEmptyCounter>0){
						calculateAverage(allNotEmptyCounter);
						String writePath="output/stats/"
								+ "CliqueCloakMerge"
								+ region
								+ "_"
								+ "k"+k
								+ "_"
								//+ "pD" +pointDensity
								+".ps";
						addPlotSourceUsed();
						addWayPointPlots(k.toString(), pointDensity);
					}
			}
			/*
			plotGrades("KMeans-Methode " + region +" K:" +k);
			plotNeighborGrades("KMeans-Methode " + region +" K:" +k);
			clearWPNG();
			*/
			
		}
		
		if (!region.contains("McPomm")){
			plotIt("KMeans-Methode " + region);
		}else{
			plotIt("KMeans-Methode Mecklenburg-Vorpommern");
		}
		
	}
	private void getFromGridMerge(int coorMax, String region) {
		for(Integer k :Main.kList){
			for(Double gridSize :Main.distanceList){
				if(gridSize>2){
					clearValues();
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
					if (allNotEmptyCounter>0){
						calculateAverage(allNotEmptyCounter);
						String writePath="output/stats/"
								+ "CliqueCloakMerge"
								+ region
								+ "_"
								+ "k"+k
								+ "_"
								//+ "pD" +pointDensity
								+".ps";
						addPlotSourceUsed();
						addWayPointPlots(k.toString(), gridSize);
						//
					}
				}
			}
			/*
			this.k=k;
			plotGrades("GridMerge-Methode " + region +" K:" +k);
			plotNeighborGrades("GridMerge-Methode " + region +" K:" +k);
			clearValues();
			*/
		}
		if (!region.contains("McPomm")){
			plotIt("GridMerge-Methode " + region);
		}else{
			plotIt("GridMerge-Methode Mecklenburg-Vorpommern");
		}
		
	}
	private void clearWPNG(){
		mergedWaypointGradeMeans = new HashMap<Integer,Double>();//grade to number
		neighborGradeMeans = new HashMap<Integer,Double>();//grade to number
		mergedWaypointGradeHighs = new HashMap<Integer, Integer>();
		neighborGradeLows = new HashMap<Integer, Integer>();
		neighborGradeHighs = new HashMap<Integer, Integer>();
		mergedWaypointGradeLows = new HashMap<Integer, Integer>();
	}
	private void clearValues() {
		allCounter=0;
		allNotEmptyCounter=0;
		k=0;
		sourceTrackNumbersMean=0.0;
		sourceLengthMean=0.0;
		sourceWaypointNumbersMean=0.0;
		sourceTrackNumbersHigh=0.0;
		sourceTrackNumbersLow=Double.MAX_VALUE;
		sourceLengthHigh=0.0;
		sourceLengthLow=Double.MAX_VALUE;
		sourceWaypointNumbersHigh=0.0;
		sourceWaypointNumbersLow=Double.MAX_VALUE;
		sourceConnectionNumbers=0.0;
		mergedWaypointNumbers=0.0;
		mergedConnectionNumbers=0.0;
		mergedTrackNumbers=0.0;
		usedTrackNumbersMean=0.0;
		usedWaypointNumbersMean=0.0;
		mergedLengthMean=0.0;
		usedTrackNumbersHigh=0.0;
		usedTrackNumbersLow=Double.MAX_VALUE;
		usedWaypointNumbersHigh=0.0;
		usedWaypointNumbersLow=Double.MAX_VALUE;
		mergedLengthHigh=0.0;
		mergedLengthLow=Double.MAX_VALUE;
		removedConnectionNumbers=0.0;
		removedWaypointsNumbers=0.0;
		removedTracksNumbers=0.0;

		
	}
	private void plotIt(String title){
		PlotSettings trackSettings = new PlotSettings();
		trackSettings.setMinX(0);
		trackSettings.setMinY(0);
		trackSettings.setGridSpacingX(0.5);
		trackSettings.setMaxX(trackLabels.size());
		trackSettings.setMarginLeft(70);
		trackSettings.setGridSpacingY(getGridSpacing(Collections.max(trackHighs).intValue()));
		trackSettings.setMaxY(Collections.max(trackHighs).intValue()+Collections.max(trackHighs).intValue()/10);
		trackSettings.setTitle(title +" - genutzte Tracks");
		Graph trackGraph = new Graph(trackSettings);
		String[] trackLabelsArray = new String[trackLabels.size()];
		double[] trackHighsArray = new double[trackLabels.size()];
		double[] trackLowsArray = new double[trackLabels.size()];
		double[] trackMeansArray = new double[trackLabels.size()];
		
		for (int i = 0; i < trackMeansArray.length; i++) {
			trackLabelsArray[i]=trackLabels.get(i);
			trackLowsArray[i]=trackLows.get(i);
			trackHighsArray[i]=trackHighs.get(i);
			trackMeansArray[i]=trackMeans.get(i);
			
		}
		trackGraph.functions.add(new BarChartPlot2(trackLabelsArray, trackHighsArray, trackLowsArray, trackLowsArray));
		drawIt(trackGraph);
		
		
		//Length
		PlotSettings lengthSettings = new PlotSettings();
		lengthSettings.setMinX(0);
		lengthSettings.setMinY(0);
		lengthSettings.setGridSpacingX(0.5);
		lengthSettings.setMaxX(lengthLabels.size());
		lengthSettings.setMarginLeft(70);
		lengthSettings.setGridSpacingY(getGridSpacing(Collections.max(lengthHighs).intValue()));
		lengthSettings.setMaxY(Collections.max(lengthHighs).intValue()+Collections.max(lengthHighs).intValue()/10);
		lengthSettings.setTitle(title +" - vorhandene Länge/k");
		Graph lengthGraph = new Graph(lengthSettings);
		String[] lengthLabelsArray = new String[lengthLabels.size()];
		double[] lengthHighsArray = new double[lengthLabels.size()];
		double[] lengthLowsArray = new double[lengthLabels.size()];
		double[] lengthMeansArray = new double[lengthLabels.size()];
		
		for (int i = 0; i < lengthMeansArray.length; i++) {
			lengthLabelsArray[i]=lengthLabels.get(i);
			lengthLowsArray[i]=lengthLows.get(i);
			lengthHighsArray[i]=lengthHighs.get(i);
			lengthMeansArray[i]=lengthMeans.get(i);
			
		}
		lengthGraph.functions.add(new BarChartPlot2(lengthLabelsArray, lengthHighsArray, lengthLowsArray, lengthLowsArray));
		drawIt(lengthGraph);
		
		//Waypoints
		PlotSettings waypointSettings = new PlotSettings();
		waypointSettings.setMinX(0);
		waypointSettings.setMinY(0);
		waypointSettings.setGridSpacingX(1);
		//waypointSettings.setMaxX(waypointLabels.size());
		waypointSettings.setMaxX(waypointPlot.maxX);
		waypointSettings.setMarginLeft(80);
		//waypointSettings.setGridSpacingY(getGridSpacing((Collections.max(waypointHighs).intValue())));
		waypointSettings.setGridSpacingY(getGridSpacing(waypointPlot.maxY));
		//waypointSettings.setMaxY(Collections.max(waypointHighs)+Collections.max(waypointHighs)/10);
		waypointSettings.setMaxY(waypointPlot.maxY+waypointPlot.maxY/10);
		waypointSettings.setTitle(title +" - genutzte Wegpunkte ");
		Graph waypointGraph = new Graph(waypointSettings);
		/*
		String[] waypointLabelsArray = new String[waypointLabels.size()];
		double[] waypointHighsArray = new double[waypointLabels.size()];
		double[] waypointLowsArray = new double[waypointLabels.size()];
		double[] waypointMeansArray = new double[waypointLabels.size()];

		for (int i = 0; i < waypointMeansArray.length; i++) {
			waypointLabelsArray[i]=waypointLabels.get(i);
			waypointLowsArray[i]=waypointLows.get(i);
			waypointHighsArray[i]=waypointHighs.get(i);
			waypointMeansArray[i]=waypointMeans.get(i);

		}
		*/
		waypointGraph.functions.add(waypointPlot );
		drawIt(waypointGraph);
		
	}
	private int getGridSpacing(int intValue) {
		intValue=intValue/10;
		if (intValue/1000000.0>0.5){
			return 1000000;
		}else if (intValue/500000.0>0.5){
				return 500000;
		}else if (intValue/100000.0>0.5){
			return 100000;
		}else if (intValue/50000.0>0.5){
			return 50000;
		}else if (intValue/10000.0>0.5){
			return 10000;
		}else if (intValue/5000.0>0.5){
			return 5000;
		}else if(intValue/1000.0>0.5){
			return 1000;
		}else if (intValue/500.0>0.5){
			return 500;
		}else if(intValue/100.0>0.5){
			return 100;
		}else if(intValue/50.0>0.5){
			return 50;
		}else if(intValue/10.0>0.5){
			return 10;
		}else{
			return 1;
		}
	}

	private void addWayPointPlots(String label, Double key){
		waypointPlot.addHighLowTo(label, key, usedWaypointNumbersHigh, sourceWaypointNumbersHigh);
		/*
		//Waypoints
		if(waypointLabels==null){
			waypointLabels = new LinkedList<String>();
			waypointLows = new LinkedList<Double>();
			waypointHighs= new LinkedList<Double>();
			waypointMeans= new LinkedList<Double>();
		}
		
		//if(waypointLabels.size()==0 || !k.toString().equals(waypointLabels.get(waypointLabels.size()-1))){
			if(usedWaypointNumbersHigh!=0){
					if((waypointLabels.size())%1==0){
						waypointLabels.add(k.toString());
					}else{
						waypointLabels.add("");
					}
					waypointLows.add(usedWaypointNumbersLow);
					waypointHighs.add(sourceWaypointNumbersHigh);
					waypointMeans.add(usedWaypointNumbersHigh);
			}
		*/
	}
	private void addPlotSourceUsed() {
		if(k==0){
			return;
		}
		if(trackLabels==null){
			trackLabels = new LinkedList<String>();
			trackLabels.add(Integer.toString(1));
			trackLows = new LinkedList<Double>();
			trackLows.add(sourceTrackNumbersLow);
			trackHighs= new LinkedList<Double>();
			trackHighs.add(sourceTrackNumbersHigh);
			trackMeans= new LinkedList<Double>();
			trackMeans.add(sourceTrackNumbersMean);
		}
		if(!k.toString().equals(trackLabels.get(trackLabels.size()-1))){ // without same k
			trackLabels.add(k.toString());
			trackLows.add(usedTrackNumbersLow);
			trackHighs.add(usedTrackNumbersHigh);
			trackMeans.add(usedTrackNumbersMean);
		}else{
			if (trackLows.get(trackLows.size()-1)>usedTrackNumbersLow){
				trackLows.set(trackLows.size()-1,usedTrackNumbersLow);
			}
			if (trackHighs.get(trackHighs.size()-1)<usedTrackNumbersHigh){
				trackHighs.set(trackHighs.size()-1,usedTrackNumbersHigh);
			}
		}
		//LENGTH
		if(lengthLabels==null){
			lengthLabels = new LinkedList<String>();
			lengthLabels.add(Integer.toString(1));
			lengthLows = new LinkedList<Double>();
			lengthLows.add(sourceLengthLow/1000);
			lengthHighs= new LinkedList<Double>();
			lengthHighs.add(sourceLengthHigh/1000);
			lengthMeans= new LinkedList<Double>();
			lengthMeans.add(sourceLengthMean/1000);
		}
		if(!k.toString().equals(lengthLabels.get(lengthLabels.size()-1))){ // with same k
			lengthLabels.add(k.toString());
			lengthLows.add(mergedLengthLow/1000);
			lengthHighs.add(mergedLengthHigh/1000);
			lengthMeans.add(mergedLengthMean/1000);
		}else{
			if (lengthLows.get(lengthLows.size()-1)>mergedLengthLow/1000){
				lengthLows.set(lengthLows.size()-1,mergedLengthLow/1000);
			}
			if (lengthHighs.get(lengthHighs.size()-1)<mergedLengthHigh/1000){
				lengthHighs.set(lengthHighs.size()-1,mergedLengthHigh/1000);
			}
		}
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
		ga.setBounds(0, 0, 600, 500);
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void plotNeighborGrades(String title) {
		PlotSettings settings = new PlotSettings();
		settings.setMinX(0);
		settings.setMinY(0);
		LinkedList<Integer> grades = new LinkedList<Integer>(neighborGradeMeans.keySet());
		for(Integer grade : grades){
			if(grade<k){
				neighborGradeHighs.remove(grade);
				neighborGradeLows.remove(grade);
				neighborGradeMeans.remove(grade);
			}
		}
		if(neighborGradeHighs.size()>0){
			grades = new LinkedList<Integer>(neighborGradeMeans.keySet());
			LinkedList<Double> values = new LinkedList<Double>(neighborGradeMeans.values());
			String[] labels = new String[grades.size()];
			double[] lows= new double[grades.size()];
			double[] highs= new double[grades.size()];
			double[] means= new double[grades.size()];
			Collections.sort(grades);
			Collections.sort(values);
			for(int i=0;i<grades.size();i++){
				Integer grade = grades.get(i);
				labels[i]=(grade.toString());
				if(grade>=k){
					lows[i]=(neighborGradeLows.get(grade).doubleValue());
					highs[i]=(neighborGradeHighs.get(grade).doubleValue());
					means[i]=(neighborGradeMeans.get(grade).doubleValue());
				}else{
					lows[i]=0.0;
					highs[i]=0.0;
					means[i]=0.0;
				}
			}
			settings.setGridSpacingY(getGridSpacing(Collections.max(neighborGradeHighs.values()).intValue()));
			settings.setMaxX(grades.size());
			settings.setMaxY(Collections.max(neighborGradeHighs.values())+Collections.max(neighborGradeHighs.values())/20);
			settings.setMarginLeft(80);
			settings.setTitle(title+ " Verbindungsgrade");
			Graph graph = new Graph(settings);
			//graph.functions.add(new BarChartPlot(labels, highs, lows, means));
			graph.functions.add(new BarChartPlot2(labels, highs, means, lows));
			drawIt(graph);
		}
	}
	private void plotGrades(String title) {
		if(k==0){
			k=1;
		}
		PlotSettings settings = new PlotSettings();
		settings.setMinX(0);
		settings.setMinY(0);
		LinkedList<Integer> grades = new LinkedList<Integer>(mergedWaypointGradeMeans.keySet());
		for(Integer grade : grades){
			if(grade<k){
				mergedWaypointGradeHighs.remove(grade);
				mergedWaypointGradeLows.remove(grade);
				mergedWaypointGradeMeans.remove(grade);
			}
		}
		if(mergedWaypointGradeHighs.size()>0){
			grades = new LinkedList<Integer>(mergedWaypointGradeMeans.keySet());
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
				if(grade>=k){
					lows[i]=(mergedWaypointGradeLows.get(grade).doubleValue());
					highs[i]=(mergedWaypointGradeHighs.get(grade).doubleValue());
					means[i]=(mergedWaypointGradeMeans.get(grade).doubleValue());
				}else{
					lows[i]=0.0;
					highs[i]=0.0;
					means[i]=0.0;
				}
			}
			settings.setGridSpacingY(getGridSpacing(Collections.max(mergedWaypointGradeHighs.values()).intValue()));
			settings.setMaxX(grades.size());
			settings.setMaxY(Collections.max(mergedWaypointGradeHighs.values())+Collections.max(mergedWaypointGradeHighs.values())/20);
			settings.setMarginLeft(80);
			settings.setTitle(title + " Punktgrade");
			Graph graph = new Graph(settings);
			//graph.functions.add(new BarChartPlot(labels, highs, lows, means));
			graph.functions.add(new BarChartPlot2(labels, highs, means,lows));
			drawIt(graph);
		}
	}
	private void calculateAverage(int counter) {
		//k/=counter;
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
		if (current.getk()!=0){
			k=current.getk();
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
					neighborGradeLows.put(grade,current.getNeighborGrade().get(grade));
				}
			}
			for(Integer grade : current.getNeighborGrade().keySet()){
				if(neighborGradeHighs.get(grade)==null){
					neighborGradeHighs.put(grade,current.getNeighborGrade().get(grade));
				}else if(current.getNeighborGrade().get(grade)>neighborGradeHighs.get(grade)){
					neighborGradeHighs.put(grade,current.getNeighborGrade().get(grade));
				}
			}
		}
		
	}

}
