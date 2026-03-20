package model.protocol.queries;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.protocol.Query;
import model.protocol.data.RoomInfo;

public final class RoomListQuery extends Query {
    private static final class TOKEN {
        public static final String ROOMS = "rooms";
    }

    protected ArrayList<RoomInfo> rooms;
    public RoomListQuery() { this.rooms = new ArrayList<>(); }
    public RoomListQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            rooms = new ArrayList<>();
            JSONArray array = json.getJSONArray(TOKEN.ROOMS);

            for (int i = 0; i < array.length(); i++) {
                rooms.add(new RoomInfo(array.getJSONObject(i)));
            }
        }
    }

    public static final String ACTION = "rooms_infos";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            JSONArray array = new JSONArray();

            for (RoomInfo info : rooms) {
                array.put(info.toJson());
            }

            json.put(TOKEN.ROOMS, array);
        }

        return json;
    }

    public RoomListQuery fillAnswer(RoomInfo info) {
        this.setAnswer();
        this.rooms.add(info);
        
        return this;
    }

    public ArrayList<RoomInfo> getRoomsInfos() {
        return rooms;
    }
}
