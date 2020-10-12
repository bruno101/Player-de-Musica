Para executar no Windows na cmd:
Mude o diretório para o do projeto ( C:\...\Plano B). Digite:
javac -d bin -cp "lib/*" src/Controller/Controller.java src/View/View.java src/View/TransparentListCellRenderer.java src/View/UserSongsListCellRenderer.java src/Model/Song.java src/Model/Playlist.java src/Controller/PausablePlayer.java
java -cp bin -cp "bin;lib/*" Controller.Controller

Em outro sistema operacional eu nao testei, mas imagino que a unica mudanca seja substituir o ';' no comando de execucao por ':'.

Observação: Na compilaçao podem aparecer 'warning's, mas nao tem problema. Na execucao podem aparecer 'NullPointerException's misteriosas envolvendo o Swing aleatoriamente, mas elas parecem nao afetar em nada a execucao do programa. # Player-de-Musica
