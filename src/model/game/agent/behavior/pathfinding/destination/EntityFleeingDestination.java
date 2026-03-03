package model.game.agent.behavior.pathfinding.destination;

import java.util.ArrayList;

import model.game.agent.PositionAgent;
import model.game.agent.behavior.pathfinding.PathFindingBehavior.Cell;
import model.game.maze.Maze;
import model.game.maze.Maze.EntityType;

/** A DestinationCheck that stops when a minimal distance to a given position is reached */
public class EntityFleeingDestination extends DestinationCheck {
    protected ArrayList<EntityType> entities;
    protected int min_distance;

    /**
     * creates an EntityDestination from a list of entities
     * @param entities the entities to flee
     * @param min_distance the minimal distance to flee
     */
    public EntityFleeingDestination(ArrayList<EntityType> entities, int min_distance) {
        this.entities = entities;
        this.min_distance = min_distance;
    }
    /**
     * creates an EntityDestination from an entity
     * @param entity the entity to flee
     * @param min_distance the minimal distance to flee
     */
    public EntityFleeingDestination(EntityType entity, int min_distance) {
        this.entities = new ArrayList<>();
        this.entities.add(entity);
        this.min_distance = min_distance;
    }

    @Override
    public boolean is_cell_final(Cell cell, Maze maze) {
        int d = min_distance;

        for (EntityType entity : entities) {
            PositionAgent closest = maze.getClosestEntity(cell.position.getX(), cell.position.getY(), entity);
            if (closest != null && (d == -1 || cell.position.manhattanDistance(closest, maze) < d)) {
                d = cell.position.manhattanDistance(closest, maze);
            }
        }

        if (d >= min_distance) return true;
        return super.is_cell_final(cell, maze);
    }
}
