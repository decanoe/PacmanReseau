package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.room.LobyRoom;
import server.socket.RoomSocketThread;

public class MainServer {
    public static LobyRoom loby = new LobyRoom("Loby");

	public static void main(String[] args) {
		int p = 2000; // le port d’écoute
		ServerSocket ecoute;

        try {
            ecoute = new ServerSocket(p); // on crée le serveur
            System.out.println("serveur mis en place ");
            while (true) {// le serveur va attendre qu’une connexion arrive
		        Socket so = ecoute.accept();
                System.out.println("Nouvelle connection entrante");

                new RoomSocketThread(so, loby).start();
            }
        } catch (IOException e) { System.out.println("problème dans le main thread\n\t"+e); }
	}
}