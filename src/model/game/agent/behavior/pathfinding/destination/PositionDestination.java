package model.game.agent.behavior.pathfinding.destination;

import model.game.agent.PositionAgent;
import model.game.agent.behavior.pathfinding.PathFindingBehavior.Cell;
import model.game.maze.Maze;

/** A DestinationCheck that stops when arriving on a given position */
public class PositionDestination extends DestinationCheck {
    protected PositionAgent position;

    /**
     * creates an PositionDestination from a position
     * @param position the position to stop on
     */
    public PositionDestination(PositionAgent position) {
        this.position = position;
    }

    @Override
    public boolean is_cell_final(Cell cell, Maze maze) {
        if (cell.position == position) return true;
        return super.is_cell_final(cell, maze);
    }
}
