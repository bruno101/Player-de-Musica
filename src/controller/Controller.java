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
	//caso o usuario esteja montando uma nova Playlist, indica se alguma musica foi adicionada - caso nenhuma seja, nao alteramos a playlist
	private boolean songAddedToNewPlaylist = false;
	
	//Um conjunto ordenado (TreeSet) com a lista de generos
	private TreeSet<String> genreSet;
	//Um conjunto de conjuntos de musicas de determinado estilo, na mesma ordem que o LinkedHashSet
	private ArrayList<ArrayList<Song>> userSongsList; 
	
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
		
		this.playlist = new Playlist();
		
	}
	
	public void importSongsFromUserData() {
		
		this.genreSet = new TreeSet<String>();
	        this.userSongsList = new ArrayList<ArrayList<Song>>();
		
		File userData = new File("resources\\userData");
		
		if ( (userData != null) && (userData.listFiles() != null) ) {
		
		for (File file: userData.listFiles()) {
				
			String songPath = file.getPath();
		        Song song = new Song(songPath);
		
            if (this.genreSet.contains(song.getSongGenre())) {
		   		//Note que 'headSet(element).size()' retorna o indice de um elemento contido em um TreeSet se ele estiver contido
    			        int index = this.genreSet.headSet(song.getSongGenre()).size();
	    		        this.userSongsList.get(index).add(song);
		   	} else {
		    	    this.genreSet.add(song.getSongGenre());
		    	    //queremos que a ordem dos generos em genreSet seja a mesma que em userSongsList
			    int index = this.genreSet.headSet(song.getSongGenre()).size();
			    this.userSongsList.add(index, new ArrayList<Song>());
			    this.userSongsList.get(index).add(song);
		   	}
				
			
		}
		
		this.view.addGenresToGenresList(this.genreSet);
		
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
		
		if (this.genreSet.contains(song.getSongGenre())) {
		   	//Note que 'headSet(element).size()' retorna o indice de um elemento contido em um TreeSet se ele estiver contido
    		        int index = this.genreSet.headSet(song.getSongGenre()).size();
	    	        this.userSongsList.get(index).add(song);
		} else {
			this.genreSet.add(song.getSongGenre());
			//queremos que a ordem dos generos em genreSet seja a mesma que em userSongsList
		        int index = this.genreSet.headSet(song.getSongGenre()).size();
		        this.userSongsList.add(index, new ArrayList<Song>());
		        this.userSongsList.get(index).add(song);
	   	}
		
		this.view.addGenresToGenresList(this.genreSet);
		
	}
	
	public void newPlaylist () {
		this.stopSong();
		this.songAddedToNewPlaylist = false;
		//so mudamos a playlist se pelo menos uma musica for adicionada a ela, entao aqui ainda nao instanciamos uma nova Playlist
	}
	
	public void addToPlaylist (int indexGenre, int indexSong) {
		if (!this.songAddedToNewPlaylist) {
			this.songAddedToNewPlaylist = true;
			this.playlist = new Playlist();
		}
		if (!playlist.contains(this.userSongsList.get(indexGenre).get(indexSong))) {
			this.playlist.addSong(this.userSongsList.get(indexGenre).get(indexSong));
		}
    }
	
	public HashSet<Integer> getAllChosenSongsFromGenre (int genreIndex) {
		
		HashSet<Integer> set = new HashSet<>();
		
		//precisamos encontrar o nome do genero nesse indice
		Iterator<String> it = this.genreSet.iterator();
		int cont = 0;
		String current = null;
		while (it.hasNext() && cont <= genreIndex) {
			current = it.next();
			cont++;
		}
		String genreName = current;
		
		for (Song song: this.playlist.getSongsList()) {
			if (genreName.equals(song.getSongGenre())) {
				set.add(this.userSongsList.get(genreIndex).indexOf(song));
			}
		}
		
		return set;
		
	}
	
	public void startPlaylist (boolean playFirstSong) {
		
		if (this.playlist.getSize() > 0) {
			
		    this.index = 0;
		    this.view.addSongsToPlaylist(this.playlist.getSongsList());
		    this.view.setCurrentSongIndex(this.index);
		    this.paused = false;
		
		    if (playFirstSong) {
		    	this.playSong();
	    	}
	
		}

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
	
	public void playSong () {
		
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
