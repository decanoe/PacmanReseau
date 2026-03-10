package model.game.maze;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.game.agent.Agent;
import model.game.agent.AgentAction;
import model.game.agent.GhostAgent;
import model.game.agent.PacmanAgent;
import model.game.agent.PositionAgent;
import model.game.agent.AgentAction.Direction;

/**
 * The model of the maze
 */
public class Maze implements Serializable, Cloneable {
	/** An enum for each entity present in the maze (and empty for specific return values) */
    public enum EntityType { Pacman, Ghost, Capsule, Food, Wall, Empty }

	private static final long serialVersionUID = 1L;

	/** The width of the maze (in cells) */
	private int size_x;
	/** The height of the maze (in cells) */
	private int size_y;

	/** a 2d array of boolean indicating wether a cell is a wall*/
	private boolean walls[][];
	/** a 2d array of boolean indicating wether a cell is a food*/
	private boolean food[][];
	/** a 2d array of boolean indicating wether a cell is a capsule*/
	private boolean capsules[][];

	/** an array of Agent for the position of the pacmans at loading*/
	private ArrayList<Agent> pacmans;
	/** an array of Agent for the position of the ghosts at loading*/
	private ArrayList<Agent> ghosts;

	/** does the maze warp on the x axis*/
	protected boolean warp_x;
	/** does the maze warp on the y axis*/
	protected boolean warp_y;

	/**
     * Creates a Maze from a path to a layout file
     * @param filename the path to the layout file
     */
    public Maze(String filename) throws Exception {
		try {
			System.out.println("Layout file is " + filename);
			// Lecture du fichier pour determiner la taille du labyrinthe
			InputStream ips = new FileInputStream(filename);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			int nbX = 0;
			int nbY = 0;
			while ((ligne = br.readLine()) != null) {
				ligne = ligne.trim();
				if (nbX == 0) {
					nbX = ligne.length();
				} else if (nbX != ligne.length()) {
					ips.close();
					br.close();
					throw new Exception("Wrong Input Format: all lines must have the same size");
				}
				nbY++;
			}
			br.close();
			System.out.println("### Size of maze is " + nbX + ";" + nbY);

			// Initialisation du labyrinthe
			size_x = nbX;
			size_y = nbY;
			walls = new boolean[size_x][size_y];
			food = new boolean[size_x][size_y];
			capsules = new boolean[size_x][size_y];

			pacmans = new ArrayList<Agent>();
			ghosts = new ArrayList<Agent>();

			// Lecture du fichier pour mettre a jour le labyrinthe
			ips = new FileInputStream(filename);
			ipsr = new InputStreamReader(ips);
			br = new BufferedReader(ipsr);
			int y = 0;
			while ((ligne = br.readLine()) != null) {
				ligne = ligne.trim();

				for (int x = 0; x < ligne.length(); x++) {
					if (ligne.charAt(x) == '%')
						walls[x][y] = true;
					else
						walls[x][y] = false;

					if (ligne.charAt(x) == '.')
						food[x][y] = true;
					else
						food[x][y] = false;

					if (ligne.charAt(x) == 'o')
						capsules[x][y] = true;
					else
						capsules[x][y] = false;

					if (ligne.charAt(x) == 'P') {
						pacmans.add(new PacmanAgent(new PositionAgent(x, y, Direction.STOP)));
					}
					if (ligne.charAt(x) == 'G') {
						ghosts.add(new GhostAgent(new PositionAgent(x, y, Direction.STOP)));
					}

					
					if (ligne.charAt(x) == 'w') {
						if (x == 0) warp_x = true;
						if (y == 0) warp_y = true;
					}
				}
				y++;
			}
			br.close();

			if (pacmans.size() == 0)
				throw new Exception("Wrong input format: must specify a Pacman start");

			// On verifie que le labyrinthe est clos
			// for (int x = 0; x < size_x; x++)
			// 	if (!walls[x][0])
			// 		throw new Exception("Wrong input format: the maze must be closed");
			// for (int x = 0; x < size_x; x++)
			// 	if (!walls[x][size_y - 1])
			// 		throw new Exception("Wrong input format: the maze must be closed");
			// for (y = 0; y < size_y; y++)
			// 	if (!walls[0][y])
			// 		throw new Exception("Wrong input format: the maze must be closed");
			// for (y = 0; y < size_y; y++)
			// 	if (!walls[size_x - 1][y])
			// 		throw new Exception("Wrong input format: the maze must be closed");
			System.out.println("### Maze loaded.");

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error at file loading: " + e.getMessage());
		}
	}
	/**
     * Creates a Maze from a json
     * @param json the json
     */
    public Maze(JSONObject json) {
		// Initialisation du labyrinthe
		size_x = json.getInt("size_x");
		size_y = json.getInt("size_y");
		warp_x = json.getBoolean("warp_x");
		warp_y = json.getBoolean("warp_y");
		walls = new boolean[size_x][size_y];
		food = new boolean[size_x][size_y];
		capsules = new boolean[size_x][size_y];

		pacmans = new ArrayList<Agent>();
		ghosts = new ArrayList<Agent>();
		
		JSONArray statics = json.getJSONArray("statics");
		for (int y = 0; y < size_y; y++) {
			for (int x = 0; x < size_x; x++) {
				String cell = statics.getJSONArray(y).getString(x);

				walls[x][y] = (cell.equals("%"));
				food[x][y] = (cell.equals("."));
				capsules[x][y] = (cell.equals("o"));
			}
		}
		
		JSONObject agents = json.getJSONObject("agents");
		JSONArray pacmans_json = agents.getJSONArray("pacmans");
		JSONArray ghosts_json = agents.getJSONArray("ghosts");

		for (int i = 0; i < pacmans_json.length(); i++) {
			pacmans.add(Agent.fromJSON(pacmans_json.getJSONObject(i)));
		}
		for (int i = 0; i < ghosts_json.length(); i++) {
			ghosts.add(Agent.fromJSON(ghosts_json.getJSONObject(i)));
		}
	}

