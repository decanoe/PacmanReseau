package server.room;

import java.util.HashMap;

import model.protocol.queries.AgentMovementQuery;
import model.protocol.queries.ChoseRoleQuery;
import model.protocol.queries.ChoseRoleQuery.Choice;
import model.protocol.queries.FullMazeQuery;
import model.protocol.queries.GameStateQuery;
import model.protocol.queries.GameStateQuery.WinState;
import server.socket.RoomSocketThread;

public class GameRoomRoleState extends GameRoomState {
    HashMap<RoomSocketThread, ChoseRoleQuery.Choice> choices;
    
    public GameRoomRoleState(GameRoom room) {
        super(room);
        this.choices = new HashMap<>();

        room.sendToAll(new GameStateQuery().fillAnswerNotRunning(WinState.None));
    }

    protected void checkReady() {
        if (room.opened_sokets.isEmpty()) return;

        for (RoomSocketThread socket : room.opened_sokets) {
            if (choices.getOrDefault(socket, ChoseRoleQuery.Choice.None) == ChoseRoleQuery.Choice.None) return;
        }

        launchGame();
    }
    protected void launchGame() {
        try {
            if (room.state == this) room.setState(new GameRoomPlayState(room, choices));
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
    protected Choice getPlayerChoice(RoomSocketThread socket) {
        if (!choices.containsKey(socket)) return Choice.None;
        return choices.get(socket);
    }

    @Override
    protected boolean onReceiveGameState(GameStateQuery query, RoomSocketThread socket) {
        query.fillAnswerNotRunning(WinState.None).send(socket);
        return true;
    }
    @Override
    protected boolean onReceiveFullMaze(FullMazeQuery query, RoomSocketThread socket) {
        return true;
    }
    
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, RoomSocketThread socket) {
        choices.put(socket, query.getChoice());
        query.fillAccept().send(socket);
        room.sendPlayerList();
        checkReady();
        return true;
    }
    @Override
    protected boolean onReceiveAgentMovement(AgentMovementQuery query, RoomSocketThread socket) {
        return true;
    }
}
