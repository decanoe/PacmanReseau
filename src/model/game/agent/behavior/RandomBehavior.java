package model.game.agent.behavior;

import model.game.agent.AgentAction;
import model.game.agent.AgentAction.Direction;
import model.game.maze.Maze;

/**
 * a behavior class for doing random movements
 */
public class RandomBehavior extends Behavior {
    @Override
    protected AgentAction get_action(Maze maze, boolean ghost_scared) {
        if (Math.random() < 0.5 || agent.get_position().getDir() == Direction.STOP)
            return new AgentAction(Direction.values()[(int)Math.floor(Math.random() * 4)]);
        return null;
    }

    public BehaviorFactory.BehaviorType get_behavior_type() { return BehaviorFactory.BehaviorType.Random; }
}
