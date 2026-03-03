package model.game.agent.behavior.pathfinding.destination;

import java.util.ArrayList;

import model.game.agent.behavior.pathfinding.PathFindingBehavior.Cell;
import model.game.maze.Maze;
import model.game.maze.Maze.EntityType;

/** A DestinationCheck that stops when arriving on a given entity type */
public class EntityDestination extends DestinationCheck {
    protected ArrayList<EntityType> entities;

    /**
     * creates an EntityDestination from a list of entities
     * @param entities the entities to stop on
     */
    public EntityDestination(ArrayList<EntityType> entities) {
        this.entities = entities;
    }
    /**
     * creates an EntityDestination from an entity
     * @param entity the entity to stop on
     */
    public EntityDestination(EntityType entity) {
        this.entities = new ArrayList<>();
        this.entities.add(entity);
    }

    @Override
    public boolean is_cell_final(Cell cell, Maze maze) {
        if (maze.isEntity(cell.position.getX(), cell.position.getY(), entities)) return true;
        return super.is_cell_final(cell, maze);
    }
}
