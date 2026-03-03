package model.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

import model.protocol.Queries.InfosQuery;
import model.protocol.Queries.StopQuery;

public abstract class SocketThread extends Thread {
    protected String name;
    protected Socket socket;
	protected BufferedReader entree;
	protected PrintWriter sortie;

    public SocketThread(Socket socket, String name) {
        this.name = name;
        this.socket = socket;
        try {
            entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sortie = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("problème à la création d'un socket\n\t"+e);
        }
    }
    public SocketThread(Socket socket) {
        this(socket, null);

        new InfosQuery().send(this);
    }

    public boolean isValid() {
        return socket.isConnected() && !socket.isClosed();
    }
    public String getPlayerName() {
        return name;
    }

    public void send(JSONObject json) {
        if (!isValid()) return;
        
        sortie.println(json.toString());
        println("a envoyé : |"+json.toString()+"|");
    }
    public void sendStop() {
        if (!isValid()) return;

        JSONObject stop_json = new JSONObject();
        stop_json.put("action", "stop");
        send(stop_json);
    }
    
    protected abstract boolean onQuery(JSONObject json);
    public boolean onReceiveInfos(InfosQuery query) {
        if (query.isAnswer()) {
            this.name = query.getName();
        }
        else {
            query.fillAnswer(this).send(this);
        }
        return true;
    }
    public boolean onReceiveStop(StopQuery query) {
        return false;
    }
    
    public abstract void onConnectionStart();
    public abstract void onConnectionEnd();

    public void run() {
        try {
            String json;
            do {
                json = entree.readLine(); // on lit ce qui arrive

                if (json == null) {
                    println("fermeture prématurée du socket (perte de connection)");
                    break;
                }
                println("a reçu : |"+json+"|");
            } while (onQuery(new JSONObject(json)));

            sendStop();
            socket.close();
        } catch (Exception e) {
            println("problème dans un socket\n\t"+e);
        }

        if (!socket.isClosed()) {
            try {
                sendStop();
                socket.close();
            } catch (Exception e) {
                println("problème fermeture de socket de prévention\n\t"+e);
            }
        }
        println("fermeture de la connexion");
        onConnectionEnd();
    }
    protected void print(String string) {
        System.out.print(name + "\t(" + Thread.currentThread().getName() + ") " + string);
    }
    protected void println(String string) {
        print(string + "\n");
    }
}
