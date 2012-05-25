package org.gpsanonymity.io;








import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.gpx.GpxRoute;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrack;
import org.openstreetmap.josm.data.gpx.ImmutableGpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;
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
	public static void exportTrackSegments(Collection<GpxTrackSegment> segs,
			String string){
		GpxData gpxd = new GpxData();
		List<GpxTrack> tracks = new LinkedList<GpxTrack>();
		for (GpxTrackSegment seg : segs) {
			Collection<Collection<WayPoint>> trackSegs = new LinkedList<Collection<WayPoint>>();
			trackSegs.add(seg.getWayPoints());
			tracks.add(new ImmutableGpxTrack(trackSegs, new HashMap<String, Object>()));
		}
		exportTracks(tracks, string);
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
	public static List<WayPoint> getAllWaypointsFromTrack(List<GpxTrack> tracks) {
		List<WayPoint> result = new LinkedList<WayPoint>();
		for (GpxTrack gpxTrack : tracks) {
			for (GpxTrackSegment seg : gpxTrack.getSegments()) {
				for (WayPoint wayPoint : seg.getWayPoints()) {
					result.add(wayPoint);
				}
			}
		}
		return result;
	}
	public static List<GpxTrack> getDataFromOSM(Bounds bounds, String filename){
		URL url;
		List<GpxTrack> allTracks = new LinkedList<GpxTrack>();
		List<GpxTrack> result = null;
		GpxReader reader=null;
		try {
			LinkedList<GpxTrack> tempTracks = new LinkedList<GpxTrack>();
			System.out.println("Downloading ...");
			for(int i =0;i==0 || !reader.data.tracks.isEmpty();i++){
				String urlString= "http://api.openstreetmap.org/api/0.6/trackpoints?bbox="+bounds.getMin().getX()+","+bounds.getMin().getY()+","+bounds.getMax().getX()+","+bounds.getMax().getY()+"&page="+i;
				System.out.println(urlString);
				url = new URL(urlString);
				reader = new GpxReader(url.openStream());
				reader.parse(false);
				tempTracks.addAll(reader.data.tracks);
			}
			System.out.println("Downloading Tracks...");
			int count=0,urlcount=0,notParsedCount=0;
			HashSet<String> ids = new HashSet<String>(); 
			for(GpxTrack track :tempTracks){
				count++;
				Object urlObject= track.getAttributes().get("url");
				if (urlObject!=null){
					urlcount++;
					assert(urlObject.getClass()==String.class);
					String trackUrlAddress =(String)urlObject;
					String id=trackUrlAddress.substring(trackUrlAddress.lastIndexOf("/")+1);
					if(!ids.contains(id)){
						ids.add(id);
						System.out.println("ID from "+ id+ " from " + trackUrlAddress);
						trackUrlAddress= "http://www.openstreetmap.org/trace/"+id+"/data/";
						System.out.println(trackUrlAddress);
						URL trackUrl = new URL(trackUrlAddress);
						try{	
							GpxReader trackReader = new GpxReader(trackUrl.openStream());
							trackReader.parse(false);
							allTracks.addAll(trackReader.data.tracks);
						}
						catch (SAXException e) {
							System.out.println("Could not parse!");
							notParsedCount++;
							//e.printStackTrace();
						}
					}
				}
			}
			
			System.out.println("Tracks with URLs:" + urlcount+"/"+count);
			System.out.println("Tracks cant parse:" + notParsedCount+"/"+urlcount);
			result = new LinkedList<GpxTrack>(allTracks);
			System.out.println("Cutting...");
			for (Iterator<GpxTrack> iterator = allTracks.iterator(); iterator.hasNext();) {
				GpxTrack gpxTrack = (GpxTrack) iterator.next();
				Bounds trackBounds=gpxTrack.getBounds();
				if(!bounds.contains(trackBounds.getMin()) 
						|| !bounds.contains(trackBounds.getMax())){
					result.remove(gpxTrack);
					Collection<Collection<WayPoint>> tempSegList = new LinkedList<Collection<WayPoint>>();
					for (Iterator<GpxTrackSegment> iterator2 = gpxTrack.getSegments().iterator(); iterator2
							.hasNext();) {
						GpxTrackSegment seg = (GpxTrackSegment) iterator2.next();
						Collection<WayPoint> tempWPList = new LinkedList<WayPoint>();
						for (Iterator<WayPoint> iterator3 = seg.getWayPoints().iterator(); iterator3
								.hasNext();) {
							WayPoint wp = (WayPoint) iterator3.next();
							if(bounds.contains(wp.getCoor())){
								tempWPList.add(wp);
							}else if(!tempWPList.isEmpty()){
								tempSegList.add(tempWPList);
								tempWPList=new LinkedList<WayPoint>();
							}
						}
						if(!tempWPList.isEmpty()){
							tempSegList.add(tempWPList);
						}
					}
					result.add(new ImmutableGpxTrack(tempSegList, gpxTrack.getAttributes()));
				}
			}
			GpxWriter writer = new GpxWriter(new FileOutputStream(new File(filename)));
			GpxData resultData = new GpxData();
			resultData.tracks=result;
			writer.write(resultData);
			System.out.println("Written to " + filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allTracks;
	}

}
