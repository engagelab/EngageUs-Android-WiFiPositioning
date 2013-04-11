package com.example.rssitest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

// Basic file IO activities
public class FileIO {

	public static ArrayList<Location> deserialzeLocationFile(String filePath)
	{
		Location location;
		ArrayList<Location> locationList = new ArrayList<Location>();

		try
		{
			FileInputStream fin = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			location = (Location) ois.readObject();
			while(location != null) {
				locationList.add( location );
				location = (Location) ois.readObject();
			}
			ois.close();
			fin.close();
			return locationList;

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return locationList;
		} 
	}
	public static void deleteLocationFile(String filePath)
	{
		try
		{
			File f = new File(filePath);
			f.delete();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void serializeToLocationFile(ArrayList<Location> locationList, String filePath)
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			Iterator<Location> it = locationList.iterator();
			while(it.hasNext())
			{
				oos.writeObject(it.next());
			}
			oos.close();
			fout.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
