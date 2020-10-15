package view;

import controller.*;
import model.*;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.io.File;
import java.util.*;

public class View {
	
	private Controller controller;
	
	private JFrame frame;
	private JList playlist;
	private JComboBox genresList;
	private JList userSongsList;
	private JLabel currentSongLabel;
	
	//conterao os indices do estilo e da musica que o user selecionar no painel de montagem de playlist
	private int selectedGenreIndex;
	private int selectedSongIndex;

    public View(Controller controller) {
		
		this.controller = controller;
	
	     //Instanciamos a 'Jframe' e o 'CardLayout' por meio do qual definiremos qual painel mostrar
	     this.frame = new JFrame("Executar Musica");
		 CardLayout cardLayout = new CardLayout();
		 
		 //Criamos a label com o nome da musica atual
		 this.currentSongLabel = new JLabel("");
		 this.currentSongLabel.setBounds(5,10,550,45);
		 this.currentSongLabel.setHorizontalAlignment(JLabel.CENTER);
		 
		 //Criamos os botoes de controle de midia
		 JButton back = new JButton();
		 back.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.goBackward();
            }	
        
		 });
		 JButton stop = new JButton();
		 stop.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.stopSong();
            }	
        
		 });
		 JButton play = new JButton();
		 play.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.playSong();
            }	
        
		 });
		 JButton pause = new JButton();
		 pause.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.pauseSong();
            }	
        
		 });
		 JButton forward = new JButton();
		 forward.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.goForward();
            }	
        
		 });
		 
		 //Definimos suas localizacoes e tamanhos
		 back.setBounds(130,65,45,45);
		 stop.setBounds(195,65,45,45);
		 play.setBounds(260,63,49,49);
		 pause.setBounds(325,65,45,45);
		 forward.setBounds(390,65,45,45);
		 
		 //Adicionamos os respectivos icones aos botoes de controle de midia
		 try {
			 Image image = ImageIO.read(new File("resources\\buttons\\BackButton.png")).getScaledInstance(49,49,Image.SCALE_DEFAULT);
			 back.setIcon(new ImageIcon(image));
			 image = ImageIO.read(new File("resources\\buttons\\StopButton.png")).getScaledInstance(49,49,Image.SCALE_DEFAULT);
			 stop.setIcon(new ImageIcon(image));
			 image = ImageIO.read(new File("resources\\buttons\\PlayButton.png")).getScaledInstance(49,49,Image.SCALE_DEFAULT);
			 play.setIcon(new ImageIcon(image));
			 image = ImageIO.read(new File("resources\\buttons\\PauseButton.png")).getScaledInstance(49,49,Image.SCALE_DEFAULT);
			 pause.setIcon(new ImageIcon(image));
			 image = ImageIO.read(new File("resources\\buttons\\ForwardButton.png")).getScaledInstance(45,45,Image.SCALE_DEFAULT);
			 forward.setIcon(new ImageIcon(image));
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 
		 //Aqui definimos o botao 'importarMusica' e a sua respectiva ação
		 JFrame telaImportacao = new JFrame();
		 telaImportacao.setSize(300,300);
		 
		 JButton importarMusica = new JButton("Importar Musica");
		 importarMusica.setBounds(70,125,200,45);
		 importarMusica.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser("Importar Musica");
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				//um filtro pra que so aparecam arquivos '.mp3'
				FileFilter filter = new FileNameExtensionFilter("MP3 File", "mp3");
				fileChooser.setFileFilter(filter);
				int result = fileChooser.showOpenDialog(telaImportacao);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					
					File file = fileChooser.getSelectedFile();
					
					if (file.isFile()) {
						
						controller.addSongToUserData(file.getAbsolutePath());
						System.out.println(file.getAbsolutePath());
						
					} else {
						
						File[] filesInDirectory = file.listFiles();
						
					    for (File songFile: filesInDirectory) {
							
						    if (songFile.getAbsolutePath().endsWith(".mp3")) {
								
							    controller.addSongToUserData(songFile.getAbsolutePath());
								System.out.println(songFile.getAbsolutePath());
								
					    	}
							
                        }
						
					}
					
				}
				
            }	
        
		 });
		 
		 //Definimos o botao de 'montarPlaylist', que quando clicado alterara o painel sendo mostrado para o 'painelDeMontagemDePlaylist'
		 JButton montarPlaylist = new JButton("Montar Playlist");
		 montarPlaylist.setBounds(290,125,200,45);
		 montarPlaylist.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {

                cardLayout.show(frame.getContentPane(), "painelDeMontagemDePlaylist");
				frame.setTitle("Montar Playlist");
				
				//recarregamos a lista de musicas do usuario
				((UserSongsListCellRenderer) userSongsList.getCellRenderer()).restart();
				userSongsList.updateUI();
				
				controller.newPlaylist();
				
            }	
        
		 });
		 
		 //Essa lista contera as musicas da playlist em execucao
		 this.playlist = new JList();
		 this.playlist.setOpaque(false);
		 this.playlist.setCellRenderer(new TransparentListCellRenderer());
		 this.playlist.setBounds(10,195,530,375);
		 //O clique em uma celula da lista gera uma acao
		 this.playlist.addMouseListener(new MouseAdapter() {
			 
			 @Override
			 public void mouseClicked(MouseEvent e) {
				 System.out.println("Clicked.");
				 int index = playlist.getSelectedIndex();
				 if (index != -1) {
					 controller.goToSong(index);
				 }
				 System.out.println("Index Selected: " + index);
				 String s = (String) playlist.getSelectedValue();
				 System.out.println("Value Selected: " + s);
			 }
			 
		});
		 
		 //Adicionamos um scroll para a lista
		 JScrollPane scrollablePlaylist = new JScrollPane(this.playlist);
		 scrollablePlaylist.setBounds(10,195,530,375);
		 scrollablePlaylist.setOpaque(false);
		 scrollablePlaylist.getViewport().setOpaque(false);
		 
		 //Definimos a tela principal, em que uma Playlist é executada
		 JPanel painelPrincipal = new JPanel();
		 //Adicionamos seus botoes e atributos
		 painelPrincipal.add(this.currentSongLabel);
		 painelPrincipal.add(back);
		 painelPrincipal.add(pause);
		 painelPrincipal.add(play);
		 painelPrincipal.add(stop);
		 painelPrincipal.add(forward);
		 painelPrincipal.add(importarMusica);
		 painelPrincipal.add(montarPlaylist);
		 painelPrincipal.add(scrollablePlaylist);
		 painelPrincipal.setBounds(0,0,565,620);
		 painelPrincipal.setLayout(null);
		 
		 //A partir daqui definimos os botoes do painel 'painelDeMontagemDePlaylist'
		 //O botao 'executarPlaylist' volta para a tela principal
		 JButton executarPlaylist = new JButton("Executar Playlist");
		 executarPlaylist.setBounds(10,10,200,45);
		 executarPlaylist.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {

                cardLayout.show(frame.getContentPane(), "painelPrincipal");
				controller.startPlaylist(false);
				frame.setTitle("Executar Playlist");
				
            }	
        
		 });
		 
		 //Aqui definimos a lista em que o usuario selecionara um estilo de musicas
		 JLabel genreChoiceLabel = new JLabel("Escolha o estilo");
		 genreChoiceLabel.setBounds(10,70,200,45);
		 
		 //conterao os indices do estilo e da musica que o user selecionar
		 
		 this.genresList = new JComboBox();
		 //this.genresList.setOpaque(false);
		 //this.genresList.setCellRenderer(new TransparentListCellRenderer());
		 this.genresList.setBounds(10,125,530,30);
		 //a escolha do usuario afetara o estado da lista de musicas exibidas na tela (userSongsList)
		 this.genresList.addActionListener(new ActionListener() {
			 
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 
				 System.out.println("Chosen.");
				 selectedGenreIndex = genresList.getSelectedIndex();
				 
				 //alteramos a lista de musicas
				 addSongsToDisplayedUserSongsList(controller.getUserSongsFromGenre(selectedGenreIndex));
				 
				 //alteramos o conjunto de musicas que devem ser destacadas
				 ((UserSongsListCellRenderer) userSongsList.getCellRenderer()).setClickedSet(controller.getAllChosenSongsFromGenre(selectedGenreIndex));
				 userSongsList.updateUI();
				 
				 System.out.println("Index Selected: " + selectedGenreIndex);
				 String s= (String) genresList.getSelectedItem();
				 System.out.println("Value Selected: " + s);
				 
			 }
			 
		});
		 JScrollPane scrollableGenreList = new JScrollPane(this.genresList);
		 scrollableGenreList.setBounds(10,125,530,30);
		 scrollableGenreList.setOpaque(false);
		 scrollableGenreList.getViewport().setOpaque(false);
		 
		 //Aqui definimos a lista em que o usuario clicara nas musicas que quer reproduzir
		 JLabel songChoiceLabel = new JLabel("Clique nas musicas que voce quer reproduzir na sua playlist");
		 songChoiceLabel.setBounds(10,165,400,45);
		 
		 UserSongsListCellRenderer userSongsListCellRenderer = new UserSongsListCellRenderer();
		 this.userSongsList = new JList();
		 this.userSongsList.setOpaque(false);
		 this.userSongsList.setCellRenderer(userSongsListCellRenderer);
		 this.userSongsList.setBounds(10,220,530,350);
		 this.userSongsList.addMouseListener(new MouseAdapter() {
			 
			 @Override
			 public void mouseClicked(MouseEvent e) {
				 System.out.println("Clicked.");
				 selectedSongIndex = userSongsList.getSelectedIndex();
				 userSongsListCellRenderer.addToClickedSet(selectedSongIndex);
				 controller.addToPlaylist(selectedGenreIndex, selectedSongIndex);
				 System.out.println("Index Selected: " + selectedSongIndex);
				 String s = (String) userSongsList.getSelectedValue();
				 System.out.println("Value Selected: " + s);
			 }
			 
		});
		 JScrollPane scrollableUserSongsList = new JScrollPane(this.userSongsList);
		 scrollableUserSongsList.setBounds(10,220,530,350);
		 scrollableUserSongsList.setOpaque(false);
		 scrollableUserSongsList.getViewport().setOpaque(false);
		 
		 //Adicionamos os botoes do 'painelDeMontagemDePlaylist'
		 JPanel painelDeMontagemDePlaylist = new JPanel();
		 painelDeMontagemDePlaylist.setBounds(0,0,565,620);
		 painelDeMontagemDePlaylist.setLayout(null);
		 painelDeMontagemDePlaylist.add(executarPlaylist);
		 painelDeMontagemDePlaylist.add(genreChoiceLabel);
		 painelDeMontagemDePlaylist.add(scrollableGenreList);
		 painelDeMontagemDePlaylist.add(songChoiceLabel);
		 painelDeMontagemDePlaylist.add(scrollableUserSongsList);
		 
		 //Por fim definimos os atributos da 'JFrame' e adicionamos os paineis e o 'CardLayout' que permitirá gerir qual painel mostrar
		 frame.setSize(565,620);
		 frame.setResizable(false);
		 frame.setLayout(cardLayout);
		 frame.add(painelPrincipal, "painelPrincipal");
		 frame.add(painelDeMontagemDePlaylist, "painelDeMontagemDePlaylist");
		 cardLayout.show(frame.getContentPane(), "painelPrincipal");
		 frame.setVisible(true);
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//As funcoes a seguir recebem informacoes do Controller/Model e alteram a View
	
	public void addSongsToPlaylist (ArrayList<Song> songs) {
		
		DefaultListModel model = new DefaultListModel();
		for (Song song : songs) {
			model.addElement(song.getSongName());
		}
		this.playlist.setModel(model);
		
	}
	
	public void addGenresToGenresList (Set<String> genres) {
	
	    DefaultComboBoxModel genresModel = new DefaultComboBoxModel();
		for (String genre: genres) {
			genresModel.addElement(genre);
		}
		this.genresList.setModel(genresModel);
		
	}
	
	public void addSongsToDisplayedUserSongsList (ArrayList<Song> userSongs) {
	
	    DefaultListModel userSongsModel = new DefaultListModel();
		for (Song userSong: userSongs) {
			userSongsModel.addElement(userSong.getSongName());
		}
		this.userSongsList.setModel(userSongsModel);
		
	}
	
	public void setCurrentSongIndex (int index) {
		((TransparentListCellRenderer) this.playlist.getCellRenderer()).setCurrentSongIndex(index);
		this.currentSongLabel.setText(this.controller.getCurrentSongName());
		this.playlist.updateUI();
	}
	
	public void askUserForGenreName (Song song) {
		
		String songName = song.getSongName();
			
		//criamos uma popUp e adicionamos botoes para recebermos input do usuario
		JDialog popUp = new JDialog(frame);
			
		JLabel labelAskingForInput = new JLabel("<html>Nao conseguimos identificar o estilo de <br>\'" + songName + "\'<br> Se quiser defini-lo, digite-o abaixo:</html>");
		labelAskingForInput.setBounds(10,0,300,70);
		
		JTextArea genreName = new JTextArea();
		genreName.setBounds(10,70,150,25);
		
		JButton send = new JButton("Enviar");
		send.setBounds(50,115,150,30);
		send.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
				song.setSongGenre(genreName.getText());
				controller.addSongToUserSongsList(song);
		                popUp.dispose();
				System.out.println(song.getSongName() + " " + genreName.getText());
            }	
        
		 });
		 
		popUp.add(labelAskingForInput);
		popUp.add(genreName);
		popUp.add(send);
			
	    popUp.setSize(300,200);
		popUp.getContentPane().setLayout(null);
		popUp.setResizable(false);
		popUp.setVisible(true);
		popUp.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		popUp.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				if (song.getSongGenre().equals("Indefinido")) {
			        controller.addSongToUserSongsList(song);
		        }
			}
		
		});
	
	}
	
}
