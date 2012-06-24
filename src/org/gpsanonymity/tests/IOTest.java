package org.gpsanonymity.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.gpsanonymity.io.IOFunctions;
import org.gpsanonymity.io.Importer;
import org.junit.Test;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.io.GpxReader;

public class IOTest {
	private int segNumber, trackNumber, waypointNumber;
	private String gpxTestData,fileName;
	public void testData() {
		try{
			gpxTestData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"Oregon 400t\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\"><metadata><link href=\"http://www.garmin.com\"><text>Garmin International</text></link><time>2009-10-17T22:58:43Z</time></metadata><trk><name>Example GPX Document</name><trkseg><trkpt lat=\"47.644548\" lon=\"-122.326897\"><ele>4.46</ele><time>2009-10-17T18:37:26Z</time></trkpt><trkpt lat=\"47.644548\" lon=\"-122.326897\"><ele>4.94</ele><time>2009-10-17T18:37:31Z</time></trkpt><trkpt lat=\"47.644548\" lon=\"-122.326897\"><ele>6.87</ele><time>2009-10-17T18:37:34Z</time></trkpt></trkseg></trk></gpx>";
			fileName = "test.gpx";
			segNumber=1;
			trackNumber=1;
			waypointNumber=3;
			File file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(gpxTestData.getBytes());
			file.deleteOnExit();
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testGetDataFromOSMWithCutting(){
		/*System.setProperty("http.proxySet", "true");
		System.setProperty("http.proxyPort","80");
		System.setProperty("http.proxyHost","http-proxy.fu-berlin.de");*/
		double minLat=53.233;
		double minLon=12.664;
		double maxLat=53.293;
		double maxLon=12.803;
		Bounds bounds = new Bounds(new LatLon(minLat,minLon),new LatLon(maxLat,maxLon));
		
		String filename= "output/McPomm.gpx";
		String tempFilename= "output/temp.gpx";
		testData();
		IOFunctions.getDataFromOSMWithCutting(bounds, filename, tempFilename);
	}
	@Test
	public void testDatFile(){
		double minLat=52.2010841;
		double minLon=13.3961105;
		double maxLat=52.2427277;
		double maxLon=13.4826279;
		
		String filename= "output/Berlin.gpx";
		testData();
		Bounds bounds = new Bounds(new LatLon(minLat,minLon),new LatLon(maxLat,maxLon));
		IOFunctions.generateDatFile(filename,bounds , 10);
		Importer imp = new Importer(filename.replace(".gpx", ".dat"));
		assertTrue(imp.getBounds().equals(bounds));
		assertTrue(imp.getCoorMax()==10);
	}
	@Test
	public void testGetDataFromOSM(){
		/*System.setProperty("http.proxySet", "true");
		System.setProperty("http.proxyPort","80");
		System.setProperty("http.proxyHost","http-proxy.fu-berlin.de");*/
		double minLat=52.2010841;
		double minLon=13.3961105;
		double maxLat=52.2427277;
		double maxLon=13.4826279;
		
		String filename= "output/Berlin.gpx";
		String tempFilename= "output/tempBerlin.gpx";
		testData();
		IOFunctions.getDataFromOSMWithOriginalGPXFiles(new Bounds(new LatLon(minLat,minLon),new LatLon(maxLat,maxLon)),filename,tempFilename);
	}
	@Test
	public void importData(){
		testData();
		GpxReader reader = IOFunctions.importGPX(fileName);
		assertTrue(IOFunctions.getAllWaypoints(reader).size()==waypointNumber);
		assertTrue(IOFunctions.getAllTrackSegments(reader).size()==segNumber);
		assertTrue(reader.data.tracks.size()==trackNumber);
		assertTrue(IOFunctions.getAllWaypoints(
				IOFunctions.getAllTrackSegments(
						reader)).size()==waypointNumber);
	}
	@Test
	public void exportData(){
		testData();
		LinkedList<WayPoint> list = new LinkedList<WayPoint>();
		list.add(new WayPoint(new LatLon(47.644548,-122.326897)));
		list.add(new WayPoint(new LatLon(47.644548,-122.326897)));
		list.add(new WayPoint(new LatLon(47.644548,-122.326897)));
		IOFunctions.exportWayPoints(list, fileName);
		GpxReader reader = IOFunctions.importGPX(fileName);
		assertTrue(IOFunctions.getAllWaypoints(reader).size()==3);
		assertTrue(IOFunctions.getAllTrackSegments(reader).size()==0);
		assertTrue(reader.data.tracks.size()==0);
		
	}
}
