package com.example.rssitest;

import java.io.IOException;
import java.io.Serializable;

// Represents a location by WiFi signal strength and methods to process its signal levels
public class Location implements Serializable {

	private static final long serialVersionUID = -7537231928043822227L;

	public String MAC1 = "";
	public int minLevel1 = 0;
	public int maxLevel1 = 0;
	public int averageLevel1 = 0;
	
	public String MAC2 = "";
	public int minLevel2 = 0;
	public int maxLevel2 = 0;
	public int averageLevel2 = 0;
	
	public String MAC3 = "";
	public int minLevel3 = 0;
	public int maxLevel3 = 0;
	public int averageLevel3 = 0;
	
	public String MAC4 = "";
	public int minLevel4 = 0;
	public int maxLevel4 = 0;
	public int averageLevel4 = 0;
	
	public int x = 0;
	public int y = 0;

	private boolean loc1Initialised, loc2Initialised, loc3Initialised, loc4Initialised = false;
	
	public Location() {

	}
	
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setMAC1Name(String MAC) {
		MAC1 = MAC;
	}
	public void setMAC2Name(String MAC) {
		MAC2 = MAC;
	}
	public void setMAC3Name(String MAC) {
		MAC3 = MAC;
	}
	public void setMAC4Name(String MAC) {
		MAC4 = MAC;
	}
	
	// Set min and max levels by comparing to previous samples
	// Remember that stronger level readings are lower negative ints
	public void setMAC1Level(int level) {
		minLevel1 = loc1Initialised ? (level > minLevel1 ? level : minLevel1) : level;
		maxLevel1 = loc1Initialised ? (level < maxLevel1 ? level : maxLevel1) : level;
		loc1Initialised = true;
	}
	
	public void setMAC2Level(int level) {
		minLevel2 = loc2Initialised ? (level > minLevel2 ? level : minLevel2) : level;
		maxLevel2 = loc2Initialised ? (level < maxLevel2 ? level : maxLevel2) : level;
		loc2Initialised = true;
	}
	
	public void setMAC3Level(int level) {
		minLevel3 = loc3Initialised ? (level > minLevel3 ? level : minLevel3) : level;
		maxLevel3 = loc3Initialised ? (level < maxLevel3 ? level : maxLevel3) : level;
		loc3Initialised = true;
	}

	public void setMAC4Level(int level) {
		minLevel4 = loc4Initialised ? (level > minLevel4 ? level : minLevel4) : level;
		maxLevel4 = loc4Initialised ? (level < maxLevel4 ? level : maxLevel4) : level;
		loc4Initialised = true;
	}
	
	public float getAverageLevel1() {
		return Math.round((maxLevel1 - minLevel1)/2 + minLevel1);
	}

	public float getAverageLevel2() {
		return Math.round((maxLevel2 - minLevel2)/2 + minLevel2);
	}

	public float getAverageLevel3() {
		return Math.round((maxLevel3 - minLevel3)/2 + minLevel3);
	}

	public float getAverageLevel4() {
		return Math.round((maxLevel4 - minLevel4)/2 + minLevel4);
	}

	// Set min and max directly
	public void setMAC1Range(int levelMin, int levelMax) {
		minLevel1 = levelMin;
		maxLevel1 = levelMax;
	}
	
	public void setMAC2Range(int levelMin, int levelMax) {
		minLevel2 = levelMin;
		maxLevel2 = levelMax;
	}
	
	public void setMAC3Range(int levelMin, int levelMax) {
		minLevel3 = levelMin;
		maxLevel3 = levelMax;
	}

	public void setMAC4Range(int levelMin, int levelMax) {
		minLevel4 = levelMin;
		maxLevel4 = levelMax;
	}
	
	public int getMinLevel1() {
		return minLevel1;
	}

	public int getMaxLevel1() {
		return maxLevel1;
	}

	public int getMinLevel2() {
		return minLevel2;
	}

	public int getMaxLevel2() {
		return maxLevel2;
	}

	public int getMinLevel3() {
		return minLevel3;
	}

	public int getMaxLevel3() {
		return maxLevel3;
	}

	public int getMinLevel4() {
		return minLevel4;
	}

	public int getMaxLevel4() {
		return maxLevel4;
	}

	public void setCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// Serialisation methods
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
	
	@Override
	public String toString() {
		return "x: "+x+" y: "+y;
	}
	
	// Determine how close this point is to another based on summed difference in signal strength
	public float distanceFrom(Location otherLocation) {
		return Math.abs(otherLocation.getAverageLevel1() - getAverageLevel1()) + Math.abs(otherLocation.getAverageLevel2() - getAverageLevel2()) + Math.abs(otherLocation.getAverageLevel3() - getAverageLevel3()) + Math.abs(otherLocation.getAverageLevel4() - getAverageLevel4());
	}
	
	// Determine whether this location is contained in the boundaries of another 
	public boolean boundsAreContaining(Location otherLocation) {
		float a1 = otherLocation.getAverageLevel1();
		float a2 = otherLocation.getAverageLevel2();
		float a3 = otherLocation.getAverageLevel3();
		float a4 = otherLocation.getAverageLevel4();
		boolean withinbounds = (minLevel1 >= a1 && a1 >= maxLevel1) && (minLevel2 >= a2 && a2 >= maxLevel2)
				&& (minLevel3 >= a3 && a3 >= maxLevel3) && (minLevel4 >= a4 && a4 >= maxLevel4);
		return withinbounds;
	}
	
}
