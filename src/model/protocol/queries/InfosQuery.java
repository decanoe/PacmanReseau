package model.protocol.queries;

import org.json.JSONObject;

import model.protocol.Query;
import model.socket.SocketThread;

public final class InfosQuery extends Query {
    private static final class TOKEN {
        public static final String NAME = "name";
    }

    protected String name;
    public InfosQuery() {}
    public InfosQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            this.name = json.getString(TOKEN.NAME);
        }
    }

    public static final String ACTION = "infos";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            json.put(TOKEN.NAME, name);
        }

        return json;
    }

    public InfosQuery fillAnswer(SocketThread socket) {
        super.setAnswer();
        this.name = socket.getPlayerLogin();
        
        return this;
    }

    public String getName() {
        return name;
    }
}
