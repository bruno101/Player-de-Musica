package Model;

import java.io.File;
import java.nio.file.*;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Song {
	
	private String songName;
	private String songGenre;
	private String songPath;
	
	public Song (String songPath) {
		
		try {
			
			Mp3File mp3file = new Mp3File(songPath);
			File s = new File(songPath);
			
			if (mp3file.hasId3v1Tag()) {
				
        	    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
				
				if ( (id3v1Tag.getTitle() != null) && (id3v1Tag.getArtist() != null) ) {
					this.songName = id3v1Tag.getArtist() + " - " + id3v1Tag.getTitle();
			    } else {
					this.songName = s.getName().substring(0, s.getName().length()-4);
				}
				
				if (id3v1Tag.getGenreDescription() != null) {
					this.songGenre = id3v1Tag.getGenreDescription();
				} else if ( (id3v1Tag.getComment() != null) && (id3v1Tag.getComment().startsWith("genre:")) ) {
					this.songGenre = id3v1Tag.getComment().substring(6);
				} else {
					this.songGenre = "Indefinido";
				}
				
			} else if (mp3file.hasId3v2Tag()) {
				
				ID3v2 id3v2Tag = mp3file.getId3v2Tag();
				
				if ( (id3v2Tag.getTitle() != null) && (id3v2Tag.getArtist() != null) ) {
					this.songName = id3v2Tag.getArtist() + " - " + id3v2Tag.getTitle();
			    } else {
					this.songName = s.getName().substring(0, s.getName().length()-4);
				}
				
				if (id3v2Tag.getGenreDescription() != null) {
					this.songGenre = id3v2Tag.getGenreDescription();
				} else if ( (id3v2Tag.getComment() != null) && (id3v2Tag.getComment().startsWith("genre:")) ) {
					this.songGenre = id3v2Tag.getComment().substring(6);
				} else {
					this.songGenre = "Indefinido";
				}
				
			} else {
				
				this.songName = s.getName().substring(0, s.getName().length()-4);
				this.songGenre = "Indefinido";
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
					
		this.songPath = songPath;
	}
	
	public String getSongName () {
		return this.songName;
	}
	
	public void setSongGenre (String genre) {
		
		this.songGenre = genre;
		
		//Alteramos tambem a tag para que nas proximas vez que abrirmos o programa o genero seja lembrado
		try {
		
		    Mp3File mp3file = new Mp3File(songPath);
			File s = new File(songPath);
		
    		if (mp3file.hasId3v1Tag()) {
	    		
		    	ID3v1 id3v1Tag = mp3file.getId3v1Tag();
	    		id3v1Tag.setComment("genre:"+genre);
			
	    	} else if (mp3file.hasId3v2Tag()) {
			
		    	//ID3v2 id3v2Tag = mp3file.getId3v2Tag();
		    	//id3v2Tag.setGenreDescription(genre);
				ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	    		id3v2Tag.setComment("genre:"+genre);
			
	    	} else {
			
		    	ID3v2 id3v2Tag = new ID3v24Tag();
			    id3v2Tag.setComment("genre:"+genre);
			    mp3file.setId3v2Tag(id3v2Tag);
				
			}
			
			//Substituindo o arquivo original
			mp3file.save("src\\UserData\\temp.mp3");
			File source = new File("src\\UserData\\temp.mp3");
			Files.move(source.toPath(), s.toPath(), StandardCopyOption.REPLACE_EXISTING);
			
		} catch (Exception e) {
			
			e.printStackTrace();
	    
		}
		
	}
	
	public String getSongGenre () {
		return this.songGenre;
	}
			
	
	public String getSongPath () {
		return this.songPath;
	}
	
	/*
    public String getGenreFromGenreNum (int genreNum) {
		return "";
	}
	
	public int getGenreNumFromGenre (String genre) {
		return 0;
    }
	*/

}