package model.game.agent.behavior.pathfinding.direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.game.agent.AgentAction.Direction;

/** a DirectionOrder with random order */
public class RandomDirectionOrder extends DirectionOrder {
    @Override
    public List<Direction> get_direction_order() {
        var directions = new ArrayList<>(DIRECTIONS);
        Collections.shuffle(directions);
        return directions;
    }
}
