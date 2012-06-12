package org.gpsanonymity.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;

public class Statistician implements Serializable{
	private static final long serialVersionUID = 1L;
	private transient String filePath="";
	private LinkedList<Integer> ks = new LinkedList<Integer>();
	private LinkedList<Integer> sourceTrackNumbers = new LinkedList<Integer>();
	private LinkedList<Double>  sourceLength = new LinkedList<Double>();
	private LinkedList<Integer> sourceWaypointNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> sourceConnectionNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> mergedWayPointNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> mergedConnectionNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> mergedTrackNumbers = new LinkedList<Integer>();
	private LinkedList<Double>  mergedLength = new LinkedList<Double>();
	private LinkedList<HashMap<Integer,Integer>> mergedWaypointGrade = new LinkedList<HashMap<Integer,Integer>>();
	private LinkedList<HashMap<Integer,Integer>> neighborGrade = new LinkedList<HashMap<Integer,Integer>>();
	private int currentIndex=-1;
	private int maxIndex=-1;
	private LinkedList<Integer> removedConnectionNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> removedWaypointsNumbers = new LinkedList<Integer>();
	private LinkedList<Integer> removedTracksNumbers = new LinkedList<Integer>();
	
	public Statistician() {
		newDataSet();
	}
	public Statistician(String path) {
		filePath=path;
		try {
			ObjectInputStream oos = new ObjectInputStream(new FileInputStream(new File(path)));
			Statistician statistician =(Statistician)oos.readObject();
			Statistician(statistician);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}
	public void Statistician(Statistician stati) {
		ks = stati.ks;
		sourceTrackNumbers = stati.sourceTrackNumbers;
		sourceLength = stati.sourceLength;
		sourceWaypointNumbers = stati.sourceConnectionNumbers;
		sourceConnectionNumbers = stati.sourceConnectionNumbers;
		mergedWayPointNumbers = stati.mergedWayPointNumbers;
		mergedConnectionNumbers = stati.mergedConnectionNumbers;
		mergedTrackNumbers = stati.mergedTrackNumbers;
		mergedLength = stati.mergedLength;
		mergedWaypointGrade = stati.mergedWaypointGrade;
		neighborGrade = stati.neighborGrade;
		currentIndex=stati.currentIndex;
		maxIndex=stati.maxIndex;
		removedConnectionNumbers = stati.removedConnectionNumbers;
		removedWaypointsNumbers = stati.removedWaypointsNumbers;
		removedTracksNumbers = stati.removedTracksNumbers;
		
	}
	public void setFromMergedTracks(List<GpxTrack> tracks) {
		setMergedTrackNumber(tracks.size());
		int wpCount=0;
		int connectionCount=0;
		double length=0;
		for (GpxTrack gpxTrack : tracks) {
			length+=gpxTrack.length();
			for (GpxTrackSegment seg : gpxTrack.getSegments()) {
				wpCount+=seg.getWayPoints().size();
				connectionCount+=seg.getWayPoints().size();
			}
			connectionCount--;
		}
		setMergedWayPointNumber(wpCount);
		setMergedConnectionNumber(connectionCount);
		setMergedLength(length);
		
	}
	public void setFromMergedWayPoints(Collection<MergedWayPoint> mwps) {
		setMergedWayPointNumber(mwps.size());
		for (MergedWayPoint mwp : mwps) {
			incrementMergedWayPointGrade(mwp.getTrackGrade());
			for(MergedWayPoint neighbor: mwp.getNeighbors()){
				incrementNeighborGrade(mwp.getNeighborGrade(neighbor));
			}
		}
	}
	public void setFromSourceTracks(int k,Collection<GpxTrack> sourceTracks){
		setk(k);
		setSourceTrackNumber(sourceTracks.size());
		int wpCount=0;
		int connectionCount=0;
		double length=0;
		for (GpxTrack gpxTrack : sourceTracks) {
			length+=gpxTrack.length();
			for (GpxTrackSegment seg : gpxTrack.getSegments()) {
				wpCount+=seg.getWayPoints().size();
				connectionCount+=seg.getWayPoints().size();
			}
			connectionCount--;
		}
		setSourceWaypointNumber(wpCount);
		setSourceConnectionNumber(connectionCount);
		setSourceLength(length);
		
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
	public int getk() {
		return ks.get(currentIndex);
	}
	public void setk(int k) {
		this.ks.set(currentIndex, k);
	}
	public double getSourceLength() {
		return sourceLength.get(currentIndex);
	}
	public void setSourceLength(double length) {
		this.sourceLength.set(currentIndex, length);
	}
	public int getSourceTrackNumber() {
		return sourceTrackNumbers.get(currentIndex);
	}
	public void setMergedLength(double length) {
		this.mergedLength.set(currentIndex, length);
	}
	public double getMergedLength() {
		return mergedLength.get(currentIndex);
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
		ks.add(null);
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
		if(Runtime.getRuntime().freeMemory()/Runtime.getRuntime().maxMemory()<0.2){
			
		}
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
	public void addRemovedConnectionNumber(int count) {
		removedConnectionNumbers.set(currentIndex, getRemovedConnectionNumber()+count);
		
	}
	public void write(String path) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
			oos.writeObject(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@Override
	public boolean equals(Object obj) {
		if(!obj.getClass().equals(this.getClass())){
			return false;
		}else{
			Statistician stati = (Statistician)obj;
			if(!ks.equals(stati.ks)){
				return false;
			}else if(!sourceTrackNumbers.equals(stati.sourceTrackNumbers)){
				return false;
			}else if(!sourceLength.equals(stati.sourceLength)){
				return false;
			}else if(!sourceWaypointNumbers.equals(stati.sourceWaypointNumbers)){
				return false;
			}else if(!sourceConnectionNumbers.equals(stati.sourceConnectionNumbers)){
				return false;
			}else if(!mergedWayPointNumbers.equals(stati.mergedWayPointNumbers)){
				return false;
			}else if(!mergedConnectionNumbers.equals(stati.mergedConnectionNumbers)){
				return false;
			}else if(!mergedLength.equals(stati.mergedLength)){
				return false;
			}else if(!mergedWaypointGrade.equals(stati.mergedWaypointGrade)){
				return false;
			}else if(!neighborGrade.equals(stati.neighborGrade)){
				return false;
			}else if(currentIndex!=stati.currentIndex){
				return false;
			}else if(maxIndex!=stati.maxIndex){
				return false;
			}else if(!removedConnectionNumbers.equals(stati.removedConnectionNumbers)){
				return false;
			}else if(!removedWaypointsNumbers.equals(stati.removedWaypointsNumbers)){
				return false;
			}else if(!removedTracksNumbers.equals(stati.removedTracksNumbers)){
				return false;
			}else{
				return true;
			}
		}
		
	}
}
