package model.game.agent.behavior.pathfinding;

import java.util.ArrayList;
import java.util.PriorityQueue;

import model.game.agent.AgentAction;
import model.game.agent.PositionAgent;
import model.game.agent.AgentAction.Direction;
import model.game.agent.behavior.Behavior;
import model.game.agent.behavior.BehaviorFactory;
import model.game.agent.behavior.BehaviorFactory.PathfindingPreset;
import model.game.agent.behavior.pathfinding.cell_weight.CellWeight;
import model.game.agent.behavior.pathfinding.destination.DestinationCheck;
import model.game.agent.behavior.pathfinding.direction.DirectionOrder;
import model.game.agent.behavior.pathfinding.spread.MoveCheck;
import model.game.maze.Maze;

/**
 * a customizable behavior class that pathfinds to a specified target
 */
public class PathFindingBehavior extends Behavior {
    protected DestinationCheck destination_check;
    protected MoveCheck move_check;
    protected DirectionOrder direction_order;
    protected CellWeight cell_weight;
    public PathfindingPreset preset;

    /**
     * creates the pathfinding behavior from its composants
     * @param destination_check the {@link DestinationCheck} to use
     * @param move_check the {@link MoveCheck} to use
     * @param direction_order the {@link DirectionOrder} to use
     * @param cell_weight the {@link CellWeight} to use
    */
    public PathFindingBehavior(DestinationCheck destination_check, MoveCheck move_check, DirectionOrder direction_order, CellWeight cell_weight, PathfindingPreset preset) {
        this.destination_check = destination_check;
        this.move_check = move_check;
        this.direction_order = direction_order;
        this.cell_weight = cell_weight;
        this.preset = preset;
    }

    /**
     * a cell for the pathfinding algorithm 
     */
    public class Cell implements Comparable<Cell> {
        protected CellWeight cell_weight;

        public PositionAgent position;
        public Direction first_direction;
        public int distance;
        public int weight;
        /**
         * Creates a cell from its atributes
         * @param position the position of the cell
         * @param first_direction the direction to go from the start cell to arrive at this cell (null if this is the first cell)
         * @param distance the distance from the start cell to this one
         * @param cell_weight the {@link CellWeight} to use for comparing
         */
        public Cell(PositionAgent position, Direction first_direction, int distance, CellWeight cell_weight) {
            this.position = position;
            this.first_direction = first_direction;
            this.distance = distance;
            this.cell_weight = cell_weight;
            this.weight = 0;
        }

        /**
         * Spreads the cell, exploring in the given direction
         * @param d the direction to spread
         * @param maze the maze in wich to spread
         * @return the new cell
         */
        public Cell spread(Direction d, Maze maze) {
            PositionAgent next_pos = position.add_action(new AgentAction(d));
            maze.warpPosition(next_pos);
            Cell next_cell = new Cell(next_pos, first_direction != null ? first_direction : d, distance + 1, cell_weight);
            next_cell.weight = cell_weight.get_weight(next_cell, maze);
            return next_cell;
        }

        public int compareTo(Cell other) {
            if (distance + weight == other.distance + other.weight) return weight - other.weight;
            return (distance + weight) - (other.distance + other.weight);
        }
    }

    /**
     * use a pathfinding algorithm to get the direction in wich to move to arrive at the target
     * @param maze the maze in which to pathfind
     * @param first_cell the cell from wich to start
     * @return the direction found or null if the target cannot be reached
    */
    private Direction get_pathfinding_direction(Maze maze, Cell first_cell) {
        PriorityQueue<Cell> cell_queue = new PriorityQueue<Cell>();
        ArrayList<PositionAgent> visited_cells = new ArrayList<PositionAgent>();

        visited_cells.add(first_cell.position);
        for (Direction d : direction_order.get_direction_order()) {
            if (move_check.can_spread(first_cell, d, maze)) {
                Cell cell = first_cell.spread(d, maze);
                cell_queue.add(cell);
                visited_cells.add(cell.position);
            }
        }
        
        while (!cell_queue.isEmpty()) {
            Cell cell = cell_queue.poll();

            if (destination_check.is_cell_final(cell, maze)) {
                return cell.first_direction;
            }

            for (Direction d : direction_order.get_direction_order()) {
                if (move_check.can_spread(cell, d, maze)) {
                    Cell next_cell = cell.spread(d, maze);
                    if (!visited_cells.contains(next_cell.position)) {
                        cell_queue.add(next_cell);
                        visited_cells.add(next_cell.position);
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected AgentAction get_action(Maze maze, boolean ghost_scared) {
        Direction d = get_pathfinding_direction(maze, new Cell(agent.get_position(), null, 0, cell_weight));
        if (d == null) return null;
        return new AgentAction(d);
    }

    public BehaviorFactory.BehaviorType get_behavior_type() { return BehaviorFactory.BehaviorType.Pathfinding; }
}