	/**
	 * Returns the width of the maze (in cells)
	 * @return the width of the maze
	 */
	public int getSizeX() {
		return (size_x);
	}
	/**
	 * Returns the height of the maze (in cells)
	 * @return the height of the maze
	 */
	public int getSizeY() {
		return (size_y);
	}

	/**
	 * Returns true if the maze warps on the x axis
	 * @return wether the maze warps on the x axis
	 */
	public boolean getWarpX() {
		return warp_x;
	}
	/**
	 * Returns true if the maze warps on the y axis
	 * @return wether the maze warps on the y axis
	 */
	public boolean getWarpY() {
		return warp_y;
	}

	/**
	 * wether the cell is a wall
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return True is the cell is a wall
	 */
	public boolean isWall(int x, int y) {
		return (walls[warpX(x)][warpY(y)]);
	}
	/**
	 * wether the cell is a food
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return True is the cell is a food
	 */
	public boolean isFood(int x, int y) {
		return (food[warpX(x)][warpY(y)]);
	}
	/**
	 * wether the cell is a capsule
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return True is the cell is a capsule
	 */
	public boolean isCapsule(int x, int y) {
		return (capsules[warpX(x)][warpY(y)]);
	}

	/**
	 * add or remove the food from a cell
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @param b wether the cell should have a food or not
	 */
	public void setFood(int x, int y, boolean b) {
		food[warpX(x)][warpY(y)] = b;
	}
	/**
	 * add or remove the capsule from a cell
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @param b wether the cell should have a capsule or not
	 */
	public void setCapsule(int x, int y, boolean b) {
		capsules[warpX(x)][warpY(y)] = b;
	}
	
