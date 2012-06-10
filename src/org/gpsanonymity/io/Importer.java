package org.gpsanonymity.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.gpx.GpxTrack;

public class Importer {
	private Bounds bounds;
	private int coorMax;
	private String genericPath;
	private int currentX=0;
	private int currentY=-1;
	
	public int getCurrentX() {
		return currentX;
	}
	public int getCurrentY() {
		return currentY;
	}
	public Importer(String path) {
		this.genericPath=path.replace(".dat", ".gpx");
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
			this.bounds =(Bounds)ois.readObject();
			this.coorMax = ois.readInt();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public List<GpxTrack> next(){
		currentY++;
		String currentFileName = genericPath.replace(".gpx", currentX+"_"+currentY+".gpx");
		return IOFunctions.getAllTracks(IOFunctions.importGPX(currentFileName));
	}
}
