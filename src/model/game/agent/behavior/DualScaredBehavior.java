package model.game.agent.behavior;

import java.util.Arrays;
import java.util.List;

import model.game.agent.Agent;
import model.game.agent.AgentAction;
import model.game.maze.Maze;

/**
 * a behavior class that changes when the ghosts are scared
 */
public class DualScaredBehavior extends Behavior {
    Behavior normalBehavior;
    Behavior scaredBehavior;

    /**
     * creates the behavior from a normal behavior and a scared one 
     * @param normalBehavior the normal behavior
     * @param scaredBehavior the scared behavior
    */
    public DualScaredBehavior(Behavior normalBehavior, Behavior scaredBehavior) {
        this.normalBehavior = normalBehavior;
        this.scaredBehavior = scaredBehavior;

        normalBehavior.set_parent(this, 0);
        scaredBehavior.set_parent(this, 1);
    }

    @Override
    protected AgentAction get_action(Maze maze, boolean ghost_scared) {
        if (ghost_scared) return scaredBehavior.get_action(maze, ghost_scared);
        else return normalBehavior.get_action(maze, ghost_scared);
    }

    @Override
    public void set_agent(Agent agent) { super.set_agent(agent); normalBehavior.set_agent(agent); scaredBehavior.set_agent(agent); }

    public BehaviorFactory.BehaviorType get_behavior_type() { return BehaviorFactory.BehaviorType.Dual_Scared; }
    @Override
    public List<Behavior> get_child_behaviors() { return Arrays.asList(normalBehavior, scaredBehavior); }
    @Override
    public List<String> get_child_header() { return Arrays.asList("normal", "scared"); }

    @Override
    public void set_child_behavior(int index, Behavior behavior) {
        if (index == 0) {
            this.normalBehavior = behavior;
            behavior.set_parent(this, 0);
            behavior.set_agent(agent);
        }
        if (index == 1) {
            this.scaredBehavior = behavior;
            behavior.set_parent(this, 1);
            behavior.set_agent(agent);
        }
    }
}
