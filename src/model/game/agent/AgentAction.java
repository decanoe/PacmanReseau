package model.game.agent;

/** a class representing an action that can be executed by an agent */
public class AgentAction {
	/** the x offset of the movement */
	private int _vx;
	/** the y offset of the movement */
	private int _vy;

	/** an enum containing all the direction along which an agent can move */
	public static enum Direction {
		NORTH,
		SOUTH,
		EAST,
		WEST,
		STOP,
	}

	/** the direction of the movement */
	private Direction _direction;

	/**
	 * creates an AgentAction from a direction
	 * @param d the direction of the action
	*/
	public AgentAction(Direction d) {

		_direction = d;

		// Calcul le vecteur de déplacement associé

		switch (_direction) {
		case NORTH:
			_vx = 0;
			_vy = -1;
			break;
		case SOUTH:
			_vx = 0;
			_vy = 1;
			break;
		case EAST:
			_vx = 1;
			_vy = 0;
			break;
		case WEST:
			_vx = -1;
			_vy = 0;
			break;
		case STOP:
			_vx = 0;
			_vy = 0;
			break;
		default:
			_vx = 0;
			_vy = 0;
			break;
		}
	}

	/**
	 * gets the x offset of the movement
	 * @return the x offset of the movement
	*/
	public int get_vx() {
		return _vx;
	}
	/**
	 * sets the x offset of the movement
	 * @param x the x offset of the movement
	*/
	public void set_vx(int _vx) {
		this._vx = _vx;
	}

	/**
	 * gets the y offset of the movement
	 * @return the y offset of the movement
	*/
	public int get_vy() {
		return _vy;
	}
	/**
	 * sets the y offset of the movement
	 * @param y the y offset of the movement
	*/
	public void set_vy(int _vy) {
		this._vy = _vy;
	}

	/**
	 * gets the direction
	 * @return the direction the movement
	*/
	public Direction get_direction() {
		return _direction;
	}
	/**
	 * sets the direction of the movement
	 * @param direction the direction of the movement
	*/
	public void set_direction(Direction direction) {
		this._direction = direction;
	}
}
