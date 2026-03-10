package server.room;

import java.util.HashMap;

import model.protocol.Queries.ChoseRoleQuery;
import model.protocol.Queries.GameStateQuery;
import server.socket.RoomSocketThread;

public class GameRoomRoleState extends GameRoomState {
    HashMap<RoomSocketThread, ChoseRoleQuery.Choice> choices;
    
    public GameRoomRoleState(GameRoom room) {
        super(room);
        this.choices = new HashMap<>();

        room.sendToAll(new GameStateQuery().fillAnswerNotRunning());
    }

    protected void checkReady() {
        for (RoomSocketThread socket : room.opened_sokets) {
            if (choices.getOrDefault(socket, ChoseRoleQuery.Choice.None) == ChoseRoleQuery.Choice.None) return;
        }

        launchGame();
    }
    protected void launchGame() {
        try {
            if (room.state == this) room.setState(new GameRoomPlayState(room));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    protected void onPlayerLeave(RoomSocketThread socket) {
        choices.remove(socket);
        checkReady();
    }
    @Override
    protected boolean onReceiveGameState(GameStateQuery query, RoomSocketThread socket) {
        query.fillAnswerNotRunning().send(socket);
        return true;
    }
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, RoomSocketThread socket) {
        choices.put(socket, query.getChoice());
        query.fillAccept().send(socket);
        checkReady();
        return true;
    }
}
