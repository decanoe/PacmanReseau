package model.game.agent.behavior.pathfinding.direction;

import java.util.Arrays;
import java.util.List;

import model.game.agent.AgentAction.Direction;

/** A pathfinding module that determines in which order to check the spread directions */
public class DirectionOrder {
    protected static final List<Direction> DIRECTIONS = Arrays.asList(new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST });

    /**
     * The order in which to check spread directions
     * @return a list of direction to check
     */
    public List<Direction> get_direction_order() {
        return DIRECTIONS;
    }
}
