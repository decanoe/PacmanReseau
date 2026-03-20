package model.protocol.queries;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.protocol.Query;
import model.protocol.data.PlayerInfo;

public final class PlayerListQuery extends Query {
    private static final class TOKEN {
        public static final String PLAYERS = "players";
    }

    protected ArrayList<PlayerInfo> players;
    public PlayerListQuery() { this.players = new ArrayList<>(); }
    public PlayerListQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            players = new ArrayList<>();
            JSONArray array = json.getJSONArray(TOKEN.PLAYERS);

            for (int i = 0; i < array.length(); i++) {
                players.add(new PlayerInfo(array.getJSONObject(i)));
            }
        }
    }

    public static final String ACTION = "players_infos";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            JSONArray array = new JSONArray();

            for (PlayerInfo info : players) {
                array.put(info.toJson());
            }

            json.put(TOKEN.PLAYERS, array);
        }

        return json;
    }

    public PlayerListQuery fillAnswer() {
        this.setAnswer();
        return this;
    }
    public PlayerListQuery fillAnswer(PlayerInfo info) {
        this.setAnswer();
        this.players.add(info);
        
        return this;
    }

    public ArrayList<PlayerInfo> getPlayersInfos() {
        return players;
    }
}
