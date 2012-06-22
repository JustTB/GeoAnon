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
import java.util.HashSet;
import java.util.List;

import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;

public class Statistician implements Serializable{
	private static final long serialVersionUID = 1L;
	private transient String filePath="";
	private Integer ks;
	private Integer sourceTrackNumbers;
	private Double  sourceLength;
	private Integer sourceWaypointNumbers;
	private Integer sourceConnectionNumbers;
	private Integer mergedWaypointNumbers;
	private Integer mergedConnectionNumbers;
	private Integer mergedTrackNumbers;
	private Integer usedTrackNumbers;
	private Integer usedWaypointNumbers;
	private Double  mergedLength;
	private HashMap<Integer,Integer> mergedWaypointGrade = new HashMap<Integer,Integer>();//grade to number
	private HashMap<Integer,Integer> neighborGrade = new HashMap<Integer,Integer>();//grade to number
	private Integer removedConnectionNumbers;
	private Integer removedWaypointsNumbers;
	private Integer removedTracksNumbers;
	
	public Statistician() {
	}
	public Statistician(String path) {
		filePath=path;
		try {
			ObjectInputStream oos = new ObjectInputStream(new FileInputStream(new File(path)));
			Statistician statistician =(Statistician)oos.readObject();
			copyFrom(statistician);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}
	public void copyFrom(Statistician stati) {
		ks = stati.ks;
		sourceTrackNumbers = stati.sourceTrackNumbers;
		sourceLength = stati.sourceLength;
		sourceWaypointNumbers = stati.sourceConnectionNumbers;
		sourceConnectionNumbers = stati.sourceConnectionNumbers;
		mergedWaypointNumbers = stati.mergedWaypointNumbers;
		mergedConnectionNumbers = stati.mergedConnectionNumbers;
		mergedTrackNumbers = stati.mergedTrackNumbers;
		usedTrackNumbers = stati.usedTrackNumbers;
		mergedLength = stati.mergedLength;
		mergedWaypointGrade = new HashMap<Integer, Integer>(stati.mergedWaypointGrade);
		neighborGrade = new HashMap<Integer, Integer>(stati.neighborGrade);
		removedConnectionNumbers = stati.removedConnectionNumbers;
		removedWaypointsNumbers = stati.removedWaypointsNumbers;
		removedTracksNumbers = stati.removedTracksNumbers;
		
	}
	public void setFromMergedTracks(List<GpxTrack> tracks) {
		setMergedTrackNumber(tracks.size());
		int wpCount=0;
		int connectionCount=0;
		double length=0;
		HashSet<GpxTrack> tracksLeft= new HashSet<GpxTrack>();
		HashSet<WayPoint> wpsLeft= new HashSet<WayPoint>();
		for (GpxTrack gpxTrack : tracks) {
			length+=gpxTrack.length();
			for (GpxTrackSegment seg : gpxTrack.getSegments()) {
				for(WayPoint wps : seg.getWayPoints()){
					if(wps instanceof MergedWayPoint){
						MergedWayPoint mwp=(MergedWayPoint)wps;
						tracksLeft.addAll(mwp.getTracks());
						wpsLeft.addAll(mwp.getWayPoints());
					}
				}
				wpCount+=seg.getWayPoints().size();
				connectionCount+=seg.getWayPoints().size();
			}
			connectionCount--;
		}
		setUsedWaypointNumbers(wpsLeft.size());
		setUsedTrackNumbers(tracksLeft.size());
		setMergedWayPointNumber(wpCount);
		setMergedConnectionNumber(connectionCount);
		setMergedLength(length);
		
	}
	public void setFromMergedWayPoints(Collection<MergedWayPoint> mwps) {
		setMergedWayPointNumber(mwps.size());
		for (MergedWayPoint mwp : mwps) {
			incrementMergedWaypointGrade(mwp.getTrackGrade());
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
	public int getk() {
		return ks;
	}
	public void setk(int k) {
		this.ks=k;
	}
	public double getSourceLength() {
		return sourceLength;
	}
	public void setSourceLength(double length) {
		this.sourceLength=length;
	}
	public int getSourceTrackNumber() {
		return sourceTrackNumbers;
	}
	public void setMergedLength(double length) {
		this.mergedLength= length;
	}
	public double getMergedLength() {
		return mergedLength;
	}
	public void setSourceTrackNumber(int sourceTrackNumber) {
		this.sourceTrackNumbers=sourceTrackNumber;
	}
	public int getMergedConnectionNumber() {
		return mergedConnectionNumbers;
	}
	public void setMergedConnectionNumber(int sourceTrackNumber) {
		this.mergedConnectionNumbers=sourceTrackNumber;
	}
	public int getRemovedConnectionNumber() {
		return (removedConnectionNumbers==null?0:removedConnectionNumbers);
	}
	public void setRemovedConnectionNumber(int sourceTrackNumber) {
		this.removedConnectionNumbers=sourceTrackNumber;
	}
	public int getSourceConnectionNumber() {
		return sourceConnectionNumbers;
	}
	public void setSourceConnectionNumber(int sourceTrackNumber) {
		this.sourceConnectionNumbers=sourceTrackNumber;
	}
	public int getMergedTrackNumber() {
		return mergedTrackNumbers;
	}
	
	public void setMergedTrackNumber(int mergedSourceTrackNumber) {
		this.mergedTrackNumbers=mergedSourceTrackNumber;
	}
	public int getSourceWaypointNumber() {
		return sourceWaypointNumbers;
	}
	public void setSourceWaypointNumber(int sourceWaypointNumber) {
		this.sourceWaypointNumbers=sourceWaypointNumber;
	}
	public int getMergedWayPointNumber() {
		return mergedWaypointNumbers;
	}
	public void setMergedWayPointNumber(int mergedWayPointNumber) {
		this.mergedWaypointNumbers=mergedWayPointNumber;
	}
	public int getRemovedWaypointsNumber() {
		return removedWaypointsNumbers;
	}
	public void setRemovedWaypointsNumber(int removedWaypoints) {
		this.removedWaypointsNumbers=removedWaypoints;
	}
	public int getRemovedTracksNumber() {
		return removedTracksNumbers;
	}
	public void setRemovedTracksNumber(int removedTracks) {
		this.removedTracksNumbers=removedTracks;
	}
	public void addRemovedConnectionNumber(int count) {
		removedConnectionNumbers=getRemovedConnectionNumber()+count;
		
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
			}else if(!mergedWaypointNumbers.equals(stati.mergedWaypointNumbers)){
				return false;
			}else if(!mergedConnectionNumbers.equals(stati.mergedConnectionNumbers)){
				return false;
			}else if(!mergedLength.equals(stati.mergedLength)){
				return false;
			}else if(!mergedWaypointGrade.equals(stati.mergedWaypointGrade)){
				return false;
			}else if(!neighborGrade.equals(stati.neighborGrade)){
				return false;
			}else if(!removedConnectionNumbers.equals(stati.removedConnectionNumbers)){
				return false;
			}else if(!removedWaypointsNumbers.equals(stati.removedWaypointsNumbers)){
				return false;
			}else if(!removedTracksNumbers.equals(stati.removedTracksNumbers)){
				return false;
			}else if(!usedTrackNumbers.equals(stati.usedTrackNumbers)){
				return false;
			}else if(!usedWaypointNumbers.equals(stati.usedWaypointNumbers)){
				return false;
			}else{
				return true;
			}
		}
		
	}
	public int getUsedTrackNumbers() {
		return usedTrackNumbers;
	}
	public void setUsedTrackNumbers(int usedTrackNumbers) {
		this.usedTrackNumbers=usedTrackNumbers;
	}
	public int getUsedWaypointNumbers() {
		return usedWaypointNumbers;
	}
	public void setUsedWaypointNumbers(int usedWaypointNumbers) {
		this.usedWaypointNumbers=usedWaypointNumbers;
	}
	public void incrementMergedWaypointGrade(int grade) {
		if(this.mergedWaypointGrade.containsKey(grade)){
			this.mergedWaypointGrade.put(grade, this.mergedWaypointGrade.get(grade)+1);
		}else{
			this.mergedWaypointGrade.put(grade, 1);
		}
	}
	public void incrementNeighborGrade(int grade) {
		if(this.neighborGrade.containsKey(grade)){
			this.neighborGrade.put(grade, this.neighborGrade.get(grade)+1);
		}else{
			this.neighborGrade.put(grade, 1);
		}
	}
}