	/**
	 * get the static entity of the cell
	 * <p>
	 * if multiple static entities are present on the same cell, returns the first checked
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @return the static entity of the cell (Wall, Food, Capsule or Empty)
	 */
	public EntityType getStaticEntity(int x, int y) {
		if (isWall(x, y)) return EntityType.Wall;
		if (isFood(x, y)) return EntityType.Food;
		if (isCapsule(x, y)) return EntityType.Capsule;
		return EntityType.Empty;
	}
	/**
	 * removes a static entity from the cell (except for walls)
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 */
	public void clearStaticEntity(int x, int y) {
		if (isWall(x, y)) return;
		else if (isFood(x, y)) setFood(x, y, false);
		else if (isCapsule(x, y)) setCapsule(x, y, false);
	}
	/**
	 * checks if the cell contains the given entity
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @param entityType the entity to check
	 * @return True if the entity is present
	 */
	public boolean isEntity(int x, int y, EntityType entityType) {
		switch (entityType) {
			case Wall: return isWall(x, y);
			case Food: return isFood(x, y);
			case Capsule: return isCapsule(x, y);
			case Ghost: {
				for (Agent agent : ghosts) {
					if (agent.get_position().equals(new PositionAgent(x, y, null))) return true;
				}
			} return false;
			case Pacman:{
				for (Agent agent : pacmans) {
					if (agent.get_position().equals(new PositionAgent(x, y, null))) return true;
				}
			} return false;
			case Empty: return !isWall(x, y);
			default: return false;
		}
	}
	/**
	 * checks if the cell contains at least one of the given entities
	 * @param x the x coordinate of the cell
	 * @param y the y coordinate of the cell
	 * @param entitiesType an array of entities to check
	 * @return True if one of the entities is present
	 */
	public boolean isEntity(int x, int y, ArrayList<EntityType> entitiesType) {
		for (EntityType entity : entitiesType) {
            if (isEntity(x, y, entity)) return true;
        }
		return false;
	}
	/**
	 * get the position of the closest static entity of the given type
	 * @param x the x coordinate of the cell from wich the distance is computed
	 * @param y the y coordinate of the cell from wich the distance is computed
	 * @param entityType the entity to search
	 * @return the position of the closest static entity found of the given type
	 */
	protected PositionAgent getClosestStaticEntity(int x, int y, EntityType entityType) {
		PositionAgent result = null;
		double d = -1;

		for (int i = 0; i < getSizeX(); i++)
		for (int j = 0; j < getSizeX(); j++) {
			if (!isEntity(i, j, entityType)) continue;

            double new_d = new PositionAgent(x, y, null).distance(new PositionAgent(i, j, null), this);
            if (d == -1 || new_d < d) {
				d = new_d;
				result = new PositionAgent(i, j, null);
			}
		}
		return result;
	}
	/**
	 * get the position of the closest agent of the given type
	 * @param x the x coordinate of the cell from wich the distance is computed
	 * @param y the y coordinate of the cell from wich the distance is computed
	 * @param ghost whether to search a ghost or a pacman
	 * @return the position of the closest agent found of the given type
	 */
	protected PositionAgent getClosestAgent(int x, int y, boolean ghost) {
		PositionAgent result = null;
		double d = -1;

        for (Agent agent : ghost ? getGhosts() : getPacmans() ) {
            double new_d = new PositionAgent(x, y, null).distance(agent.get_position(), this);
            if (d == -1 || new_d < d) {
				d = new_d;
				result = new PositionAgent(agent.get_position().getX(), agent.get_position().getY(), null);
			}
        }
		return result;
	}
	/**
	 * get the position of the closest entity of the given type
	 * @param x the x coordinate of the cell from wich the distance is computed
	 * @param y the y coordinate of the cell from wich the distance is computed
	 * @param entityType the entity to search
	 * @return the position of the closest entity found of the given type
	 */
	public PositionAgent getClosestEntity(int x, int y, EntityType entityType) {
		switch (entityType) {
			case Ghost: return getClosestAgent(x, y, true);
			case Pacman: return getClosestAgent(x, y, false);
			default: return getClosestStaticEntity(x, y, entityType);
		}
	}
	
	/**
	 * Removes from the list of agent positions all positions that are not in the maze
	 */
	public void removeOutsideAgents() {
		var p_iter = pacmans.listIterator();
        while (p_iter.hasNext()) {
            PositionAgent pos = p_iter.next().get_position();
            if (pos.getX() < 0 || pos.getY() < 0) {
                p_iter.remove();
            }
        }
		
		var g_iter = ghosts.listIterator();
        while (g_iter.hasNext()) {
            PositionAgent pos = g_iter.next().get_position();
            if (pos.getX() < 0 || pos.getY() < 0) {
                g_iter.remove();
            }
        }
	}

