package model;

import java.util.ArrayList;

public class Playlist {
	
	private String name = "";
	private ArrayList<Song> songs = new ArrayList<>();
	
	public Playlist() {
	}
	
	public Playlist(String text) {
		
		String[] splitText = text.split(",,");
		this.name = splitText[0];
		for (int i = 1; i<splitText.length; i++) {
			this.songs.add(new Song(splitText[i]));
		}
		
	}
	
	public void setName (String name) {
		this.name = name;
	}
	
	public String getName () {
		return this.name;
	}
	
	public void addSong (String songPath) {
		this.songs.add(new Song(songPath));
	}
	
	public void addSong (Song song) {
		this.songs.add(song);
	}
	
	public Song getSong (int index) {
		return songs.get(index);
	}
	
	public String getSongName (int index) {
		return songs.get(index).getSongName();
	}
	
	public ArrayList<Song> getSongsList () {
		return this.songs;
	}
	
	public int getSize () {
		return this.songs.size();
	}
	
	public boolean contains (Song song) {
		return this.songs.contains(song);
	}
	
	@Override
	public String toString() {
		String text = this.name;
		for (Song song: this.songs) {
			text += ",,";
			text += song.getSongPath();
		}
		return text;
	}
}
