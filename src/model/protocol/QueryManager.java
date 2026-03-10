package model.protocol;

import org.json.JSONObject;

import model.protocol.Queries.*;
import model.socket.SocketThread;

public class QueryManager<T extends SocketThread> {
	public final boolean onQuery(JSONObject json, T socket) {
        String action = json.getString(Query.ACTION);

        if (ChoseRoleQuery.ACTION.equals(action))       return onReceiveChoseRole(new ChoseRoleQuery(json), socket);
        if (GameStateQuery.ACTION.equals(action))       return onReceiveGameState(new GameStateQuery(json), socket);
        if (GoToRoomQuery.ACTION.equals(action))        return onReceiveGoToRoom(new GoToRoomQuery(json), socket);
        if (RoomListQuery.ACTION.equals(action))        return onReceiveRoomList(new RoomListQuery(json), socket);
        if (AgentMovementQuery.ACTION.equals(action))   return onReceiveAgentMovement(new AgentMovementQuery(json), socket);
        
        if (InfosQuery.ACTION.equals(action))       return socket.onReceiveInfos(new InfosQuery(json));
        if (StopQuery.ACTION.equals(action))        return socket.onReceiveStop(new StopQuery());

        return true;
    }

    protected boolean onReceiveChoseRole(ChoseRoleQuery q, T socket) { return true; }
    protected boolean onReceiveGameState(GameStateQuery q, T socket) { return true; }
    protected boolean onReceiveGoToRoom(GoToRoomQuery q, T socket) { return true; }
    protected boolean onReceiveRoomList(RoomListQuery q, T socket) { return true; }
    protected boolean onReceiveAgentMovement(AgentMovementQuery q, T socket) { return true; }
}
