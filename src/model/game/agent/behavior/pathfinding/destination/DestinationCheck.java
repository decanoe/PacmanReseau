package model.game.agent.behavior.pathfinding.destination;

import model.game.agent.behavior.pathfinding.PathFindingBehavior.Cell;
import model.game.maze.Maze;

/** A pathfinding module that determines wether a cell is the target or not */
public class DestinationCheck {
    /**
     * wether a cell is the target or not
     * @param cell the cell to check
     * @param maze the maze in wich to check
     * @return True if the pathfinding can stop at this cell, False else
     */
    public boolean is_cell_final(Cell cell, Maze maze) {
        return false;
    }
}
