package org.gpsanonymity.data;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.TypeConstraintException;

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
	private Bounds holeGrid;
	private BiMap<Bounds,MergedWayPoint> mergedWaypoints = new BiMap<Bounds, MergedWayPoint>();
	/**
	 * 
	 * @param ts MUST BE a Collection from type WayPoint or GpxTrack
	 * @param distance
	 * @exception throws an exception if it is not a Collection of WayPoint or GpxTrack
	 * IMPORTANT: through performance reasons we only check the first element of the collection,
	 * if the collection has not only GpxTracks or not only WayPoints this will bring DOOM!!!!!
	 */
	public GridMatrix(List<GpxTrack> ts, double distance) {
		initWithTracks(ts, distance);
	}
	public GridMatrix(double distance, Collection<WayPoint> wps) {
		initWithWayPoints(wps, distance);
	}
	private void initWithTracks(Collection<GpxTrack> ts, double distance){
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
		generateTracks();
	}
	public void generateTracks() {
		for (int i = 0; i < widthSize; i++) {//count X
			for (int j = 0; j < heightSize; j++) {//count Y
				if(j+1<heightSize){//up
					connectSameTracks(i,j,i,j+1);
					if(i-1>=0){//upperleft
						connectSameTracks(i,j,i-1,j+1);
					}
					if(i+1<widthSize){//upperright
						connectSameTracks(i,j,i+1,j+1);
					}
				}
				if(i+1<widthSize){//right
					connectSameTracks(i,j,i+1,j);
				}
			}
		}
		
	}
	private void connectSameTracks(int x, int y, int x2, int y2) {
		//get merged points
		MergedWayPoint firstOne = getMergedPoint(x,y);
		MergedWayPoint secondOne = getMergedPoint(x2,y2);
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
	}
	private LatLon addNorthernDistance(LatLon p, double distance){
		return new LatLon(p.getY()+distance/110574, p.getX()); 
	}
	public void addSegment(GpxTrackSegment s) {
		Collection<WayPoint> wps = s.getWayPoints();
		MergedWayPoint temp;
		for (Iterator<WayPoint> wp = wps.iterator(); wp.hasNext();) {
			WayPoint wayPoint = (WayPoint) wp.next();
			temp= addWayPoint(wayPoint);
			temp.addSegment(s);
		}
		
	}
	public void addTrack(GpxTrack t) {
		Collection<GpxTrackSegment> tss = t.getSegments();
		Collection<WayPoint> wps;
		MergedWayPoint temp;
		for (Iterator<GpxTrackSegment> iterator = tss.iterator(); iterator.hasNext();) {
			GpxTrackSegment gpxTrackSegment = (GpxTrackSegment) iterator.next();
			wps = gpxTrackSegment.getWayPoints();
			for (Iterator<WayPoint> wp = wps.iterator(); wp.hasNext();) {
				WayPoint wayPoint = (WayPoint) wp.next();
				temp= addWayPoint(wayPoint);
				temp.addSegment(gpxTrackSegment);
				temp.addTrack(t);
			}
		}
	}
	public void addTracks(Collection<GpxTrack> ts) {
		for (Iterator<GpxTrack> iter = ts.iterator(); iter.hasNext();) {
			GpxTrack gpxTrack = (GpxTrack) iter.next();
			addTrack(gpxTrack);
			
		}
		
	}
	public MergedWayPoint addWayPoint(WayPoint p) {
		Bounds bounds = findField(p);
		if (mergedWaypoints.containsKey(bounds)){
			mergedWaypoints.getValue(bounds).addWayPoint(p);
			return mergedWaypoints.getValue(bounds);
		}else{
			return null;
		}
	}
	private LatLon addWesternDistance(LatLon p, double distance){
		return new LatLon(p.getY(),p.getX()+distance/111320*Math.cos(p.getY()));
	}
	public Bounds findField(WayPoint wp){
		if (holeGrid.contains(wp.getCoor())){
			int x = (int)Math.ceil(wp.getCoor().getX()/distance)-1;
			int y = (int)Math.ceil(wp.getCoor().getY()/distance)-1;
			Bounds result= getValue(x, y);
			if (result.contains(wp.getCoor())){
				return result;
			}else{
				new Error("Assumption failure!");
				return null;
			}
		}else{
			return null;
		}
		
	}
	private void generateGrid() {
		//initial Bounds lying on (-1,-1)
		LatLon uRCorner=downLeftCorner;
		LatLon dLCorner = addWesternDistance(addNorthernDistance(uRCorner, -distance), -distance);
		for (int i = 0; i < widthSize; i++) {//count X
			dLCorner = addWesternDistance(dLCorner, distance);
			uRCorner = addWesternDistance(uRCorner, distance);
			for (int j = 0; j < heightSize; j++) {//count Y
				dLCorner = addNorthernDistance(dLCorner, distance);
				uRCorner = addNorthernDistance(dLCorner, distance);
				put(i,j, new Bounds(dLCorner, uRCorner));
			}
			//set Y to -1
			dLCorner = addNorthernDistance(dLCorner, -heightSize*distance);
			uRCorner = addNorthernDistance(dLCorner, -heightSize*distance);
		}
		holeGrid = new Bounds(downLeftCorner,getValue(widthSize-1, heightSize-1).getMax());
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
		this.widthSize = (int)Math.ceil(
				border.getMin()
				.greatCircleDistance(rightDownCorner)/distance);
		this.heightSize = (int)Math.ceil(
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