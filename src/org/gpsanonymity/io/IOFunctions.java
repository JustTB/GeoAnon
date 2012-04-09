package org.gpsanonymity.io;








import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.gpx.GpxRoute;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.gui.preferences.GPXSettingsPanel;
import org.openstreetmap.josm.io.GpxReader;
import org.openstreetmap.josm.io.GpxWriter;
import org.xml.sax.SAXException;


public class IOFunctions {
	//number of nearest waypoints
	
	public static void exportWayPoints(List<WayPoint> wayPoints,
			String string){
		GpxData gpxd = new GpxData();
		gpxd.waypoints=wayPoints;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(string));
			GpxWriter gpxWriter = new GpxWriter(fos);
			gpxWriter.write(gpxd);
		} catch (FileNotFoundException e) {
			System.out.println("File "+string+" not found.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding.");
			e.printStackTrace();
		}
		
		
		
		
	}
	public static void exportTracks(List<GpxTrack> tracks,
			String string){
		GpxData gpxd = new GpxData();
		gpxd.tracks=tracks;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(string));
			GpxWriter gpxWriter = new GpxWriter(fos);
			gpxWriter.write(gpxd);
		} catch (FileNotFoundException e) {
			System.out.println("File "+string+" not found.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding.");
			e.printStackTrace();
		}
		
		
		
		
	}
	public static LinkedList<GpxTrackSegment> getAllTrackSegments(
			GpxReader reader) {
		LinkedList<GpxTrackSegment> result = new LinkedList<GpxTrackSegment>();
		for (Iterator<GpxTrack> gpxIterator = reader.data.tracks.iterator(); gpxIterator.hasNext();) {
			GpxTrack track = gpxIterator.next();
			result.addAll(track.getSegments());
		}
		return result;
	}
	public static LinkedList<WayPoint> getAllWaypoints(GpxReader reader) {
		LinkedList<WayPoint> result = new LinkedList<WayPoint>();
		for (Iterator<GpxRoute> gpxRoute = reader.data.routes.iterator(); gpxRoute.hasNext();) {
			GpxRoute route = (GpxRoute) gpxRoute.next();
			result.addAll(route.routePoints);
		}
        //For all Tracks get all TrackSeqments and add the Waypoints to to the LinkedList waypoints
        for (Iterator<GpxTrack> gpxIterator = reader.data.tracks.iterator(); gpxIterator.hasNext();) {
			GpxTrack track = gpxIterator.next();
			for (Iterator<GpxTrackSegment> trackseg = track.getSegments().iterator(); trackseg.hasNext();) {
				GpxTrackSegment seq = (GpxTrackSegment) trackseg.next();
				result.addAll(seq.getWayPoints());
			}
		}
        //add all waypoints
        result.addAll(reader.data.waypoints);
        System.out.println("Number of Waypoints:" + result.size());
		return result;
	}
	public static LinkedList<WayPoint> getAllWaypoints(List<GpxTrackSegment> segments) {
		LinkedList<WayPoint> result = new LinkedList<WayPoint>();
		//For all Tracks get all TrackSeqments and add the Waypoints to to the LinkedList waypoints

		for (Iterator<GpxTrackSegment> trackseg = segments.iterator(); trackseg.hasNext();) {
			GpxTrackSegment seq = (GpxTrackSegment) trackseg.next();
			result.addAll(seq.getWayPoints());
		}
		return result;
	}
	/**
	 * imports the waypoints from a gpx file given in f and put it in to waypoints
	 * 1. the routewaypoints
	 * 2. the track waypoints
	 * 3. the waypoints
	 * 
	 * @param f gpx file
	 * @param waypoints 
	 * @throws IOException if the file is not readable
	 * @throws SAXException cant parse the gpx file f
	 */
	public static GpxReader importGPX(String fPath) {
		try{
			File f = new File(fPath);
			FileInputStream fis = new FileInputStream(f);
			final GpxReader r = new GpxReader(fis);
			r.parse(true);
			System.out.println("Has RoutePoints:" + r.data.hasRoutePoints());
			System.out.println("Has Trackpoints:" + r.data.hasTrackPoints());
			System.out.println("Not Empty:" + !r.data.waypoints.isEmpty());
			return r;
		}catch (IOException e) {
			System.out.println(fPath + " is not readable.");
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("No GPX file or not parsable.");
			e.printStackTrace();
		}
        return null;
        
	}
	public static List<GpxTrack> getAllTracks(GpxReader reader) {
		return new LinkedList<GpxTrack>(reader.data.tracks);
	}

}
