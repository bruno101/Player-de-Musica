package controller;

import view.*;
import model.*;

import java.util.Collections;
import java.util.*;
import java.io.IOException;
import java.io.File;
import java.io.*;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jl.player.advanced.*;
import javazoom.jl.player.Player;

public class Controller {
	
	View view;
	
	//a playlist em execucao
	private Playlist playlist;
	//o indice da musica selecionada no momento
	private int index;
	//PausablePlayer e a classe responsavel por executar o audio
	private PausablePlayer songBeingPlayed;
	//indica se a musica atual foi pausada
	private boolean paused;
	//indica o indice da Playlist selecionada pelo usuario no momento
	private int selectedPlaylistIndex;
	
	//A lista de generos
	private ArrayList<String> genreList;
	//Uma lista de listas de musicas de determinado estilo, na mesma ordem que o LinkedHashSet
	private ArrayList<ArrayList<Song>> userSongsList; 
	//Uma lista com as Playlists do usuario
	private ArrayList<Playlist> userPlaylistsList;
	
	
	public static void main(String args[]) {
		Controller controller = new Controller();
		View view = new View(controller);
		controller.view = view;
		controller.run();
	}
	
	public void run() {
		
		//verificamos se a pasta userData existe, e se ela nao existir a criamos
		if (!Files.exists(Paths.get("resources\\userData"))) {
			new File("resources\\userData").mkdirs();
		}
		
		this.importSongsFromUserData();
		this.importPlaylistsFromUserData();
		
		this.playlist = new Playlist();
		
	}
	
	public void importSongsFromUserData() {
		
		this.genreList = new ArrayList<String>();
	    this.userSongsList = new ArrayList<ArrayList<Song>>();
		
		File userData = new File("resources\\userData");
		
		if ( (userData != null) && (userData.listFiles() != null) ) {
		
		for (File file: userData.listFiles()) {
			
			if (!file.getPath().endsWith(".mp3")) {
				continue;
			}
				
			String songPath = file.getPath();
		    Song song = new Song(songPath);
		
            if (this.genreList.contains(song.getSongGenre())) {
    			int index = this.genreList.indexOf(song.getSongGenre());
	    		this.userSongsList.get(index).add(song);
		   	} else {
		    	this.genreList.add(song.getSongGenre());
		    	//queremos que a ordem dos generos em genreList seja a mesma que em userSongsList
			    int index = this.genreList.indexOf(song.getSongGenre());
			    this.userSongsList.add(index, new ArrayList<Song>());
			    this.userSongsList.get(index).add(song);
		   	}
				
			
		}
		
		this.view.addGenresToGenresList(this.genreList);
		
		}
		
	}
	
