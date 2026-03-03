package model.game.agent.behavior;

import model.game.agent.AgentAction;
import model.game.agent.AgentAction.Direction;
import model.game.maze.Maze;

/**
 * a behavior class that stops any movements
 */
public class StopBehavior extends Behavior {
    @Override
    public AgentAction get_action(Maze maze, boolean ghost_scared) {
        return new AgentAction(Direction.STOP);
    }

    public BehaviorFactory.BehaviorType get_behavior_type() { return BehaviorFactory.BehaviorType.Stop; }
}
