Para executar no Windows na cmd:
Mude o diretório para o do projeto ( C:\...\Player-de-Musica-main). Digite:

javac -d bin -cp "lib/*" src/controller/Controller.java src/view/View.java src/view/TransparentListCellRenderer.java src/view/UserSongsListCellRenderer.java src/model/Song.java src/model/Playlist.java src/model/PausablePlayer.java

java -cp bin -cp "bin;lib/*" controller.Controller

Se aparecer 'directory not found: bin' é porque é necessário criar dentro de 'Player-de-Musica-main' uma pasta 'bin'.

Em outro sistema operacional eu nao testei, mas imagino que a unica mudanca seja substituir o ';' no comando de execucao por ':'.

Observação: Na compilaçao podem aparecer 'warning's, mas nao tem problema. Na execucao podem aparecer 'NullPointerException's misteriosas envolvendo o Swing aleatoriamente, mas elas parecem nao afetar em nada a execucao do programa. # Player-de-Musica
