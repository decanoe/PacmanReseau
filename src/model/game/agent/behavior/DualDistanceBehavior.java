package model.game.agent.behavior;

import java.util.Arrays;
import java.util.List;

import model.game.agent.Agent;
import model.game.agent.AgentAction;
import model.game.agent.PositionAgent;
import model.game.maze.Maze;
import model.game.maze.Maze.EntityType;

/**
 * a behavior class that changes based on a distance to an entity
 */
public class DualDistanceBehavior extends Behavior {
    Behavior closeBehavior;
    Behavior farBehavior;
    EntityType entity;
    float distance_threashold;

    /**
     * creates the behavior from a close behavior and a far one 
     * @param closeBehavior the behavior to do when the distance is under the threashold
     * @param farBehavior the behavior to do when the distance is over the threashold
     * @param entity the entity to compute the distance from
     * @param distance_threashold the distance threashold
    */
    public DualDistanceBehavior(Behavior closeBehavior, Behavior farBehavior, EntityType entity, float distance_threashold) {
        this.closeBehavior = closeBehavior;
        this.farBehavior = farBehavior;
        this.entity = entity;
        this.distance_threashold = distance_threashold;

        closeBehavior.set_parent(this, 0);
        farBehavior.set_parent(this, 1);
    }

    @Override
    protected AgentAction get_action(Maze maze, boolean ghost_scared) {
        PositionAgent closest = maze.getClosestEntity(agent.get_position().getX(), agent.get_position().getY(), entity);

        if (closest == null) return farBehavior.get_action(maze, ghost_scared);
        if (agent.get_position().distance(closest, maze) < distance_threashold) return closeBehavior.get_action(maze, ghost_scared);
        return farBehavior.get_action(maze, ghost_scared);
    }

    @Override
    public void set_agent(Agent agent) { super.set_agent(agent); closeBehavior.set_agent(agent); farBehavior.set_agent(agent); }

    public BehaviorFactory.BehaviorType get_behavior_type() { return BehaviorFactory.BehaviorType.Dual_Distance; }
    @Override
    public List<Behavior> get_child_behaviors() { return Arrays.asList(closeBehavior, farBehavior); }
    @Override
    public List<String> get_child_header() { return Arrays.asList("close", "far"); }

    @Override
    public void set_child_behavior(int index, Behavior behavior) {
        if (index == 0) {
            this.closeBehavior = behavior;
            behavior.set_parent(this, 0);
            behavior.set_agent(agent);
        }
        if (index == 1) {
            this.farBehavior = behavior;
            behavior.set_parent(this, 1);
            behavior.set_agent(agent);
        }
    }

    public void set_distance_threashold(float distance_threashold) { this.distance_threashold = distance_threashold; }
    public float get_distance_threashold() { return distance_threashold; }
    public void set_entity_target(EntityType entity) { this.entity = entity; }
    public EntityType get_entity_target() { return entity; }
}
