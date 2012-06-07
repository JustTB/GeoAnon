package org.gpsanonymity.io;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.io.GpxReader;
import org.xml.sax.SAXException;

public class TrackAdder implements Runnable{
	
	private Collection<GpxTrack> tracks;
	private String url;
	private AtomicBoolean zeroTracks;
	public TrackAdder(String url, Collection<GpxTrack> tracks, AtomicBoolean endIsNear) {
		this.zeroTracks=endIsNear;
		this.tracks=tracks;	
		this.url=url;
	}
	public void run() {
		try{
			URL url = new URL(this.url);	
			GpxReader reader = new GpxReader(url.openStream());
			System.out.println(url);
			reader.parse(false);
			if(reader.data.tracks.size()>0){
				System.out.println(reader.data.tracks.size());
				tracks.addAll(reader.data.tracks);
			}else{
				zeroTracks.set(true);
			}
		}catch(SAXException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