	public void addSongToUserData (String filePath) {
		
		//copiamos para a pasta UserData
		File source = new File(filePath);
		File destination = new File("resources\\userData\\" + source.getName());
		
		try {
			Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Song song = new Song(destination.getPath());
		
		//fazemos esse tratamento para lidarmos com musicas sem genero explicito na tag
		if (song.getSongGenre().equals("Indefinido")) {
			this.view.askUserForGenreName(song);
		} else {
			addSongToUserSongsList(song);
		}
	}
	
	public void addSongToUserSongsList (Song song) {
		
		//adicionamos ao vetor de musicas do usuario para que a musica importada possa ser encontrada sem que o app tenha que ser reiniciado
		
		if (this.genreList.contains(song.getSongGenre())) {
    		        int index = this.genreList.indexOf(song.getSongGenre());
	    	        this.userSongsList.get(index).add(song);
		} else {
			this.genreList.add(song.getSongGenre());
			//queremos que a ordem dos generos em genreList seja a mesma que em userSongsList
		        int index = this.genreList.indexOf(song.getSongGenre());
		        this.userSongsList.add(index, new ArrayList<Song>());
		        this.userSongsList.get(index).add(song);
	   	}
		
		this.view.addGenresToGenresList(this.genreList);
		
	}
	
	public void importPlaylistsFromUserData () {
		
		this.userPlaylistsList = new ArrayList<>();
		
		try {
			File userPlaylistData = new File("resources\\userData\\userPlaylistData.txt");
		    if (userPlaylistData.exists()) {
				String content = new Scanner(userPlaylistData).useDelimiter("\\Z").next();
				for (String playlistText : content.split("\n\n")) {
					this.userPlaylistsList.add(new Playlist(playlistText));
				}
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.view.addPlaylistsToPlaylistsList(this.userPlaylistsList);
		
	}
	
	public void addPlaylistToUserData (Playlist playlist) {
		
		try {
			
			File userPlaylistData = new File("resources\\userData\\userPlaylistData.txt");
		    if (!userPlaylistData.exists()) {
			    userPlaylistData.createNewFile();
				Files.write(Paths.get("resources\\userData\\userPlaylistData.txt"), playlist.toString().getBytes(), StandardOpenOption.APPEND);
		    } else {
				Files.write(Paths.get("resources\\userData\\userPlaylistData.txt"), ( "\n\n"+ playlist ).getBytes(), StandardOpenOption.APPEND);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addPlaylistToUserPlaylistsList (Playlist playlist) {
		
		this.userPlaylistsList.add(playlist);
		this.view.addPlaylistsToPlaylistsList(this.userPlaylistsList);
		
	}
	
	public void newPlaylist () {
		this.stopSong();
		this.playlist = new Playlist();
	}
	
	public void setSelectedPlaylistIndex (int index) {
		this.selectedPlaylistIndex = index;
	}
	
	public Playlist getPlaylist () {
		return this.playlist;
	}
	
	public void setPlaylistName (String name) {
		this.playlist.setName (name);
	}
	
	public void addToPlaylist (int indexGenre, int indexSong) {
		if (!playlist.contains(this.userSongsList.get(indexGenre).get(indexSong))) {
			this.playlist.addSong(this.userSongsList.get(indexGenre).get(indexSong));
		}
    }
	
	public HashSet<Integer> getAllChosenSongsFromGenre (int genreIndex) {
		
		HashSet<Integer> set = new HashSet<>();
		
		String genreName = this.genreList.get(genreIndex);
		
		for (Song song: this.playlist.getSongsList()) {
			if (genreName.equals(song.getSongGenre())) {
				set.add(this.userSongsList.get(genreIndex).indexOf(song));
			}
		}
		
		return set;
		
	}
	
	//retorna 0 se nao é possível começar alguma Playlist
	public int startPlaylist (boolean playFirstSong) {
			
		
		this.index = 0;
		if (this.selectedPlaylistIndex != -1) {
			this.stopSong();
            this.playlist = this.userPlaylistsList.get(this.selectedPlaylistIndex);
		} else {
			return 0;
		}
		this.view.addSongsToPlaylist(this.playlist.getSongsList());
		if (this.playlist.getSize() > 0) {
		     this.view.setCurrentSongIndex(this.index);
		} else {
			return 0;
		}
		this.paused = false;
		
		if (playFirstSong) {
		    this.playSong();
	    }
		
		return 1;

	}
	
	public String getCurrentSongName () {
		return this.playlist.getSongName(this.index);
	}
	
	public void goBackward () {
		
		System.out.println("BACKWARD");
		System.out.println(this.index);
		
		if (this.index > 0) {
			
			this.index--;
			this.view.setCurrentSongIndex(this.index);
			System.out.println("Voltar");
			this.paused = false;
		
		    if (this.songBeingPlayed != null) {
			    this.stopSong();
			    this.playSong();
		    }
			
		}
		
	}
	
	public int playSong () {
		
		if (this.playlist.getSize() == 0) {
			return 0;
		}
		
		try {
			
			String filePath = this.playlist.getSong(index).getSongPath();
			
			if (this.paused) {
				
				this.songBeingPlayed.resume();
				this.paused = false;
				
			} else {
				
				if (this.songBeingPlayed == null) {
					FileInputStream in = new FileInputStream(filePath);
			                this.songBeingPlayed = new PausablePlayer(in, this);
			                this.songBeingPlayed.play();
				}
				
			}
			
			return 1;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
		
	}
	
	public void pauseSong () {
		
		if (this.songBeingPlayed != null) {
			System.out.println("Pausar");
		        this.songBeingPlayed.pause();
		        this.paused = true;
		}
		
	}
	
	public void stopSong () {
		
		if (this.songBeingPlayed != null) {
			System.out.println("Parar");
		        this.paused = false;
		        this.songBeingPlayed.stop();
			this.songBeingPlayed = null;
		}
		
	}
	
	public void goForward () {
		
		System.out.println("FORWARD");
		System.out.println(this.index);
		
		if ( (this.index + 1) < this.playlist.getSize() ) {
			
			this.index++;
			this.view.setCurrentSongIndex(this.index);
			System.out.println("Proximo");
			this.paused = false;
		
		    if (this.songBeingPlayed != null) {
			    this.stopSong();
			    this.playSong();
		    }
		
		}
	
	}
	
	public void goToSong (int index) {
		
		System.out.println("Ir para musica");
		System.out.println(this.index);
		
		this.index = index;
		this.view.setCurrentSongIndex(this.index);
		System.out.println("Proximo");
		this.paused = false;
		
		if (this.songBeingPlayed != null) {
			this.stopSong();
	     	        this.playSong();
                }
	
	}
	
	public ArrayList<Song> getUserSongsFromGenre (int index) {
		return this.userSongsList.get(index);
	}
	
}
