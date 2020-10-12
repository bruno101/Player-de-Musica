package Controller;

import java.io.*;
import javazoom.jl.player.Player;

//codigo da classe baseado no que encontrei em "https://stackoverflow.com/questions/12057214/jlayer-pause-and-resume-song/23937027"
//a classe permite que seja possivel pausar musicas

public class PausablePlayer {

    private final static int NOTSTARTED = 0;
    private final static int PLAYING = 1;
    private final static int PAUSED = 2;
    private final static int FINISHED = 3;

    // player fazendo todo o trabalo
    private final Player player;

    // objecto de locking usado para se comunicar com o thread do player
    private final Object playerLock = new Object();

    // variavel de status do que o player esta fazendo e deve fazer
    private int playerStatus = NOTSTARTED;
	
	Controller controller;

    public PausablePlayer(final InputStream inputStream, Controller controller) throws Exception {
        this.player = new Player(inputStream);
		this.controller = controller;
    }

    /**
     * Comeca o playback - recomeca se estiver pausado
     */
    public void play() throws Exception {
        synchronized (playerLock) {
            switch (playerStatus) {
                case NOTSTARTED:
                    final Runnable r = new Runnable() {
                        public void run() {
                            playInternal();
                        }
                    };
                    final Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setPriority(Thread.MAX_PRIORITY);
                    playerStatus = PLAYING;
                    t.start();
                    break;
                case PAUSED:
                    resume();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Pausa playback. Retorna true se novo estado eh PAUSED.
     */
    public boolean pause() {
        synchronized (playerLock) {
            if (playerStatus == PLAYING) {
                playerStatus = PAUSED;
            }
            return playerStatus == PAUSED;
        }
    }

    /**
     * Recomeca playblack. Retorna true se novo estado eh PLAYING.
     */
    public boolean resume() {
        synchronized (playerLock) {
            if (playerStatus == PAUSED) {
                playerStatus = PLAYING;
                playerLock.notifyAll();
            }
            return playerStatus == PLAYING;
        }
    }

    /**
     * Para playback. Se nao estiver tocando, nao faz nada.
     */
    public void stop() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
            playerLock.notifyAll();
        }
    }

    private void playInternal() {
        while (playerStatus != FINISHED) {
            try {
                if (!player.play(1)) {
                    break;
                }
            } catch (final Exception e) {
                break;
            }
            // checa se pausado ou terminado
            synchronized (playerLock) {
                while (playerStatus == PAUSED) {
                    try {
                        playerLock.wait();
                    } catch (final InterruptedException e) {
                        // termina o player
                        break;
                    }
                }
            }
			
        }
		if (playerStatus != FINISHED) {
			System.out.println("END OF PLAY INTERNAL");
		    controller.goForward();
		} 
		close();
    }

    /**
     * Fecha o player, independentemente do estado atual
     */
    public void close() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
        }
        try {
            player.close();
        } catch (final Exception e) {
            // ignoramos a excecao, pois estamos terminando de qualquer forma
        }
    }

}