package model.protocol;

import org.json.JSONObject;

import model.protocol.queries.*;
import model.socket.SocketThread;

public class QueryManager<T extends SocketThread> {
	public final boolean onQuery(JSONObject json, T socket) {
        String action = json.getString(Query.ACTION);

        if (LoginSaltQuery.ACTION.equals(action))       return onReceiveLoginSalt(new LoginSaltQuery(json), socket);
        if (LoginQuery.ACTION.equals(action))           return onReceiveLogin(new LoginQuery(json), socket);

        if (GoToRoomQuery.ACTION.equals(action))        return onReceiveGoToRoom(new GoToRoomQuery(json), socket);
        if (RoomListQuery.ACTION.equals(action))        return onReceiveRoomList(new RoomListQuery(json), socket);

        if (ChoseRoleQuery.ACTION.equals(action))       return onReceiveChoseRole(new ChoseRoleQuery(json), socket);
        if (PlayerListQuery.ACTION.equals(action))        return onReceivePlayerList(new PlayerListQuery(json), socket);

        if (GameStateQuery.ACTION.equals(action))       return onReceiveGameState(new GameStateQuery(json), socket);
        if (AgentMovementQuery.ACTION.equals(action))   return onReceiveAgentMovement(new AgentMovementQuery(json), socket);
        if (CosmeticsQuery.ACTION.equals(action))   return onReceiveCosmeticsQuery(new CosmeticsQuery(json), socket);
        
        if (InfosQuery.ACTION.equals(action))           return socket.onReceiveInfos(new InfosQuery(json));
        if (StopQuery.ACTION.equals(action))            return socket.onReceiveStop(new StopQuery());

        return true;
    }

    protected boolean onReceiveLoginSalt(LoginSaltQuery q, T socket) { return true; }
    protected boolean onReceiveLogin(LoginQuery q, T socket) { return true; }


    protected boolean onReceiveGoToRoom(GoToRoomQuery q, T socket) { return true; }
    protected boolean onReceiveRoomList(RoomListQuery q, T socket) { return true; }

    protected boolean onReceiveChoseRole(ChoseRoleQuery q, T socket) { return true; }
    protected boolean onReceivePlayerList(PlayerListQuery q, T socket) { return true; }

    protected boolean onReceiveGameState(GameStateQuery q, T socket) { return true; }
    protected boolean onReceiveAgentMovement(AgentMovementQuery q, T socket) { return true; }
    protected boolean onReceiveCosmeticsQuery(CosmeticsQuery q, T socket) { return true; }
}
