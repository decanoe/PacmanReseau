package model.game.agent;

import model.game.maze.Maze;

/**
 * a class for representing a pacman
 */
public class PacmanAgent extends Agent {
    /**
     * creates a pacman from a position
     * @param position the position of the pacman
     */
    public PacmanAgent(PositionAgent position) {
        super(position);
    }

    @Override
    public Maze.EntityType get_type() { return Maze.EntityType.Pacman; }
    @Override
    public boolean can_eat(Maze.EntityType entity, boolean ghost_scared) {
        switch (entity) {
            case Food: return true;
            case Capsule: return true;
            case Ghost: return ghost_scared;
            default: return false;
        }
    }
}