	/**
	 * check wether the given action is legal from the given position
	 * <p>
	 * a move is illegal if it goes through a wall
	 * @param pos the position from wich start the action
	 * @param action the action to check
	 * @return True if the action is legal, False else
	 */
	public boolean isLegalMove(PositionAgent pos, AgentAction action) {
		PositionAgent next_pos = pos.add_action(action);
		warpPosition(next_pos);
        if (isWall(next_pos.getX(), next_pos.getY())) return false;
        return true;
    }
	/**
	 * warps the position arround the edges of the screen
	 * @param pos the position to warp
	 */
	public void warpPosition(PositionAgent pos) {
		pos.setX(warpX(pos.getX()));
		pos.setY(warpY(pos.getY()));
	}
	/**
	 * warps an x coordinate arround the edges of the screen
	 * @param x the x coordinate to warp
	 * @return the warped coordinate
	 */
	int warpX(int x) {
		return x - (int)Math.floor((double)x / size_x) * size_x;
	}
	/**
	 * warps a y coordinate arround the edges of the screen
	 * @param y the y coordinate to warp
	 * @return the warped coordinate
	 */
	int warpY(int y) {
		return y - (int)Math.floor((double)y / size_y) * size_y;
	}

	/**
	 * a getter to the number of pacman present in the maze
	 * @return the number of pacman present in the maze
	 */
	public int getInitNumberOfPacmans() {
		return (pacmans.size());
	}
	/**
	 * a getter to the number of ghosts present in the maze
	 * @return the number of ghosts present in the maze
	 */
	public int getInitNumberOfGhosts() {
		return (ghosts.size());
	}

	/**
	 * a getter to the list of pacman positions in the maze
	 * @return the list of pacman positions in the maze
	 */
	public ArrayList<Agent> getPacmans() {
		return pacmans;
	}
	/**
	 * a getter to the list of ghost positions in the maze
	 * @return the list of ghost positions in the maze
	 */
	public ArrayList<Agent> getGhosts() {
		return ghosts;
	}
	/**
	 * a setter to the list of agents
	 * @param agents the new list of agents
	 */
	public void setAgents(ArrayList<Agent> agents) {
		pacmans = new ArrayList<Agent>();
		ghosts = new ArrayList<Agent>();

		for (Agent agent : agents) {
			if (agent.get_type() == EntityType.Pacman) pacmans.add(agent);
			else ghosts.add(agent);
		}
	}

	public JSONObject toJSON() {
		JSONArray statics = new JSONArray();
		for (int y = 0; y < size_y; y++) {
			JSONArray row = new JSONArray();
			for (int x = 0; x < size_x; x++) {
				if (walls[x][y])
					row.put("%");
				else if (food[x][y])
					row.put(".");
				else if (capsules[x][y])
					row.put("o");
				else
					row.put(" ");
			}
			statics.put(row);
		}
		
		JSONObject agents = new JSONObject();
		JSONArray pacmans_json = new JSONArray();
		JSONArray ghosts_json = new JSONArray();
		agents.put("pacmans", pacmans_json);
		agents.put("ghosts", ghosts_json);

		for (Agent pacman : pacmans) {
			pacmans_json.put(pacman.toJSON());
		}
		for (Agent ghost : ghosts) {
			ghosts_json.put(ghost.toJSON());
		}


		JSONObject json = new JSONObject();
		json.put("statics", statics);
		json.put("agents", agents);

		json.put("size_x", size_x);
		json.put("size_y", size_y);
		json.put("warp_x", warp_x);
		json.put("warp_y", warp_y);

		return json;
	}
	public String toString() {
		String s = "Maze\n";
		s += plateauToString();
		s += "\nPosition agents fantom :";
		for (Agent a : ghosts) {
			s += a.get_position() + " ";
		}
		s += "\nPosition agents pacman :";
		for (Agent a : pacmans) {
			s += a.get_position() + " ";
		}
		return s;
	}
	public String plateauToString() {
		String s = "";
		for (int i = 0; i < size_x; i++) {
			for (int j = 0; j < size_y; j++) {
				if (walls[i][j])
					s += "X";
				else if (food[i][j])
					s += "f";
				else if (capsules[i][j])
					s += "c";
				else
					s += " ";
			}
			s += "\n";
		}
		return s;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return (Maze) super.clone();
	}
}
