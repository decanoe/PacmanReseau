package model.game.agent.behavior;

import java.util.ArrayList;
import java.util.List;

import model.game.agent.Agent;
import model.game.agent.AgentAction;
import model.game.maze.Maze;

/**
 * a class for representing an agent behavior
 */
public abstract class Behavior {
    /** the linked agent */
    protected Agent agent;
    protected Behavior parent = null;
    protected int child_index = -1;
    
    /**
     * Sets the agent of the behavior
     * @param agent the agent
    */
    public void set_agent(Agent agent) { this.agent = agent; }
    /**
     * Sets the parent behavior of this behavior
     * @param parent the parent
     * @param index the index of this behavior in the parent chilren
    */
    public void set_parent(Behavior parent, int index) { this.parent = parent; this.child_index = index; }
    /**
     * clears the agent of the behavior
    */
    public void clear_agent() { this.agent = null; }
    /**
     * gets the action the agent should realize
     * @param maze the Maze the agent is in
     * @param ghost_scared wether the ghosts are scared
     * @return an AgentAction or null if no action can be decided
    */
    protected abstract AgentAction get_action(Maze maze, boolean ghost_scared);
    
    /**
     * gets the action the agent should realize
     * @param maze the Maze the agent is in
     * @param ghost_scared wether the ghosts are scared
     * @return an AgentAction or the default action if no action can be decided
    */
    public AgentAction get_action_or_default(Maze maze, boolean ghost_scared) {
        AgentAction action = get_action(maze, ghost_scared);
        if (action != null) return action;
        return new AgentAction(agent.get_position().getDir());
    }

    /**
     * gets the type of the behavior
     * @return the type of the behavior
    */
    public abstract BehaviorFactory.BehaviorType get_behavior_type();
    /**
     * gets the behaviors inside this behavior
     * @return a list of child behaviors
    */
    public List<Behavior> get_child_behaviors() { return new ArrayList<>(); }
    /**
     * gets a list of child helper names
     * @return a list of child helper names
    */
    public List<String> get_child_header() { return new ArrayList<>(); }
    /**
     * gets the parent behavior
     * @return the parent behavior or null if root
    */
    public Behavior get_parent_behavior() { return parent; }
    /**
     * gets the index of this behavior in the parent children
     * @return the index of this behavior in the parent children
    */
    public int get_child_index() { return child_index; }
    /**
     * sets the child behavior (if this behavior can have childs)
     * @param index the index of the child
     * @param behavior the behavior to set
    */
    public void set_child_behavior(int index, Behavior behavior) {}

    /**
     * replace this behavior with another one
     * @param new_behavior the new behavior to set
    */
    public void replace_this_behavior(Behavior new_behavior) {
        if (this.parent == null) {
            this.agent.set_behavior(new_behavior);
        }
        else {
            this.parent.set_child_behavior(get_child_index(), new_behavior);
        }
    }
}
