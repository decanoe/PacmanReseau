package server.room;

import model.protocol.queries.AgentMovementQuery;
import model.protocol.queries.ChoseRoleQuery;
import model.protocol.queries.ChoseRoleQuery.Choice;
import model.protocol.queries.FullMazeQuery;
import model.protocol.queries.GameStateQuery;
import server.socket.RoomSocketThread;

public abstract class GameRoomState {
    GameRoom room;

    public GameRoomState(GameRoom room) {
        this.room = room;
    }

    protected abstract void onPlayerLeave(RoomSocketThread socket);

    protected abstract Choice getPlayerChoice(RoomSocketThread socket);

    protected abstract boolean onReceiveChoseRole(ChoseRoleQuery query, RoomSocketThread socket);
    protected abstract boolean onReceiveFullMaze(FullMazeQuery query, RoomSocketThread socket);
    protected abstract boolean onReceiveGameState(GameStateQuery query, RoomSocketThread socket);
    protected abstract boolean onReceiveAgentMovement(AgentMovementQuery query, RoomSocketThread socket);
}
