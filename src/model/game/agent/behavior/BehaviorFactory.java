package model.game.agent.behavior;

import model.game.agent.behavior.pathfinding.PathFindingBehavior;
import model.game.agent.behavior.pathfinding.cell_weight.EntityFleeingWeight;
import model.game.agent.behavior.pathfinding.cell_weight.EntityTargetWeight;
import model.game.agent.behavior.pathfinding.destination.EntityDestination;
import model.game.agent.behavior.pathfinding.destination.EntityFleeingDestination;
import model.game.agent.behavior.pathfinding.direction.RandomDirectionOrder;
import model.game.agent.behavior.pathfinding.spread.AvoidEntity;
import model.game.agent.behavior.pathfinding.spread.MoveCheck;
import model.game.maze.Maze.EntityType;

public class BehaviorFactory {
    public enum BehaviorType {
        Stop, Random, Dual_Default, Dual_Scared, Dual_Distance, Pathfinding
    }
    public enum PathfindingPreset {
        SearchCapsule, SearchFood, SearchGhosts, SearchPacmans, FleePacman, Custom
    }

    public static Behavior create_behavior(BehaviorType type) {
        switch (type) {
            case Stop: return new StopBehavior();
            case Random: return new RandomBehavior();
            case Dual_Default: return new DualDefaultBehavior(create_behavior(BehaviorType.Random), create_behavior(BehaviorType.Random));
            case Dual_Scared: return new DualScaredBehavior(create_behavior(BehaviorType.Random), create_behavior(BehaviorType.Random));
            case Dual_Distance: return new DualDistanceBehavior(create_behavior(BehaviorType.Random), create_behavior(BehaviorType.Random), EntityType.Capsule, 5);
            case Pathfinding: return new PathFindingBehavior(new EntityDestination(EntityType.Capsule), new AvoidEntity(EntityType.Ghost), new RandomDirectionOrder(), new EntityTargetWeight(EntityType.Capsule), PathfindingPreset.Custom);
        }
        return new RandomBehavior();
    }

    public static Behavior create_pathfinding(PathfindingPreset preset) {
        switch (preset) {
            case SearchCapsule: return new PathFindingBehavior(new EntityDestination(EntityType.Capsule), new AvoidEntity(EntityType.Ghost), new RandomDirectionOrder(), new EntityTargetWeight(EntityType.Capsule), preset);
            case SearchFood: return new PathFindingBehavior(new EntityDestination(EntityType.Food), new AvoidEntity(EntityType.Ghost), new RandomDirectionOrder(), new EntityTargetWeight(EntityType.Food), preset);
            case SearchGhosts: return new PathFindingBehavior(new EntityDestination(EntityType.Ghost), new MoveCheck(), new RandomDirectionOrder(), new EntityTargetWeight(EntityType.Ghost), preset);
            case SearchPacmans: return new PathFindingBehavior(new EntityDestination(EntityType.Pacman), new AvoidEntity(EntityType.Ghost), new RandomDirectionOrder(), new EntityTargetWeight(EntityType.Pacman), preset);
            case FleePacman: return new PathFindingBehavior(new EntityFleeingDestination(EntityType.Pacman, 20), new MoveCheck(), new RandomDirectionOrder(), new EntityFleeingWeight(EntityType.Pacman), preset);
            default: return create_behavior(BehaviorType.Pathfinding);
        }
    }
}