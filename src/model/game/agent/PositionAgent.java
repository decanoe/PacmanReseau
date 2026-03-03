package model.game.agent;

import java.io.Serializable;

import model.game.agent.AgentAction.Direction;
import model.game.maze.Maze;

/** a class representing a position and a direction of an agent */
public class PositionAgent implements Serializable {

	private static final long serialVersionUID = 1L;

	/** the x coordinate of the position */
	private int x;
	/** the y coordinate of the position */
	private int y;
	/** the direction of the agent */
	private Direction dir;

	/**
	 * creates an PositionAgent from its coordinates and its direction
	 * @param x the x coordinate of the position
	 * @param y the y coordinate of the position
	 * @param dir the direction of the position
	*/
	public PositionAgent(int x, int y, Direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	/**
	 * creates an PositionAgent from its coordinates
	 * @param x the x coordinate of the position
	 * @param y the y coordinate of the position
	*/
	public PositionAgent(int x, int y) {
		this(x, y, null);
	}
	
	/**
	 * creates the PositionAgent resulting of the application the given action on this position
	 * @param action the action to apply
	 * @return a new PositionAgent resulting of the givent action
	*/
	public PositionAgent add_action(AgentAction action) {
		return new PositionAgent(x + action.get_vx(), y + action.get_vy(), action.get_direction());
	}

	/**
	 * gets the x coordinate of the position
	 * @return the x coordinate of the position
	*/
	public int getX() {
		return x;
	}
	/**
	 * sets the x coordinate of the position
	 * @param x the x coordinate of the position
	*/
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * gets the y coordinate of the position
	 * @return the y coordinate of the position
	*/
	public int getY() {
		return y;
	}
	/**
	 * sets the y coordinate of the position
	 * @param y the y coordinate of the position
	*/
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * gets the direction of the position
	 * @return the direction  of the position
	*/
	public Direction getDir() {
		return dir;
	}
	/**
	 * sets the direction of the position
	 * @param dir the direction of the position
	*/
	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	/**
	 * returns the manhattan distance to another position
	 * @param other the other position
	 * @return the manhattan distance the another position
	*/
	public int manhattanDistance(PositionAgent other, Maze maze) {
		int dx = Math.abs(x - other.x);
		int dy = Math.abs(y - other.y);
		if (maze.getWarpX()) dx = Math.min(dx, maze.getSizeX() - dx);
		if (maze.getWarpY()) dy = Math.min(dy, maze.getSizeY() - dy);
		return dx + dy;
	}
	/**
	 * returns the euclidian distance to another position
	 * @param other the other position
	 * @return the euclidian distance the another position
	*/
	public double distance(PositionAgent other, Maze maze) {
		int dx = Math.abs(x - other.x);
		int dy = Math.abs(y - other.y);
		if (maze.getWarpX()) dx = Math.min(dx, maze.getSizeX() - dx);
		if (maze.getWarpY()) dy = Math.min(dy, maze.getSizeY() - dy);
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public boolean equals(PositionAgent other) {
		return (x == other.x) && (y == other.y);
	}
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final PositionAgent other = (PositionAgent) obj;
		return (x == other.x) && (y == other.y);
    }
}
