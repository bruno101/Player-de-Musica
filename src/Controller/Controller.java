package Controller;

import View.*;
import Model.*;

import java.util.Collections;
import java.util.*;
import java.io.IOException;
import java.io.File;
import java.io.*;
import java.io.RandomAccessFile;
import java.nio.file.Files;
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

/*Para compilar:

(Windows)
javac -d bin -cp "lib/*" src/Controller/Controller.java src/View/View.java src/View/TransparentListCellRenderer.java src/View/UserSongsListCellRenderer.java src/Model/Song.java src/Model/Playlist.java src/Controller/PausablePlayer.java
java -cp bin -cp "bin;lib/*" Controller.Controller

*/

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
		
		this.importSongsFromUserData();
		this.playlist = new Playlist();
		
	}
	
	public void importSongsFromUserData() {
		
		this.genreSet = new TreeSet<String>();
	    this.userSongsList = new ArrayList<ArrayList<Song>>();
		
		File userData = new File("src\\UserData");
		
		//int i = 0;
		
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
	
	public void addSongToUserData (String filePath) {
		
		//copiamos para a pasta UserData
		File source = new File(filePath);
		File destination = new File("src\\UserData\\" + source.getName());
		
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
		this.playlist = new Playlist();
	}
	
	public void addToPlaylist (int indexGenre, int indexSong) {
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
			
			Mp3File mp3file = new Mp3File(filePath);
			
			File s = new File(filePath);
			System.out.println("Name of the file: " + s.getName());
			
			System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
            System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
            System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
            System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
            System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
            System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
			
			if (mp3file.hasId3v1Tag()) {
        	    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
        	    System.out.println("Artist: " + id3v1Tag.getArtist());
        	    System.out.println("Title: " + id3v1Tag.getTitle());
        	    System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
            }
        
		    if (mp3file.hasId3v2Tag()) {
        	    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
        	    System.out.println("Artist: " + id3v2Tag.getArtist());
        	    System.out.println("Title: " + id3v2Tag.getTitle());
        	    System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
        	    System.out.println("Comment: " + id3v2Tag.getComment());
        	    System.out.println("Composer: " + id3v2Tag.getComposer());
        	    System.out.println("Publisher: " + id3v2Tag.getPublisher());
        	    System.out.println("Original artist: " + id3v2Tag.getOriginalArtist());
        	    System.out.println("Album artist: " + id3v2Tag.getAlbumArtist());
        	    System.out.println("Copyright: " + id3v2Tag.getCopyright());
        	    System.out.println("URL: " + id3v2Tag.getUrl());
        	    System.out.println("Encoder: " + id3v2Tag.getEncoder());
            }
			
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
