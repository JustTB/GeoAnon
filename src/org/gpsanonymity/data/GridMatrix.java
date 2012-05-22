package org.gpsanonymity.data;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gpsanonymity.merge.MergeGPS;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;


public class GridMatrix extends Matrix<Integer, Bounds> {
	private int widthSize;
	private int heightSize;
	private double distance;
	private LatLon downLeftCorner;
	private Bounds wholeGrid;
	private BiMap<Bounds,MergedWayPoint> mergedWaypoints = new BiMap<Bounds, MergedWayPoint>();
	private List<GpxTrack> tracks;
	private double minimalSpeed;
	/**
	 * 
	 * @param ts MUST BE a Collection from type WayPoint or GpxTrack
	 * @param distance
	 * @exception throws an exception if it is not a Collection of WayPoint or GpxTrack
	 * IMPORTANT: through performance reasons we only check the first element of the collection,
	 * if the collection has not only GpxTracks or not only WayPoints this will bring DOOM!!!!!
	 */
	public GridMatrix(List<GpxTrack> ts,int k, double distance, double minimalSpeed) {
		this.minimalSpeed=minimalSpeed;
		initWithTracks(ts,k, distance);
	}
	public GridMatrix(double distance, Collection<WayPoint> wps) {
		initWithWayPoints(wps, distance);
	}
	private void initWithTracks(Collection<GpxTrack> ts,int k, double distance){
		Bounds newBounds=null;
		for (Iterator<GpxTrack> iterator = ts.iterator(); iterator.hasNext();) {
			GpxTrack gpxTrack = (GpxTrack) iterator.next();
			if (newBounds==null){
				newBounds=gpxTrack.getBounds();
			}else{
				newBounds.extend(gpxTrack.getBounds());
			}
		}
		initialize(newBounds,distance);
		addTracks(ts);
		eliminateLowerGrades(k);
		generateTracks(k);
	}
	private void eliminateLowerGrades(int k) {
		int size = mergedWaypoints.values().size();
		int counter=0;
		Collection<MergedWayPoint> values = new LinkedList<MergedWayPoint>(mergedWaypoints.values());
		for (MergedWayPoint mwp : values) {
			if(mwp.getGrade()<k){
				mergedWaypoints.removeValue(mwp);
				counter++;
			}
		}
		System.out.println("Pointsremoved:"+counter+"/"+size);
		
	}
	public void generateTracks(int k) {
		connectPoints(k);
		tracks=MergeGPS.buildTracks(new LinkedList<MergedWayPoint>(mergedWaypoints.values()),k);
		
	}
	public List<GpxTrack> getTracks() {
		return tracks;
	}
	private void connectPoints(int k) {
		for (int i = 0; i < widthSize; i++) {//count X
			for (int j = 0; j < heightSize; j++) {//count Y
				if(j+1<heightSize){//up
					connectSameTracks(i,j,i,j+1,k);
					if(i-1>=0){//upperleft
						connectSameTracks(i,j,i-1,j+1, k);
					}
					if(i+1<widthSize){//upperright
						connectSameTracks(i,j,i+1,j+1, k);
					}
				}
				if(i+1<widthSize){//right
					connectSameTracks(i,j,i+1,j, k);
				}
			}
		}
	}
	private void connectSameTracks(int x, int y, int x2, int y2, int k) {
		//get merged points
		MergedWayPoint mwp1 = getMergedPoint(x, y);
		MergedWayPoint mwp2 = getMergedPoint(x2, y2);
		if(mwp1!=null && mwp2!=null){
			mwp1.connectSameTracks(mwp2, distance, minimalSpeed,k);
		}
	}
	private MergedWayPoint getMergedPoint(int x, int y) {
		return mergedWaypoints.getValue(getValue(x, y));
	}
	public GridMatrix(GpxTrack t, double distance){
		initialize(t.getBounds(),distance);
		addTrack(t);
	}
	private void initWithWayPoints(Collection<WayPoint> wps, double distance){
		Bounds newBounds=null;
		for (Iterator<WayPoint> iterator = wps.iterator(); iterator.hasNext();) {
			WayPoint wp =  iterator.next();
			if (newBounds==null){
				newBounds= new Bounds(wp.getCoor());
			}else{
				newBounds.extend(wp.getCoor());
			}
		}
		initialize(newBounds,distance);
		addWayPoints(wps);
	}
	public static LatLon addNorthernDistance(LatLon p, double distance){
		return new LatLon(p.getY()+(180/Math.PI)*(distance/6378135),p.getX());
		//return new LatLon(p.getY()+distance/110574,p.getX()); 
	}
	public void addSegment(GpxTrackSegment s) {
		Collection<WayPoint> wps = s.getWayPoints();
		MergedWayPoint temp;
		for (Iterator<WayPoint> wp = wps.iterator(); wp.hasNext();) {
			WayPoint wayPoint = (WayPoint) wp.next();
			temp= addWayPoint(wayPoint);
			temp.addSegment(s,wayPoint);
		}
		
	}
	public void addTrack(GpxTrack t) {
		Collection<GpxTrackSegment> tss = t.getSegments();
		Collection<WayPoint> wps;
		MergedWayPoint temp;
		MergedWayPoint neighbor = null;
		for (Iterator<GpxTrackSegment> iterator = tss.iterator(); iterator.hasNext();) {
			GpxTrackSegment gpxTrackSegment = (GpxTrackSegment) iterator.next();
			wps = gpxTrackSegment.getWayPoints();
			for (Iterator<WayPoint> wp = wps.iterator(); wp.hasNext();) {
				WayPoint wayPoint = (WayPoint) wp.next();
				temp= addWayPoint(wayPoint);
				temp.addSegment(gpxTrackSegment,wayPoint);
				temp.addTrack(t,wayPoint);
				//temp is not the first element in the track and not the same MergedWayPoint
				if (neighbor!=null && neighbor!=temp){
					temp.connect(neighbor);
				}
				neighbor=temp;
			}
		}
	}
	public void addTracks(Collection<GpxTrack> ts) {
		for (Iterator<GpxTrack> iter = ts.iterator(); iter.hasNext();) {
			GpxTrack gpxTrack = (GpxTrack) iter.next();
			addTrack(gpxTrack);
			
		}
		
	}
	public void addWayPoints(Collection<WayPoint> wps) {
		for (WayPoint wp : wps) {
			addWayPoint(wp);
		}
	}
	public MergedWayPoint addWayPoint(WayPoint p) {
		Bounds bounds = findField(p);
		if(bounds==null){
			return null;
		}else if (mergedWaypoints.containsKey(bounds)){
			mergedWaypoints.getValue(bounds).addWayPoint(p);
			return mergedWaypoints.getValue(bounds);
		}else{
			MergedWayPoint newMWP = new MergedWayPoint(p);
			mergedWaypoints.putKeyValue(bounds, newMWP);
			return newMWP;
		}
	}
	public static LatLon addWesternDistance(LatLon p, double distance){
		return new LatLon(p.getY(),p.getX()+(180/Math.PI)*(distance/6378135)/Math.cos(Math.toRadians(p.getY())));
	}
	public Bounds findField(WayPoint wp){
		if (wholeGrid.contains(wp.getCoor())){
			LatLon referH = new LatLon(wp.getCoor().getY(),wholeGrid.getMin().getX());
			LatLon referW = new LatLon(wholeGrid.getMin().getY(),wp.getCoor().getX());
			//distance between referenceWidth to wp is height
			double hDistance=referW.greatCircleDistance(wp.getCoor());
			int h = (int)Math.floor(
					hDistance/distance);
			//distance between referenceHeight to wp is width
			double wDistance=referH.greatCircleDistance(wp.getCoor());
			int w = (int)Math.floor(wDistance/distance);
			
			Bounds correctedResult,result= getValue(w, h);
			if(!result.contains(wp.getCoor())){
				correctedResult=getValue(w+1, h);
				if(!correctedResult.contains(wp.getCoor())){
					correctedResult=findFieldSimple(wp);
				}
				return correctedResult;
			} 
			return result;
		}else{
			return null;
		}
		
	}
	private Bounds findFieldSimple(WayPoint wp) {
		Collection<Bounds> coll = values();
		Bounds result=null;
		for (Iterator<Bounds> iter = coll.iterator(); iter.hasNext();) {
			result = (Bounds) iter.next();
			if (result.contains(wp.getCoor())){
				break;
			}
		}
		return result;
	}
	private void generateGrid() {
		//initial Bounds lying on (-1,-1)
		LatLon uRCornerStart,dLCornerStart;
		LatLon dLCorner=dLCornerStart=downLeftCorner; 
		LatLon uRCorner=uRCornerStart=MergeGPS.addDistance(dLCornerStart, distance, distance);
		for (int w = 0; w < widthSize; w++) {//count X
			for (int h = 0; h < heightSize; h++) {//count Y
				dLCorner = MergeGPS.addDistance(dLCornerStart
						,h*distance
						,w*distance);
				uRCorner = MergeGPS.addDistance(uRCornerStart
						,h*distance,
						w*distance);
				put(w,h, new Bounds(dLCorner, uRCorner));
			}
			//set Y to -1
			dLCorner = new LatLon(dLCornerStart.getY(),dLCorner.getX());//addNorthernDistance(dLCorner, -heightSize*distance);
			uRCorner = new LatLon(uRCornerStart.getY(),uRCorner.getX());
		}
		wholeGrid = new Bounds(downLeftCorner,getValue(widthSize-1, heightSize-1).getMax());
	}
	public double getDistance() {
		return distance;
	}
	
