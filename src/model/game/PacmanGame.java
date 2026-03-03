package model.game;

import java.util.ArrayList;

import model.game.agent.*;
import model.game.agent.AgentAction.Direction;
import model.game.agent.behavior.*;
import model.game.agent.behavior.BehaviorFactory.PathfindingPreset;
import model.game.maze.Maze;
import model.game.maze.Maze.EntityType;

/**
 * An implementation of Game for a pacman game
 * <p>
 * Contains support for multiple PropertyChangeListener
 * <ul>
 * <li>"maze" the used Maze fired when initialisation occures</li>
 * <li>"ghost_scared" a boolean indicating wether ghosts are scared</li>
 * <li>"game_over" an int fired when a game over occurs (with the tun it occured)</li>
 * @see Game Game doc for a list of all base property
 */
public class PacmanGame extends Game {
    /** the path to the layout of the maze */
    protected String layout_path;
    /** the maze of this game */
    protected Maze maze;
    /** the number of turns until the ghosts stop being scared (-1 if they are not scared) */
    protected int ghost_scared_counter = -1;
    /** an array with all Agents present in the maze */
    public ArrayList<Agent> agents;

    /**
     * a getter to the maze
     * @return the maze of the game
     */
    public Maze get_maze() { return maze; }
    /**
     * sets the maze path ot a new path
     * <p>
     * init should be called after this operation
     * @param layout_path the path to the layout of the new maze
     */
    public void set_layout_path(String layout_path) { this.layout_path = layout_path; }

    /**
     * Constructs a Game with a path to a maze layout and a running speed
     * <p>
     * At creation, the game isn't initialized and is not running
     * @param layout_path the path to the layout of the maze for the game
     * @param speed an double representing the time (in seconds) between turns when running
     */
    public PacmanGame(String layout_path, double speed) {
        super(speed);
        this.layout_path = layout_path;
        agents = new ArrayList<>();
    }

    @Override
    protected void initialize_game() {
        Maze old_maze = maze;
        clear_ghost_scared();
        try {
            maze = new Maze(layout_path);
            agents.clear();

            for (PositionAgent p : maze.getPacman_start()) {
                Behavior chased_behavior = new DualDistanceBehavior(
                    BehaviorFactory.create_pathfinding(PathfindingPreset.SearchCapsule),
                    BehaviorFactory.create_pathfinding(PathfindingPreset.SearchFood),
                    EntityType.Ghost, 5);
                Behavior chase_behavior = new DualDistanceBehavior(
                    BehaviorFactory.create_pathfinding(PathfindingPreset.SearchGhosts),
                    BehaviorFactory.create_pathfinding(PathfindingPreset.SearchFood),
                    EntityType.Ghost, 10);
                
                Behavior b = new DualScaredBehavior(chased_behavior, chase_behavior);
                
                agents.add(new PacmanAgent(p).set_behavior(b));
            }
            for (PositionAgent p : maze.getGhosts_start()) {
                Behavior b = new DualScaredBehavior(
                    BehaviorFactory.create_pathfinding(PathfindingPreset.SearchPacmans),
                    BehaviorFactory.create_pathfinding(PathfindingPreset.FleePacman)
                    );

                agents.add(new GhostAgent(p).set_behavior(b));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        support.firePropertyChange("maze", old_maze, maze);
    }
    
    @Override
    protected void take_turn() {
        if (ghost_scared_counter > 0) ghost_scared_counter--;

        ArrayList<AgentAction> actions = new ArrayList<>();
        for (Agent agent : agents) {
            actions.add(agent.get_action(maze, are_ghost_scared()));
        }
        for (Agent agent : agents) {
            AgentAction action = actions.removeFirst();
            if (maze.isLegalMove(agent.get_position(), action)) moveAgent(agent, action);
            else {
                action = agent.get_default_action();
                if (maze.isLegalMove(agent.get_position(), action)) moveAgent(agent, action);
                else moveAgent(agent, new AgentAction(Direction.STOP));
            }
        }

        manage_agent_collisions();
        remove_dead_agents();

        if (ghost_scared_counter == 0) clear_ghost_scared();
    }
    /**
     * Applies the action to the specified agent, eating static entities on its way if possible
     * @param agent the agent to move
     * @param action the action to apply
     */
    protected void moveAgent(Agent agent, AgentAction action) {
        agent.move(action);
        maze.warpPosition(agent.get_position());

        int x = agent.get_position().getX();
        int y = agent.get_position().getY();

        EntityType entity = maze.getStaticEntity(x, y);
        if (agent.can_eat(entity, are_ghost_scared())) {
            maze.clearStaticEntity(x, y);
            if (entity == EntityType.Capsule) set_ghost_scared();
        }
    }
    /**
     * Wether two agents have collided during or after their last move
     * @param agent1 the first agent
     * @param agent2 the second agent
     * @return True if the two agents have collided, False else
     */
    protected boolean have_collided(Agent agent1, Agent agent2) {
        if (agent1.get_position().equals(agent2.get_position())) return true; // 2 agents in same space
        if (agent1.get_last_position().equals(agent2.get_position()) && agent2.get_last_position().equals(agent1.get_position())) return true; // 2 agents switched positions
        return false;
    }
    /**
     * Check all collisions between agents and kill agents that have been eated
     */
    protected void manage_agent_collisions() {
        for (Agent agent1 : agents)
        for (Agent agent2 : agents) {
            if (!have_collided(agent1, agent2)) continue;

            if (agent1.can_eat(agent2.get_type(), are_ghost_scared())) {
                agent2.kill();
            }
            else if (agent2.can_eat(agent1.get_type(), are_ghost_scared())) {
                agent1.kill();
            }
        }
    }
    /**
     * Removes all agents that have been killed
     */
    protected void remove_dead_agents() {
        var iter = agents.listIterator();

        boolean death = false;
        while (iter.hasNext()) {
            Agent agent = iter.next();
            if (agent.is_dead()) {
                iter.remove();
                death = true;
            }
        }
        if (death) {
            maze.removeOutsideAgents();
        }
    }

    /**
     * Set the ghost scared timer to its defined value of 20 turns
     * <p>
     * Fires a property change on "ghost_scared"
     */
    protected void set_ghost_scared() {
        ghost_scared_counter = 20;
        support.firePropertyChange("ghost_scared", false, true);
    }
    /**
     * Remove the ghost scared state
     * <p>
     * Fires a property change on "ghost_scared"
     */
    protected void clear_ghost_scared() {
        ghost_scared_counter = -1;
        support.firePropertyChange("ghost_scared", true, false);
    }
    /**
     * Wether the ghosts are scared
     * @return True if the ghosts are scared, False else
     */
    protected boolean are_ghost_scared() {
        return ghost_scared_counter >= 0;
    }

    @Override
    protected boolean game_continue() {
        boolean food_left = false;
        for (int x = 0; x < maze.getSizeX() && !food_left; x++)
        for (int y = 0; y < maze.getSizeY() && !food_left; y++) {
            if (maze.isFood(x, y) || maze.isCapsule(x, y)) food_left = true;
        }
        if (!food_left) return false;

        boolean pacman_left = false;
        for (Agent agent : agents) {
            if (agent.get_type() == EntityType.Pacman) pacman_left = true;
        }
        if (!pacman_left) return false;
        return true;
    }
    
    @Override
    protected void game_over() {
        System.out.println("PacmanGame.game_over() -> at turn " + turn);
        support.firePropertyChange("game_over", turn-1, turn);
    }
}
