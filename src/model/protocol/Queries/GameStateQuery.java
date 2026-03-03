package model.protocol.Queries;

import org.json.JSONObject;

import model.protocol.Query;

public final class GameStateQuery extends Query {
    private static final class TOKEN {
        public static final String RUNNING = "running";
    }

    protected boolean running;
    public GameStateQuery() {}
    public GameStateQuery(boolean running) {
        this.running = running;
        this.setAnswer();
    }
    public GameStateQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            this.running = json.getBoolean(TOKEN.RUNNING);
        }
    }

    public static final String ACTION = "get_game_state";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            json.put(TOKEN.RUNNING, running);
        }

        return json;
    }

    public GameStateQuery fillAnswer(boolean running) {
        super.setAnswer();
        this.running = running;
        
        return this;
    }

    public Boolean isGameRunning() {
        return running;
    }
}
