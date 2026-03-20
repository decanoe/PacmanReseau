package server.room;

import java.util.ArrayList;
import java.util.ListIterator;

import model.protocol.queries.GoToRoomQuery;
import model.protocol.queries.RoomListQuery;
import server.socket.RoomSocketThread;

public class LobbyRoom extends Room {
    public static ArrayList<GameRoom> rooms = new ArrayList<>();

    public LobbyRoom(String name) {
        super(name);
    }
    protected void removeEmptyRooms() {
        ListIterator<GameRoom> iter = rooms.listIterator();
        while(iter.hasNext()){
            if(iter.next().isEmpty()){
                iter.remove();
            }
        }
    }
    protected RoomListQuery createRoomListQuery() {
        removeEmptyRooms();
        RoomListQuery query = new RoomListQuery();
        for (GameRoom room : rooms) {
            query.fillAnswer(room.toRoomInfo());
        }
        return query.fillAnswer();
    }
    protected void sendRoomList(RoomSocketThread socket) {
        createRoomListQuery().send(socket);
    }
    protected void sendRoomList() {
        sendToAll(createRoomListQuery());
    }

    @Override
    protected boolean onReceiveRoomList(RoomListQuery query, RoomSocketThread socket) {
        sendRoomList(socket);
        return true;
    }
    @Override
    protected boolean onReceiveGoToRoom(GoToRoomQuery query, RoomSocketThread socket) {
        if (query.toLobby()) {
            query.fillDenie().send(socket);
            return true;
        }
        if (query.toNewRoom()) {
            GameRoom new_room = new GameRoom(this);
            rooms.add(new_room);
            query.fillAccept(new_room.getName()).send(socket);
            socket.setRoom(new_room);
            sendRoomList();
            return true;
        }

        for (Room room : rooms) {
            if (room.getName().equals(query.getRoomName())) {
                socket.setRoom(room);

                query.fillAccept().send(socket);
                return true;
            }
        }

        query.fillDenie().send(socket);
        sendRoomList(socket);
        return true;
    }

    @Override
    protected void onRoomEnter(RoomSocketThread socket) {
        sendRoomList(socket);
    }
}
