package model.protocol.Queries;

import org.json.JSONObject;

import java.awt.Color;

import model.game.maze.Maze.EntityType;
import model.protocol.Query;

public final class CosmeticsQuery extends Query {
    private static final class TOKEN {
        public static enum TYPE { Fantome, Pacman, Labyrinthe }
        public static enum INDEX { color1, color2 }
    }

    String[] pacman_colors;
    String[] ghost_colors, maze_colors;
    public CosmeticsQuery() {
    }
    public CosmeticsQuery(JSONObject json) {
        super(json);

        if (this.isAnswer()) {
            pacman_colors = readColors(json, TOKEN.TYPE.Pacman);
            ghost_colors = readColors(json, TOKEN.TYPE.Fantome);
            maze_colors = readColors(json, TOKEN.TYPE.Labyrinthe);
        }
    }

    protected String readColor(JSONObject json, TOKEN.INDEX index) {
        if (json.has(index.toString())) return json.getString(index.toString());
        return null;
    }
    protected String[] readColors(JSONObject json, TOKEN.TYPE type) {
        if (!json.has(type.toString())) return new String[] { null, null };
        
        JSONObject obj = json.getJSONObject(type.toString());
        return new String[] { readColor(obj, TOKEN.INDEX.color1), readColor(obj, TOKEN.INDEX.color2) };
    }
    protected void write(JSONObject json, TOKEN.TYPE type, String[] colors) {
        JSONObject obj = new JSONObject();

        if (colors[0] != null) obj.put(TOKEN.INDEX.color1.toString(), colors[0]);
        if (colors[1] != null) obj.put(TOKEN.INDEX.color2.toString(), colors[1]);

        json.put(type.toString(), obj);
    }

    public static final String ACTION = "get_cosmetics";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            write(json, TOKEN.TYPE.Pacman, pacman_colors);
            write(json, TOKEN.TYPE.Fantome, ghost_colors);
            write(json, TOKEN.TYPE.Labyrinthe, maze_colors);
        }

        return json;
    }

    public CosmeticsQuery fillAnswer(JSONObject web_json) {
        super.setAnswer();

        System.out.println(web_json.toString());
        
        pacman_colors = readColors(web_json, TOKEN.TYPE.Pacman);
        ghost_colors = readColors(web_json, TOKEN.TYPE.Fantome);
        maze_colors = readColors(web_json, TOKEN.TYPE.Labyrinthe);
        
        return this;
    }

    protected Color[] get_colors_or_default(String[] colors, Color[] _default) {
        return new Color[] {
            colors[0] != null ? Color.decode(colors[0]) : _default[0],
            colors[1] != null ? Color.decode(colors[1]) : _default[1] };
    }

    public Color[] getPacmanColors(Color[] _default) {
        return get_colors_or_default(pacman_colors, _default);
    }
    public Color[] getGhostColors(Color[] _default) {
        return get_colors_or_default(ghost_colors, _default);
    }
    public Color[] getMazeColors(Color[] _default) {
        return get_colors_or_default(maze_colors, _default);
    }

    public Color[] getAgentColors(EntityType type, Color[] _default) {
        if (type == EntityType.Pacman) return getPacmanColors(_default);
        else return getGhostColors(_default);
    }
}
