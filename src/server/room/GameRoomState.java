package server.room;

import model.protocol.Queries.AgentMovementQuery;
import model.protocol.Queries.ChoseRoleQuery;
import model.protocol.Queries.GameStateQuery;
import server.socket.RoomSocketThread;

public abstract class GameRoomState {
    GameRoom room;

    public GameRoomState(GameRoom room) {
        this.room = room;
    }

    protected abstract void onPlayerLeave(RoomSocketThread socket);

    protected abstract boolean onReceiveChoseRole(ChoseRoleQuery query, RoomSocketThread socket);
    protected abstract boolean onReceiveGameState(GameStateQuery query, RoomSocketThread socket);
    protected abstract boolean onReceiveAgentMovement(AgentMovementQuery query, RoomSocketThread socket);
}
