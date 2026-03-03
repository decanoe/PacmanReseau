package model.protocol.Queries;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.protocol.Query;
import server.room.GameRoom;
import server.room.Room;

public final class RoomListQuery extends Query {
    private static final class TOKEN {
        public static final String ROOMS = "rooms";
    }

    protected JSONArray array;
    public RoomListQuery() {}
    public RoomListQuery(ArrayList<GameRoom> rooms) {
        fillAnswer(rooms);
    }
    public RoomListQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            this.array = json.getJSONArray(TOKEN.ROOMS);
        }
    }

    public static final String ACTION = "rooms_infos";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            json.put(TOKEN.ROOMS, array);
        }

        return json;
    }

    public RoomListQuery fillAnswer(ArrayList<GameRoom> rooms) {
        array = new JSONArray();
        for(Room room: rooms){
            array.put(room.getJson());
        }
        this.setAnswer();
        
        return this;
    }

    public JSONArray getRoomsInfos() {
        return array;
    }
}
