package server.room;

import model.protocol.data.RoomInfo;
import model.protocol.queries.AgentMovementQuery;
import model.protocol.queries.ChoseRoleQuery;
import model.protocol.queries.CosmeticsQuery;
import model.protocol.queries.GameStateQuery;
import model.protocol.queries.GoToRoomQuery;
import server.socket.RoomSocketThread;
import server.web_interface.WebInterface;

public class GameRoom extends Room {
    private static int ID_GENERATOR = 0;
    LobyRoom loby;

    GameRoomState state;

    public GameRoom(LobyRoom loby) {
        super("GameRoom-" + ID_GENERATOR++);
        this.loby = loby;

        this.state = new GameRoomRoleState(this);
    }

    public RoomInfo toRoomInfo() {
        return new RoomInfo(this.name, this.nbPlayers());
    }
    public void setState(GameRoomState state) {
        this.state = state;
    }

    @Override
    protected void onRoomExit(RoomSocketThread socket) {
        state.onPlayerLeave(socket);
    }

    @Override
    protected boolean onReceiveGoToRoom(GoToRoomQuery query, RoomSocketThread socket) {
        if (query.toLoby()) {
            query.fillAccept().send(socket);
            socket.setRoom(loby);
        }
        else {
            query.fillDenie().send(socket);
        }
        return true;
    }
    @Override
    protected boolean onReceiveGameState(GameStateQuery query, RoomSocketThread socket) {
        return state.onReceiveGameState(query, socket);
    }
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, RoomSocketThread socket) {
        return state.onReceiveChoseRole(query, socket);
    }
    @Override
    protected boolean onReceiveAgentMovement(AgentMovementQuery query, RoomSocketThread socket) {
        return state.onReceiveAgentMovement(query, socket);
    }
    @Override
    protected boolean onReceiveCosmeticsQuery(CosmeticsQuery query, RoomSocketThread socket) {
        query.fillAnswer(WebInterface.getActiveCosmetics(socket.getPlayerLogin())).send(socket);
        return true;
    }
}
