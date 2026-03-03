package server.room;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.protocol.Query;
import model.protocol.QueryManager;
import server.socket.RoomSocketThread;

public abstract class Room extends QueryManager<RoomSocketThread> {
    protected ArrayList<RoomSocketThread> opened_sokets = new ArrayList<>();
    protected String name;

    public Room(String name) {
        this.name = name;
    }
    public void addSoket(RoomSocketThread socket) {
        this.opened_sokets.add(socket);
        onRoomEnter(socket);
    }
    public void removeSoket(RoomSocketThread socket) {
        this.opened_sokets.remove(socket);
        onRoomExit(socket);
    }

    public int nbPlayers() {
        return opened_sokets.size();
    }
    public Boolean isEmpty() {
        return nbPlayers() == 0;
    }

    public void clearUnusedSokets() {
        int i = 0;
        while (i < opened_sokets.size()) {
            if (!opened_sokets.get(i).isValid()) {
                opened_sokets.remove(i);
            }
            else i++;
        }
    }
    public void sendToAll(Query query) {
        clearUnusedSokets();
        for (RoomSocketThread socket : opened_sokets) {
            query.send(socket);
        }
    }

    public String getName() {
        return name;
    }
    public String getDebugName() {
        return getName();
    }
    public JSONObject getJson() {
        JSONObject obj = new JSONObject();
        obj.put("name", getName());
        obj.put("nb_players", opened_sokets.size());

        JSONArray players = new JSONArray();
        for (RoomSocketThread socket : opened_sokets) {
            players.put(socket.getPlayerName());
        }
        obj.put("players", players);
        return obj;
    }
    
    protected void onRoomEnter(RoomSocketThread socket) {}
    protected void onRoomExit(RoomSocketThread socket) {}
}
