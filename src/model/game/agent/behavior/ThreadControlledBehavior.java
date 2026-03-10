package model.game.agent.behavior;

import model.game.agent.AgentAction;
import model.game.agent.AgentAction.Direction;
import model.game.maze.Maze;

/**
 * a behavior class for controlling an agent with the keyboard
 */
public class ThreadControlledBehavior extends Behavior {
    protected Direction d = Direction.STOP;

    @Override
    protected AgentAction get_action(Maze maze, boolean ghost_scared) {
        Direction current_d = agent.get_position().getDir();
        if (d == Direction.STOP && current_d != Direction.STOP) return new AgentAction(current_d);

        return new AgentAction(d);
    }

    public void setNextDirection(Direction d) {
        this.d = d;
    }

    public BehaviorFactory.BehaviorType get_behavior_type() { return BehaviorFactory.BehaviorType.Random; }
}
