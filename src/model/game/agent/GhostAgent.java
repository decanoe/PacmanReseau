package model.game.agent;

import java.awt.Color;

import model.game.maze.Maze;

/**
 * a class for representing a ghost
 */
public class GhostAgent extends Agent {
    /**
     * creates an ghost from a position
     * @param position the position of the ghost
     */
    public GhostAgent(PositionAgent position) {
        super(position);
        this.color = new Color(Color.HSBtoRGB((float)Math.random(), 1, 1));
    }

    @Override
    public Maze.EntityType get_type() { return Maze.EntityType.Ghost; }
    @Override
    public boolean can_eat(Maze.EntityType entity, boolean ghost_scared) {
        return !ghost_scared && entity == Maze.EntityType.Pacman;
    }
}
