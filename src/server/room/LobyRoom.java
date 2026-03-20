package server.room;

import java.util.ArrayList;

import model.protocol.queries.GoToRoomQuery;
import model.protocol.queries.RoomListQuery;
import server.socket.RoomSocketThread;

public class LobyRoom extends Room {
    public static ArrayList<GameRoom> rooms = new ArrayList<>();

    public LobyRoom(String name) {
        super(name);

        for (int index = 0; index < 128; index++) {
            rooms.add(new GameRoom(this));
        }
    }
    protected void removeEmptyRooms() {
        // ListIterator<GameRoom> iter = rooms.listIterator();
        // while(iter.hasNext()){
        //     if(iter.next().isEmpty()){
        //         iter.remove();
        //     }
        // }
    }
    protected void sendRoomList(RoomSocketThread socket) {
        RoomListQuery query = new RoomListQuery();
        for (GameRoom room : rooms) {
            query.fillAnswer(room.toRoomInfo());
        }
        query.send(socket);
    }
    protected void sendRoomList() {
        RoomListQuery query = new RoomListQuery();
        for (GameRoom room : rooms) {
            query.fillAnswer(room.toRoomInfo());
        }
        sendToAll(query);
    }

    @Override
    protected boolean onReceiveRoomList(RoomListQuery query, RoomSocketThread socket) {
        removeEmptyRooms();
        sendRoomList(socket);
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
        removeEmptyRooms();
        sendRoomList(socket);
    }
}
