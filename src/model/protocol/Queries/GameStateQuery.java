package model.protocol.Queries;

import org.json.JSONObject;

import model.game.maze.Maze;
import model.protocol.Query;

public final class GameStateQuery extends Query {
    private static final class TOKEN {
        public static final String RUNNING = "running";
        public static final String MAZE = "maze";
        public static final String WINSTATE = "win_state";
    }
    public enum WinState { Pacman, Ghost, None };

    protected WinState state = WinState.None;
    protected boolean running;
    protected Maze maze = null;
    public GameStateQuery() {}
    public GameStateQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            this.state = WinState.valueOf(json.getString(TOKEN.WINSTATE));
            this.running = json.getBoolean(TOKEN.RUNNING);
            if (isGameRunning()) {
                this.maze = new Maze(json.getJSONObject(TOKEN.MAZE));
            }
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
            if (isGameRunning()) {
                json.put(TOKEN.MAZE, maze.toJSON());
            }
        }

        return json;
    }

    public GameStateQuery fillAnswerNotRunning(WinState state) {
        super.setAnswer();
        this.running = false;
        this.state = state;
        
        return this;
    }
    public GameStateQuery fillAnswerRunning(Maze maze) {
        super.setAnswer();
        this.running = true;
        this.maze = maze;
        
        return this;
    }

    public Boolean isGameRunning() {
        return running;
    }
    public Maze getMaze() {
        return maze;
    }
    public WinState getWinState() {
        return state;
    }
}
