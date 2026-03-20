package server.room;

import model.protocol.data.PlayerInfo;
import model.protocol.data.RoomInfo;
import model.protocol.queries.AgentMovementQuery;
import model.protocol.queries.ChoseRoleQuery;
import model.protocol.queries.CosmeticsQuery;
import model.protocol.queries.GameStateQuery;
import model.protocol.queries.GoToRoomQuery;
import model.protocol.queries.PlayerListQuery;
import server.socket.RoomSocketThread;
import server.web_interface.WebInterface;

public class GameRoom extends Room {
    private static int ID_GENERATOR = 0;
    LobbyRoom lobby;

    GameRoomState state;

    public GameRoom(LobbyRoom lobby) {
        super("GameRoom-" + ID_GENERATOR++);
        this.lobby = lobby;

        this.state = new GameRoomRoleState(this);
    }

    public RoomInfo toRoomInfo() {
        return new RoomInfo(this.name, this.nbPlayers());
    }
    public void setState(GameRoomState state) {
        this.state = state;
    }

    protected PlayerListQuery createPlayerListQuery() {
        PlayerListQuery query = new PlayerListQuery();
        for (RoomSocketThread socket : this.opened_sokets) {
            query.fillAnswer(new PlayerInfo(socket.getPlayerLogin(), state.getPlayerChoice(socket)));
        }
        return query.fillAnswer();
    }
    protected void sendPlayerList(RoomSocketThread socket) {
        createPlayerListQuery().send(socket);
    }
    protected void sendPlayerList() {
        sendToAll(createPlayerListQuery());
    }

    @Override
    protected void onRoomExit(RoomSocketThread socket) {
        state.onPlayerLeave(socket);
    }
    @Override
    protected void onRoomEnter(RoomSocketThread socket) {
        sendPlayerList();
    }

    @Override
    protected boolean onReceiveGoToRoom(GoToRoomQuery query, RoomSocketThread socket) {
        if (query.toLobby()) {
            query.fillAccept().send(socket);
            socket.setRoom(lobby);
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
    protected boolean onReceivePlayerList(PlayerListQuery query, RoomSocketThread socket) {
        sendPlayerList(socket);
        return true;
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
