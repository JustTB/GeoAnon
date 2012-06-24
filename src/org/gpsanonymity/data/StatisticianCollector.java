package org.gpsanonymity.data;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.gpsanonymity.Main;
import org.gpsanonymity.io.Importer;

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
	private HashMap<Integer, Integer> mergedWaypointGradeHighs;
	private HashMap<Integer, Integer> neighborGradeLows;
	private HashMap<Integer, Integer> neighborGradeHighs;
	private HashMap<Integer, Integer> mergedWaypointGradeLows;
	public StatisticianCollector(String pathOfDatFile,String method,String datFile, String dir, String region) {
		Importer importer = new Importer(pathOfDatFile);
		int coorMax = importer.getCoorMax();
		Main.initialize();
		for(Integer k :Main.kList){
			for(Double pointDensity :Main.distanceList){
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
							
							e.printStackTrace();
						}
						allCounter++;
					}
				}
				calculateAverage(allNotEmptyCounter);
				String writePath="output/stats/"
						+ "KMeansMerge"
						+ region
						+ "_"
						+ "k"+k
						+ "_"
						+ "pD" +pointDensity
						+".ps";
				plotSourceUsed(writePath);
				plotGrades("K-Means-Merge " + region +" K:" +k +" Punktdichte"+ pointDensity);
			}
			
		}

	}
	private void plotSourceUsed(String title) {
		PlotSettings trackSettings = new PlotSettings();
		trackSettings.setMinX(0);
		trackSettings.setMinY(0);
		trackSettings.setMaxX(3);
		trackSettings.setMaxY(sourceTrackNumbersHigh.intValue()+5);
		String[] trackLabels = new String[]{"ursprüngliche Tracks"
				, "genutzte Tracks"};
		double[] trackLows= new double[]{sourceTrackNumbersLow
				,usedTrackNumbersLow};
		double[] trackHighs= new double[]{sourceTrackNumbersHigh
				,usedTrackNumbersHigh};
		double[] trackMeans= new double[]{sourceTrackNumbersMean
				,usedTrackNumbersMean};
		trackSettings.setTitle(title);
		Graph trackGraph = new Graph(trackSettings);
		trackGraph.functions.add(new BarChartPlot(trackLabels, trackHighs, trackLows, trackMeans));
		new GraphApplication(trackGraph);
		
		PlotSettings lengthSettings = new PlotSettings();
		lengthSettings.setMinX(0);
		lengthSettings.setMinY(0);
		lengthSettings.setMaxX(3);
		lengthSettings.setMaxY(mergedLengthHigh.intValue()+5);
		String[] lengthLabels = new String[]{"ursprüngliche Gesamtlänge"
				,"Endlänge"};
		double[] lengthLows= new double[]{sourceLengthLow
				,mergedLengthLow};
		double[] lengthHighs= new double[]{sourceLengthHigh
				,mergedLengthHigh};
		double[] lengthMeans= new double[]{sourceLengthMean
				,mergedLengthMean};
		lengthSettings.setTitle(title);
		Graph lengthGraph = new Graph(lengthSettings);
		lengthGraph.functions.add(new BarChartPlot(lengthLabels, lengthHighs, lengthLows, lengthMeans));
		new GraphApplication(lengthGraph);
		
		PlotSettings wpSettings = new PlotSettings();
		wpSettings.setMinX(0);
		wpSettings.setMinY(0);
		wpSettings.setMaxX(3);
		wpSettings.setMaxY(sourceWaypointNumbersHigh.intValue()+5);

		String[] wpLabels = new String[]{"ursprüngliche WP"
				,"genutzte Wegpunkte"};
		double[] wpLows= new double[]{sourceWaypointNumbersLow
				,usedWaypointNumbersLow
				};
		double[] wpHighs= new double[]{sourceWaypointNumbersHigh
				,usedWaypointNumbersHigh
				};
		double[] wpMeans= new double[]{sourceWaypointNumbersMean
				,usedWaypointNumbersMean
				};
		wpSettings.setTitle(title);
		Graph wpGraph = new Graph(wpSettings);
		wpGraph.functions.add(new BarChartPlot(wpLabels, wpHighs, wpLows, wpMeans));
		new GraphApplication(wpGraph);
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
		new GraphApplication(graph);
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
		usedWaypointNumbersMean+=current.getUsedWaypointNumbers();
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
		for(Integer grade : current.getMergedWayPointGrade().keySet()){
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
		for(Integer grade : current.getMergedWayPointGrade().keySet()){
			if(neighborGradeLows.get(grade)==null){
				neighborGradeLows.put(grade,current.getNeighborGrade().get(grade));
			}else if(current.getMergedWayPointGrade().get(grade)<neighborGradeLows.get(grade)){
				neighborGradeLows.put(grade,neighborGradeLows.get(grade));
			}
		}
		for(Integer grade : current.getMergedWayPointGrade().keySet()){
			if(neighborGradeHighs.get(grade)==null){
				neighborGradeHighs.put(grade,current.getNeighborGrade().get(grade));
			}else if(current.getMergedWayPointGrade().get(grade)>neighborGradeHighs.get(grade)){
				neighborGradeHighs.put(grade,neighborGradeHighs.get(grade));
			}
		}
		
	}

}
