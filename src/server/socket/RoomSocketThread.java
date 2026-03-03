package server.socket;

import java.net.Socket;

import org.json.JSONObject;

import model.protocol.QueryManager;
import model.socket.SocketThread;
import server.room.Room;

public class RoomSocketThread extends SocketThread {
    Room room;
    
    public RoomSocketThread(Socket socket, Room room) {
        super(socket);
        setRoom(room);
    }
    public void setRoom(Room room) {
        if (this.room != null) this.room.removeSoket(this);
        this.room = room;
        room.addSoket(this);
    }

    @Override
    protected boolean onQuery(JSONObject json) {
        if (this.room != null) return room.onQuery(json, this);
        return new QueryManager<RoomSocketThread>().onQuery(json, this);
    }
    @Override
    public void onConnectionStart() { }
    @Override
    public void onConnectionEnd() { room.removeSoket(this); }

    @Override
    protected void print(String string) {
        if (room != null)
            System.out.print(name + "\t(" + Thread.currentThread().getName() + " / " + room.getName() + ") " + string);
        else super.print(string);
    }
}
