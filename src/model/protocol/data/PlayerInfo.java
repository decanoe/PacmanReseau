package model.protocol.data;

import org.json.JSONObject;

import model.protocol.queries.ChoseRoleQuery.Choice;

public class PlayerInfo {
    private static final class TOKEN {
        public static final String NAME = "name";
        public static final String CHOICE = "choice";
    }

    public String name;
    public Choice choice;

    public PlayerInfo(String name, Choice choice) {
        this.name = name;
        this.choice = choice;
    }
    public PlayerInfo(JSONObject json) {
        this.name = json.getString(TOKEN.NAME);
        this.choice = Choice.valueOf(json.getString(TOKEN.CHOICE));
    }
    public JSONObject toJson() {
        JSONObject result = new JSONObject();

        result.put(TOKEN.NAME, name);
        result.put(TOKEN.CHOICE, choice.toString());

        return result;
    }
}
