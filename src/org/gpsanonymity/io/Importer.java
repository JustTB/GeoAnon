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
			this.bounds=((SerilizableBounds)ois.readObject()).getBounds();
			this.coorMax = ois.readInt();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public boolean hasnext() {
		if(currentY+1<coorMax){
			return true;
		}else if(currentX+1<=coorMax){
			return true;
		}else{
			return false;
		}

	}
	public boolean setCoor(int x, int y){
		boolean valid = true;
		if(x>=coorMax || x<0){
			currentX=0;
			valid=false;
		}else{
			currentX=x;
		}
		if(y>=coorMax || y<0){
			currentY=0;
			valid=false;
		}else{
			currentY=y;
			return true;
		}
		return valid;
	}
	public List<GpxTrack> next(){
		currentY++;
		if(currentY>=coorMax){
			currentY=0;
			currentX++;
		}else if (currentX>=coorMax){
			return null;
		}
		String currentFileName = genericPath.replace(".gpx", currentX+"_"+currentY+".gpx");
		return IOFunctions.getAllTracks(IOFunctions.importGPX(currentFileName));
	}
	public Bounds getBounds() {
		return bounds;
	}
	public int getCoorMax() {
		return coorMax;
	}
}
