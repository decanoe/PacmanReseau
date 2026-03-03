package server.room;

import java.util.ArrayList;
import java.util.ListIterator;

import model.protocol.Queries.GoToRoomQuery;
import model.protocol.Queries.RoomListQuery;
import server.socket.RoomSocketThread;

public class LobyRoom extends Room {
    public static ArrayList<GameRoom> rooms = new ArrayList<>();

    public LobyRoom(String name) {
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
    protected void sendRoomList(RoomSocketThread socket) {
        new RoomListQuery(rooms).send(socket);
    }

    @Override
    protected boolean onReceiveRoomList(RoomListQuery query, RoomSocketThread socket) {
        removeEmptyRooms();
        query.fillAnswer(rooms).send(socket);
        return true;
    }
    @Override
    protected boolean onReceiveGoToRoom(GoToRoomQuery query, RoomSocketThread socket) {
        if (query.toLoby()) {
            query.fillDenie().send(socket);
            return true;
        }
        if (query.toNewRoom()) {
            GameRoom new_room = new GameRoom(this);
            rooms.add(new_room);
            socket.setRoom(new_room);
            query.fillAccept(new_room.getName()).send(socket);
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
        removeEmptyRooms();
        sendRoomList(socket);
    }
}
