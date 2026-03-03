package model.game.agent.behavior;

import java.util.Arrays;
import java.util.List;

import model.game.agent.Agent;
import model.game.agent.AgentAction;
import model.game.maze.Maze;

/**
 * a behavior class for changing the default behavior
 */
public class DualDefaultBehavior extends Behavior {
    Behavior normalBehavior;
    Behavior defaultBehavior;

    /**
     * creates the behavior from a normal behavior and a default one 
     * @param normalBehavior the normal behavior
     * @param defaultBehavior the default behavior
    */
    public DualDefaultBehavior(Behavior normalBehavior, Behavior defaultBehavior) {
        this.normalBehavior = normalBehavior;
        this.defaultBehavior = defaultBehavior;

        normalBehavior.set_parent(this, 0);
        defaultBehavior.set_parent(this, 1);
    }

    @Override
    protected AgentAction get_action(Maze maze, boolean ghost_scared) {
        AgentAction normal = normalBehavior.get_action(maze, ghost_scared);
        if (normal != null) return normal;
        return defaultBehavior.get_action(maze, ghost_scared);
    }

    @Override
    public void set_agent(Agent agent) { super.set_agent(agent); normalBehavior.set_agent(agent); defaultBehavior.set_agent(agent); }

    public BehaviorFactory.BehaviorType get_behavior_type() { return BehaviorFactory.BehaviorType.Dual_Default; }
    @Override
    public List<Behavior> get_child_behaviors() { return Arrays.asList(normalBehavior, defaultBehavior); }
    @Override
    public List<String> get_child_header() { return Arrays.asList("normal", "default"); }

    @Override
    public void set_child_behavior(int index, Behavior behavior) {
        if (index == 0) {
            this.normalBehavior = behavior;
            behavior.set_parent(this, 0);
            behavior.set_agent(agent);
        }
        if (index == 1) {
            this.defaultBehavior = behavior;
            behavior.set_parent(this, 1);
            behavior.set_agent(agent);
        }
    }
}