	public LatLon getDownLeftCorner() {
		return downLeftCorner;
	}
	public double getHeight() {
		return heightSize;
	}
	public List<MergedWayPoint> getMergedWaypoints() {
		return new LinkedList<MergedWayPoint>(mergedWaypoints.values());
	}
	public double getWidth() {
		return widthSize;
	}
	private void initialize (Bounds border, double distance){
		this.distance=distance;
		LatLon rightDownCorner = new LatLon(border.getMin().getY(), border.getMax().getX());
		System.out.println("Width in m:"+ border.getMin()
				.greatCircleDistance(rightDownCorner));
		System.out.println("Height in m:"+ border.getMax()
				.greatCircleDistance(rightDownCorner));
		this.widthSize =1+ (int)Math.floor(
				border.getMin()
				.greatCircleDistance(rightDownCorner)/distance);
		this.heightSize =1+ (int)Math.floor(
				border.getMax()
				.greatCircleDistance(rightDownCorner)/distance);
		downLeftCorner=border.getMin();
		generateGrid();
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public void setDownLeftCorner(LatLon downLeftCorner) {
		this.downLeftCorner = downLeftCorner;
	}
	public void setHeight(int height) {
		this.heightSize = height;
	}
	public void setWidth(int width) {
		this.widthSize = width;
	}
}
