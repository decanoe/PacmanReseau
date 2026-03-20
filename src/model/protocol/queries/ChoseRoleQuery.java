package model.protocol.queries;

import org.json.JSONObject;

import model.protocol.QueryClaim;

public final class ChoseRoleQuery extends QueryClaim {
    private static final class TOKEN {
        public static final String CHOICE = "choice";
    }
    
    public enum Choice { None, Pacman, Ghost };
    protected Choice choice = Choice.None;

    public ChoseRoleQuery(Choice choice) {
        this.choice = choice;
    }
    public ChoseRoleQuery(JSONObject json) {
        super(json);
        
        this.choice = Choice.valueOf(json.getString(TOKEN.CHOICE));
    }

    public static final String ACTION = "chose_role";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        json.put(TOKEN.CHOICE, choice.toString());

        return json;
    }

    public Choice getChoice () {
        return choice;
    }
}
