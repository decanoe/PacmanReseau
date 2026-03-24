package model.protocol.queries;

import org.json.JSONObject;

import model.game.maze.Maze;
import model.protocol.Query;

public final class FullMazeQuery extends Query {
    private static final class TOKEN {
        public static final String MAZE = "maze";
    }

    protected Maze maze = null;
    public FullMazeQuery() {}
    public FullMazeQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            this.maze = new Maze(json.getJSONObject(TOKEN.MAZE));
        }
    }

    public static final String ACTION = "get_full_maze";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            json.put(TOKEN.MAZE, maze.toJSON());
        }

        return json;
    }
    
    public FullMazeQuery fillAnswer(Maze maze) {
        super.setAnswer();
        this.maze = maze;
        
        return this;
    }

    public Maze getMaze() {
        return maze;
    }
}
