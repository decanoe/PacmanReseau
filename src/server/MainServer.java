package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import server.room.LobyRoom;
import server.room.LoginRoom;
import server.socket.RoomSocketThread;

public class MainServer {
    public static ArrayList<RoomSocketThread> players = new ArrayList<>();

    public static LobyRoom loby = new LobyRoom("Loby");
    public static LoginRoom login = new LoginRoom(loby);

	public static void main(String[] args) {
		int p = 2000; // le port d’écoute
		ServerSocket ecoute;

        try {
            ecoute = new ServerSocket(p); // on crée le serveur
            System.out.println("serveur mis en place ");
            while (true) {// le serveur va attendre qu’une connexion arrive
		        Socket so = ecoute.accept();
                System.out.println("Nouvelle connection entrante");

                new RoomSocketThread(so, login).start();
            }
        } catch (IOException e) { System.out.println("problème dans le main thread\n\t"+e); }
	}
}