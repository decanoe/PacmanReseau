package model.protocol.Queries;

import org.json.JSONObject;

import model.game.maze.Maze;
import model.protocol.Query;

public final class GameStateQuery extends Query {
    private static final class TOKEN {
        public static final String RUNNING = "running";
        public static final String MAZE = "maze";
    }

    protected boolean running;
    protected Maze maze = null;
    public GameStateQuery() {}
    public GameStateQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
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
            json.put(TOKEN.RUNNING, running);
            if (isGameRunning()) {
                json.put(TOKEN.MAZE, maze.toJSON());
            }
        }

        return json;
    }

    public GameStateQuery fillAnswerNotRunning() {
        super.setAnswer();
        this.running = false;
        
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
}
