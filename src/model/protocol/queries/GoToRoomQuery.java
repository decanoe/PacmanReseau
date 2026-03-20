package model.protocol.queries;

import org.json.JSONObject;

import model.protocol.QueryClaim;

public final class GoToRoomQuery extends QueryClaim {
    private static final class TOKEN {
        public static final String ROOM_TYPE = "room_type";
        public static final String NAME = "room_name";
    }

    public enum RoomType { Old, New, Loby }
    protected RoomType roomType = RoomType.Old;
    protected String room_name = null;

    public GoToRoomQuery(RoomType roomType) {
        this.roomType = roomType;
    }
    public GoToRoomQuery(String room_name) {
        this.roomType = RoomType.Old;
        this.room_name = room_name;
    }
    public GoToRoomQuery(JSONObject json) {
        super(json);
        
        this.roomType = RoomType.valueOf(json.getString(TOKEN.ROOM_TYPE));

        if (roomType == RoomType.Old || (roomType == RoomType.New && isAnswer())) this.room_name = json.getString(TOKEN.NAME);
    }

    public static final String ACTION = "go_to_room";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        json.put(TOKEN.ROOM_TYPE, roomType.toString());
        if (room_name != null) json.put(TOKEN.NAME, room_name);

        return json;
    }

    public QueryClaim fillAccept(String new_room_name) {
        this.room_name = new_room_name;
        return super.fillAccept();
    }

    public RoomType getRoomType () {
        return roomType;
    }
    public Boolean toNewRoom () {
        return roomType == RoomType.New;
    }
    public Boolean toLoby () {
        return roomType == RoomType.Loby;
    }
    public String getRoomName() {
        return room_name;
    }
}
