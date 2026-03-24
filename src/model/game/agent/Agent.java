package model.game.agent;

import java.awt.Color;

import org.json.JSONObject;

import model.game.agent.behavior.Behavior;
import model.game.maze.Maze;
import model.game.maze.Maze.EntityType;

/**
 * a class for representing an agent
 */
public abstract class Agent {
    /** the current position of the agent */
    protected PositionAgent position;
    /** the colors of the agent */
    protected Color[] colors;
    /** the current last position of the agent <p> used to detect agents crossing each other */
    private PositionAgent last_position;
    /** the behavior of the agent */
    protected Behavior behavior;
    /** wether the agent is dead or alive */
    private boolean dead = false;

    /**
     * creates an agent from a position
     * @param position the position of the agent
     */
    public Agent(PositionAgent position) { this.position = position; }
    
    /**
     * applies an action to the agent
     * @param action the action to apply
     */
    public void move(AgentAction action) {
        last_position = new PositionAgent(position.getX(), position.getY(), position.getDir());
        position.setDir(action.get_direction());
        position.setX(position.getX() + action.get_vx());
        position.setY(position.getY() + action.get_vy());
    }
    /**
     * a getter to the position of the agent
     * @return the current position of the agent
     */
    public PositionAgent get_position() { return position; }
    /**
     * a getter to the last position of the agent
     * @return the last position of the agent
     */
    public PositionAgent get_last_position() { return last_position; }

    /**
     * a setter to the colors of the agent
     * @param colors the colors of the agent
     */
    public void set_colors(Color[] colors) { this.colors = colors; }
    /**
     * a setter to the colors of the agent
     * @param color1 the first color of the agent
     * @param color2 the second color of the agent
     */
    public void set_colors(Color color1, Color color2) { this.colors = new Color[] { color1, color2 }; }
    /**
     * a getter to the colors of the agent
     * @return the colors of the agent
     */
    public Color[] get_colors() { return colors; }

    /**
     * sets the agent behavior ands returns itself
     * @param behavior the behavior to set
     * @return the agent
     */
    public Agent set_behavior(Behavior behavior) {
        if (this.behavior != null) this.behavior.clear_agent();
        this.behavior = behavior;
        this.behavior.set_agent(this);
        return this;
    }
    /**
     * gets the agent behavior
     * @return the behavior of the agent
     */
    public Behavior get_behavior() {
        return this.behavior;
    }
    /**
     * gets the action from the agent behavior
     * @param maze the maze the agent is in
     * @param ghost_scared wether the ghosts are scared
     * @return the action given by the behavior
     */
    public AgentAction get_action(Maze maze, boolean ghost_scared) { return this.behavior.get_action_or_default(maze, ghost_scared); }
    /**
     * gets the default action of the agent (continue in the same direction)
     * @return the default action of the agent
     */
    public AgentAction get_default_action() { return new AgentAction(position.getDir()); }

    /**
     * gets the entity type of the agent
     * @return the entity type of the agent
     */
    public abstract Maze.EntityType get_type();
    /**
     * wether the agent can eat the specified entity depending on the scared state of the ghosts
     * @param entity the entity type to eat
     * @param ghost_scared wether the ghosts are scared
     * @return True if the agent can eat the given entity type, False else
     */
    public abstract boolean can_eat(Maze.EntityType entity, boolean ghost_scared);

    /**
     * wether the agent is dead or alive
     * @return True if the agent is dead, False else
     */
    public boolean is_dead() { return dead; }
    /**
     * kills the agent and sets its position outide of the maze
     */
    public void kill() { dead = true; position.setX(-1); position.setY(-1); }


    private static final class TOKEN {
        public static final String TYPE = "type";
        public static final String POSITION = "position";
        public static final String COLOR1 = "color1";
        public static final String COLOR2 = "color2";
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put(TOKEN.POSITION, position.toJSON());
        json.put(TOKEN.COLOR1, colors[0].getRGB());
        json.put(TOKEN.COLOR2, colors[1].getRGB());
        json.put(TOKEN.TYPE, get_type().toString());
        return json;
    }
    public static Agent fromJSON(JSONObject json) {
        EntityType type = EntityType.valueOf(json.getString(TOKEN.TYPE));

        if (type == EntityType.Ghost) {
            Agent agent = new GhostAgent(PositionAgent.fromJSON(json.getJSONObject(TOKEN.POSITION)));
            agent.set_colors(new Color(json.getInt(TOKEN.COLOR1)), new Color(json.getInt(TOKEN.COLOR2)));
            return agent;
        }
        else if (type == EntityType.Pacman) {
            Agent agent = new PacmanAgent(PositionAgent.fromJSON(json.getJSONObject(TOKEN.POSITION)));
            agent.set_colors(new Color(json.getInt(TOKEN.COLOR1)), new Color(json.getInt(TOKEN.COLOR2)));
            return agent;
        }

        return null;
    }

    public JSONObject toPartialJSON() {
        JSONObject json = new JSONObject();
        json.put(TOKEN.POSITION, position.toJSON());
        return json;
    }
    public void applyPartialJSON(JSONObject json) {
        this.position = PositionAgent.fromJSON(json.getJSONObject(TOKEN.POSITION));
    }
}
