package model.game.agent.behavior.pathfinding.cell_weight;

import java.util.ArrayList;

import model.game.agent.PositionAgent;
import model.game.agent.behavior.pathfinding.PathFindingBehavior.Cell;
import model.game.maze.Maze;
import model.game.maze.Maze.EntityType;

/** A CellWeight that adds a weight based on the negative distance to an entity */
public class EntityFleeingWeight extends CellWeight {
    protected ArrayList<EntityType> entities;

    /**
     * creates an EntityFleeingWeight from a list of entities
     * @param entities the entities compute the distance to
     */
    public EntityFleeingWeight(ArrayList<EntityType> entities) {
        this.entities = entities;
    }
    /**
     * creates an EntityFleeingWeight from an entity
     * @param entity the entity compute the distance to
     */
    public EntityFleeingWeight(EntityType entity) {
        this.entities = new ArrayList<>();
        this.entities.add(entity);
    }

    @Override
    public int get_weight(Cell cell, Maze maze) {
        int d = -1;

        for (EntityType entity : entities) {
            PositionAgent closest = maze.getClosestEntity(cell.position.getX(), cell.position.getY(), entity);
            if (closest != null && (d == -1 || cell.position.manhattanDistance(closest, maze) < d)) {
                d = cell.position.manhattanDistance(closest, maze);
            }
        }

        return -d;
    }
}
