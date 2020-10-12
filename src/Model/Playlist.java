package Model;

import java.util.ArrayList;

public class Playlist {
	
	private ArrayList<Song> songs = new ArrayList<>();
	
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
	
}