package model.game.agent.behavior.pathfinding.spread;

import java.util.ArrayList;

import model.game.agent.AgentAction;
import model.game.agent.PositionAgent;
import model.game.agent.AgentAction.Direction;
import model.game.agent.behavior.pathfinding.PathFindingBehavior.Cell;
import model.game.maze.Maze;
import model.game.maze.Maze.EntityType;

/** A MoveCheck that can't go through the given entities */
public class AvoidEntity extends MoveCheck {
    protected ArrayList<EntityType> entities;

    /**
     * Creates an AvoidEntity instance from the given entities array
     * @param entities the entities to avoid
     */
    public AvoidEntity(ArrayList<EntityType> entities) {
        this.entities = entities;
    }
    /**
     * Creates an AvoidEntity instance from a given entitiy
     * @param entity the entity to avoid
     */
    public AvoidEntity(EntityType entity) {
        this.entities = new ArrayList<>();
        this.entities.add(entity);
    }

    @Override
    public boolean can_spread(Cell cell, Direction d, Maze maze) {
        PositionAgent next_pos = cell.position.add_action(new AgentAction(d));
        maze.warpPosition(next_pos);
        if (maze.isEntity(next_pos.getX(), next_pos.getY(), entities)) return false;
        return super.can_spread(cell, d, maze);
    }
}
