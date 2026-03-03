package model.game.agent.behavior.pathfinding.cell_weight;

import model.game.agent.behavior.pathfinding.PathFindingBehavior.Cell;
import model.game.maze.Maze;

/** A pathfinding module adds a weight to cell comparison for optimisation */
public class CellWeight {
    /**
     * The weight to add to a cell
     * <p>
     * Higher weight means the cell is checked later, the weight is added to the distance from the start
     * @param cell the cell to compute the weight for
     * @param maze the maze in which to compute the weight
     * @return the weight to add to the cell
     */
    public int get_weight(Cell cell, Maze maze) {
        return 0;
    }
}
