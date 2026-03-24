package model.protocol.queries;

import org.json.JSONObject;

import model.game.maze.Maze;
import model.protocol.Query;

public final class PartialMazeQuery extends Query {
    private static final class TOKEN {
        public static final String PARTIAL_MAZE = "maze";
    }

    protected JSONObject partial_json = null;
    public PartialMazeQuery() {}
    public PartialMazeQuery(JSONObject json) {
        super(json);

        if (isAnswer()) {
            partial_json = json.getJSONObject(TOKEN.PARTIAL_MAZE);
        }
    }

    public static final String ACTION = "get_part_maze";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            json.put(TOKEN.PARTIAL_MAZE, partial_json);
        }

        return json;
    }
    
    public PartialMazeQuery fillAnswer(Maze maze) {
        super.setAnswer();
        this.partial_json = maze.toPartialJSON();
        
        return this;
    }

    public JSONObject getPartialMaze() { return this.partial_json; }
}
