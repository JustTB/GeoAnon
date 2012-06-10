package org.gpsanonymity.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.panayotis.gnuplot.GNUPlot;

public class Statistician {
	private LinkedList<Integer> sourceTrackNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> mergedTrackNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> sourceWaypointNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> mergedWayPointNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> sourceConnectionNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> mergedConnectionNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> removedConnectionNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> removedWaypointsNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> removedTracksNumbers = new LinkedList<Integer>();
	private LinkedList<HashMap<Integer,Integer>> mergedWaypointGrade = new LinkedList<HashMap<Integer,Integer>>();
	private LinkedList<HashMap<Integer,Integer>> neighborGrade = new LinkedList<HashMap<Integer,Integer>>();
	private int currentIndex=-1;
	private int maxIndex=-1;
	
	public Statistician() {
		newDataSet();
	}
	public void incrementMergedWayPointGrade(int grade){
		HashMap<Integer, Integer> map = mergedWaypointGrade.get(currentIndex);
		if(map.containsKey(grade)){
			map.put(grade,map.get(grade)+1);
		}else{
			map.put(grade, 1);
		}
	}
	public void incrementNeighborGrade(int grade){
		HashMap<Integer, Integer> map = neighborGrade.get(currentIndex);
		if(map.containsKey(grade)){
			map.put(grade,map.get(grade)+1);
		}else{
			map.put(grade, 1);
		}
	}
	public int getSourceTrackNumber() {
		return sourceTrackNumbers.get(currentIndex);
	}
	public void setSourceTrackNumber(int sourceTrackNumber) {
		this.sourceTrackNumbers.set(currentIndex, sourceTrackNumber);
	}
	public int getMergedConnectionNumber() {
		return mergedConnectionNumbers.get(currentIndex);
	}
	public void setMergedConnectionNumber(int sourceTrackNumber) {
		this.mergedConnectionNumbers.set(currentIndex, sourceTrackNumber);
	}
	public int getRemovedConnectionNumber() {
		return removedConnectionNumbers.get(currentIndex);
	}
	public void setRemovedConnectionNumber(int sourceTrackNumber) {
		this.removedConnectionNumbers.set(currentIndex, sourceTrackNumber);
	}
	public int getSourceConnectionNumber() {
		return sourceConnectionNumbers.get(currentIndex);
	}
	public void setSourceConnectionNumber(int sourceTrackNumber) {
		this.sourceConnectionNumbers.set(currentIndex, sourceTrackNumber);
	}
	public int getMergedTrackNumber() {
		return mergedTrackNumbers.get(currentIndex);
	}
	
	public void setMergedTrackNumber(int mergedSourceTrackNumber) {
		this.mergedTrackNumbers.set(currentIndex, mergedSourceTrackNumber);
	}
	public int getSourceWaypointNumber() {
		return sourceWaypointNumbers.get(currentIndex);
	}
	public void setSourceWaypointNumber(int sourceWaypointNumber) {
		this.sourceWaypointNumbers.set(currentIndex, sourceWaypointNumber);
	}
	public int getMergedWayPointNumber() {
		return mergedWayPointNumbers.get(currentIndex);
	}
	public void setMergedWayPointNumber(int mergedWayPointNumber) {
		this.mergedWayPointNumbers.set(currentIndex, mergedWayPointNumber);
	}
	public int getRemovedWaypointsNumber() {
		return removedWaypointsNumbers.get(currentIndex);
	}
	public void setRemovedWaypointsNumber(int removedWaypoints) {
		this.removedWaypointsNumbers.set(currentIndex, removedWaypoints);
	}
	public int getRemovedTracksNumber() {
		return removedTracksNumbers.get(currentIndex);
	}
	public void setRemovedTracksNumber(int removedTracks) {
		this.removedTracksNumbers.set(currentIndex, removedTracks);
	}
	public void newDataSet(){
		maxIndex++;
		currentIndex=maxIndex;
		sourceTrackNumbers.add(null);
		sourceWaypointNumbers.add(null);
		sourceConnectionNumbers.add(null);
		mergedTrackNumbers.add(null);
		mergedWayPointNumbers.add(null);
		mergedWaypointGrade.add(new HashMap<Integer, Integer>());
		mergedConnectionNumbers.add(null);
		neighborGrade.add(new HashMap<Integer, Integer>());
		removedTracksNumbers.add(null);
		removedWaypointsNumbers.add(null);
		removedConnectionNumbers.add(null);
	}
	public boolean setPreviusDataSet(){
		if (currentIndex<1)
		 return false;
		else{
			currentIndex--;
			return true;
		}
	}
	public boolean setNextDataSet(){
		if (currentIndex>=maxIndex)
			return false;
		else{
			currentIndex++;
			return true;
		}
	}
	public boolean setDataSet(int index){
		if(index<0 || index>maxIndex){
			return false;
		}else{
			currentIndex=index;
			return true;
		}
	}
	public void writeStats(String fileName){
		
	}
}
