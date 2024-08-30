//osu! Songs Grabber by Mayuwhim
//July 2024
//Automatically copies and formats song files in your osu! folder to another folder

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

public class SongCopier {
	static final String Songs = "C:/Users/yourname/AppData/Local/osu!/Songs"; // YOUR SONG FOLDER HERE
	static final String destination = "C:/Users/yourname/music"; // YOUR DESTINATION FOLDER HERE
	
	public static void main(String[] args) {
		
		//get list of all osu! mapset folders
		File file = new File(Songs);
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
			  File f = new File(current, name);
			  if (f.isDirectory() && Character.isDigit(f.getName().charAt(0))) return true;
			  return false;
		  }
		});
		
		int ticker = 0;
		
		//pull songs from the folders
		for (int i = 0; i<directories.length; i++) {
			ArrayList<OsuFile> mapsList = new ArrayList<OsuFile>(); //song metadatas
			
			//process mapset folder
			File maybeSong = new File(Songs+directories[i]);
			//make list of song(s) in mapset
			String[] songFolder = maybeSong.list(new FilenameFilter() {
				  @Override
				  public boolean accept(File current, String name) {
					  File f = new File(current, name);
					  //file is a "song" if it is longer than 20s and ends with .mp3/.ogg (osu ranking criteria)
					  if (f.getName().toLowerCase().endsWith(".mp3") || f.getName().toLowerCase().endsWith(".ogg")) {
						  if (songDuration(f.getAbsolutePath())>20) return true;
						  return false;
					  }
					  //get song metadata from map files
					  if (f.getName().toLowerCase().endsWith(".osu")) {
						  try {
							  Scanner sc = new Scanner(f);
							  String text = "";
							  String audio = "";
							  String artist = "";
							  String title = "";
							  String diffname = "";
							  
							  while (sc.hasNextLine()) {
								  text = sc.nextLine();
								  if (text.contains("AudioFilename: ")) {
									  audio = text.split(": ")[1];
								  }
								  if (text.contains("Title:")) {
									  title = text.substring(6);
								  }
								  if (text.contains("Artist:")) {
									  artist = text.substring(7);
								  }
								  if (text.contains("Version:")) {
									  diffname = text.substring(8);
									  break;
								  }
							  }
							  //most mapsets have more than 1 map per song, check and filter for duplicate metadata
							  boolean already = false;
							  for (OsuFile m : mapsList) {
								  if (m.getAudio().equals(audio)) {
									  already = true;
								  }
								  
							  }
							  if (!already) {
								  mapsList.add(new OsuFile(audio, title, artist, diffname));
							  }
							  sc.close();
							  
						  } catch (FileNotFoundException e) {}
						  return false;
					  }
					  return false;
				 }
			});
			
			//System.out.println(directories[i]);
			
			//use song metadata to locate song files and name them properly
			for (OsuFile m: mapsList) {
				for (String s: songFolder) {
					if (m.getAudio().equals(s)) {
						File song = new File(Songs+directories[i]+"/"+s);
						//format name and replace any illegal file name characters
						String destname = destination + (m.getTitle().replace("-", "~") + " - " + m.getArtist().replace("-", "~")).replaceAll("[\\\\/:*?\"<>|]","🌳");
						//if there are multiple songs in the mapset song name might be in diffname instead of metadata so append diffname
						if (songFolder.length>1) {
							destname = destname + " [" + m.getDiffName().replace("-", "~").replaceAll("[\\\\/:*?\"<>|]","🌳")+ "]";
						}
						
						destname = destname + s.substring(s.length()-4).toLowerCase(); //add file extension
						
						//copy to new folder
						try {
							System.out.println(destname);
							FileUtils.copyFile(song, new File(destname));
						} catch (IOException e) {
							e.printStackTrace();
						}
						ticker++;
						System.out.println(ticker+"/"+(i+1)+" song(s) processed"); //debug
						break;
					}
				}
			}
			
		}
		
	}
	
	//find duration of audio file
	static int songDuration (String filepath) {
		File source = new File(filepath);
		Encoder encoder = new Encoder();
		try {
		    MultimediaInfo mi = encoder.getInfo(source);
		    long ls = mi.getDuration();
		    //System.out.println("duration(sec) = "+  ls/1000);
		    return (int)(ls/1000);
		} catch (Exception e) {
		    e.printStackTrace();
		    System.out.println(filepath);
		    return 0;
		}
	}
	
}

//song metadata object
class OsuFile {
	String AudioFilename;
	String Title;
	String Artist;
	String Diffname;
	OsuFile(String audio, String title, String artist, String diffname) {
		AudioFilename = audio;
		Title = title;
		Artist = artist;
		Diffname = diffname;
	}
	
	String getAudio() {
		return AudioFilename;
	}
	
	String getFullTitle() {
		//System.out.println(Title + " - " + Artist);
		String fullTitle = Title + " - " + Artist;
		return fullTitle;
	}
	
	String getDiffName() {
		return Diffname;
	}
	
	String getArtist() {
		return Artist;
	}
	
	String getTitle() {
		return Title;
	}
	
}
