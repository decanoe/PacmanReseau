package model.protocol.data;

import org.json.JSONObject;

public class RoomInfo {
    private static final class TOKEN {
        public static final String NAME = "name";
        public static final String PLAYER_COUNT = "player_count";
    }

    public String name;
    public int player_count;

    public RoomInfo(String name, int player_count) {
        this.name = name;
        this.player_count = player_count;
    }
    public RoomInfo(JSONObject json) {
        this.name = json.getString(TOKEN.NAME);
        this.player_count = json.getInt(TOKEN.PLAYER_COUNT);
    }
    public JSONObject toJson() {
        JSONObject result = new JSONObject();

        result.put(TOKEN.NAME, name);
        result.put(TOKEN.PLAYER_COUNT, player_count);

        return result;
    }
}
