package model.protocol.queries;

import org.json.JSONObject;

import model.protocol.Query;

public final class GameStateQuery extends Query {
    private static final class TOKEN {
        public static final String RUNNING = "running";
        public static final String WINSTATE = "win_state";
    }
    public enum WinState { Pacman, Ghost, None };

    protected WinState state = WinState.None;
    protected boolean running;
    public GameStateQuery() {}
    public GameStateQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            this.state = WinState.valueOf(json.getString(TOKEN.WINSTATE));
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
            json.put(TOKEN.WINSTATE, state.toString());
            json.put(TOKEN.RUNNING, running);
        }

        return json;
    }

    public GameStateQuery fillAnswerNotRunning(WinState state) {
        super.setAnswer();
        this.running = false;
        this.state = state;
        
        return this;
    }
    public GameStateQuery fillAnswerRunning() {
        super.setAnswer();
        this.running = true;
        
        return this;
    }

    public Boolean isGameRunning() {
        return running;
    }
    public WinState getWinState() {
        return state;
    }
}
