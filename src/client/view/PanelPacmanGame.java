package client.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

import model.game.agent.AgentAction.Direction;
import model.game.agent.PositionAgent;
import model.game.maze.Maze;

public class PanelPacmanGame extends JPanel {
	private static final long serialVersionUID = 1L;

	private Color wallColor = Color.BLUE;
	private Color wallColor2 = Color.CYAN;
	private double wallThickness = .15;

	private double sizeAgents = .9;
	private double pacmanMaxAngle = 55;
	private double sizeGhostWidth = .8;
	private double sizeGhostEyeWidth = .1;
	private double sizeGhostEyeHeight = .3;
	private Color pacmansColor = Color.yellow;

	private Color outlineColor = Color.green;
	private double sizeOutline = 1;

	private Color ghostScarredColor = Color.white;

	private double sizeFood = 0.3;
	private Color colorFood = Color.white;

	private double sizeCapsule = 0.7;
	private Color colorCapsule = Color.red;

	private Maze m;
	private int turn = 0;

	private ArrayList<PositionAgent> pacmans_pos;
	private ArrayList<PositionAgent> ghosts_pos;
	private ArrayList<Color> ghosts_colors;

	private PositionAgent target_agent = null;

	private boolean ghostsScarred;
	
	public PanelPacmanGame(Maze maze) {
		this.m = maze;
		pacmans_pos = this.m.getPacman_start();
		ghosts_pos = this.m.getGhosts_start();
		ghosts_colors = this.m.getGhosts_colors();
		ghostsScarred = false;
	}

	private double draw_cell_width;
	private double draw_cell_height;
	void paint_image_base(Graphics g) {
		int dx = getSize().width;
		int dy = getSize().height;

		g.setColor(Color.black);
		g.fillRect(0, 0, dx, dy);

		int sx = m.getSizeX();
		int sy = m.getSizeY();
		draw_cell_width = dx / (double) sx;
		draw_cell_height = dy / (double) sy;

		for (int x = 0; x < sx; x++) {
			for (int y = 0; y < sy; y++) {
				drawCell(g, x, y);
			}
		}
	}
	void paint_image_agents(Graphics g) {
		if (target_agent != null) {
			drawOutline(g, target_agent.getX(), target_agent.getY(), outlineColor, sizeOutline);
		}

		for (int i = 0; i < pacmans_pos.size(); i++) {
			PositionAgent pos = pacmans_pos.get(i);
			drawPacmans(g, pos.getX(), pos.getY(), pos.getDir(), pacmansColor, sizeAgents);
		}

		for (int i = 0; i < ghosts_pos.size(); i++) {
			PositionAgent pos = ghosts_pos.get(i);
			drawGhosts(g, pos.getX(), pos.getY(), pos.getDir(), ghostsScarred ? ghostScarredColor : ghosts_colors.get(i), sizeAgents);
		}
	}
	void clear_image_agents(Graphics g) {
		for (int i = 0; i < pacmans_pos.size(); i++) {
			PositionAgent pos = pacmans_pos.get(i);
			int x = pos.getX();
			int y = pos.getY();
			g.setColor(Color.black);
			g.fillRect((int)(x * draw_cell_width), (int)(y * draw_cell_height), (int)draw_cell_width + 1, (int)draw_cell_height + 1);
			
			for (int a = -1; a <= 1; a++)
			for (int b = -1; b <= 1; b++)
				drawCell(g, x+a, y+b);
		}

		for (int i = 0; i < ghosts_pos.size(); i++) {
			PositionAgent pos = ghosts_pos.get(i);
			int x = pos.getX();
			int y = pos.getY();
			g.setColor(Color.black);
			g.fillRect((int)(x * draw_cell_width), (int)(y * draw_cell_height), (int)draw_cell_width + 1, (int)draw_cell_height + 1);

			for (int a = -1; a <= 1; a++)
			for (int b = -1; b <= 1; b++)
				drawCell(g, x+a, y+b);
		}
	}
	public void paint(Graphics g) {
		paint_image_base(g);
		paint_image_agents(g);
		// g.drawImage(image, 0, 0, null);
		// clear_image_agents(g);
	}

	void drawOutline(Graphics g, int px, int py, Color color, double size) {
		if((px != -1) || (py != -1)){
			double posx = px * draw_cell_width;
			double posy = py * draw_cell_height;
	
			g.setColor(color);
			double nsx = draw_cell_width * size;
			double nsy = draw_cell_height * size;
			double npx = (draw_cell_width - nsx) / 2.0;
			double npy = (draw_cell_height - nsy) / 2.0;
		
			g.drawRoundRect((int)(npx + posx), (int)(npy + posy), (int)nsx, (int)nsy, 5, 5);
		}
	}
	void drawPacmans(Graphics g, int px, int py, Direction pacmanDirection, Color color, double size) {
		if((px != -1) || (py != -1)){
			double posx = px * draw_cell_width;
			double posy = py * draw_cell_height;
	
			g.setColor(color);
			double nsx = draw_cell_width * size;
			double nsy = draw_cell_height * size;
			double npx = (draw_cell_width - nsx) / 2.0;
			double npy = (draw_cell_height - nsy) / 2.0;
			
			double angle = 0;
			if (turn % 4 == 0) angle = 0;
			else if (turn % 4 == 2) angle = pacmanMaxAngle;
			else angle = pacmanMaxAngle * .5;
			
			double start;
			double length = (angle - 360);
			if (pacmanDirection == Direction.NORTH) {
				start = 90 - angle * .5;
			}
			else if (pacmanDirection == Direction.SOUTH) {
				start = 270 - angle * .5;
			}
			else if (pacmanDirection == Direction.EAST) {
				start = 360 - angle * .5;
			}
			else if (pacmanDirection == Direction.WEST) {
				start = 180 - angle * .5;
			}
			else {
				start = 0;
				length = 360;
			}
		
	
			g.fillArc((int) (npx + posx), (int) (npy + posy), (int) (nsx),
					(int) nsy, (int)start, (int)length);
		}

	}
	void drawGhosts(Graphics g, int px, int py, Direction direction, Color color, double size) {
		if((px != -1) || (py != -1)){
			double posx = (px + .5 - size * sizeGhostWidth / 2) * draw_cell_width;
			double posy = (py + .5 - size / 2) * draw_cell_height;
			
			g.setColor(color);
			
			double width = draw_cell_width * size * sizeGhostWidth;
			double height = draw_cell_height * size;
			
			
			g.fillArc((int)posx, (int)posy, (int)width, (int)(height * sizeGhostWidth), 0, 180);
			g.fillRect((int)posx, (int)(posy + height * sizeGhostWidth / 2 - 1), (int)width + 1, (int)(height * (1 - sizeGhostWidth / 2)) + 1);
			
			g.setColor(Color.BLACK);
			double centerx = (px + .5) * draw_cell_width;
			if (direction == Direction.WEST) centerx -= .1 * draw_cell_width;
			if (direction == Direction.EAST) centerx += .1 * draw_cell_width;
			double centery = (py + .5) * draw_cell_height;
			if (direction == Direction.SOUTH) centery += .1 * draw_cell_height;
			if (direction == Direction.NORTH) centery -= .1 * draw_cell_height;

			double offset = .2 * size * sizeGhostWidth * draw_cell_width;
			double w = sizeGhostEyeWidth * draw_cell_width;
			double h = sizeGhostEyeHeight * draw_cell_height;

			g.fillOval((int)(centerx + offset - w * .5), (int)(centery - h * .5), (int)w, (int)h);
			g.fillOval((int)(centerx - offset - w * .5), (int)(centery - h * .5), (int)w, (int)h);

			g.setColor(Color.black);
		}
	}
	void drawCell(Graphics g, int x, int y) {
		if (m.isWall(x, y)) drawWall(g, x, y);
		if (m.isFood(x, y)) drawFood(g, x, y);
		if (m.isCapsule(x, y)) drawCapsule(g, x, y);
	}
	void drawFood(Graphics g, int px, int py) {
		double posx = (px + .5 - sizeFood * .5) * draw_cell_width;
		double posy = (py + .5 - sizeFood * .5) * draw_cell_height;
		
		g.setColor(colorFood);
		g.fillOval((int)posx, (int)posy, (int)(sizeFood * draw_cell_width), (int)(sizeFood * draw_cell_height));
	}
	void drawCapsule(Graphics g, int px, int py) {
		double posx = (px + .5 - sizeCapsule * .5) * draw_cell_width;
		double posy = (py + .5 - sizeCapsule * .5) * draw_cell_height;
		
		g.setColor(colorCapsule);
		g.fillOval((int)posx, (int)posy, (int)(sizeCapsule * draw_cell_width), (int)(sizeCapsule * draw_cell_height));
	}
	void drawWall(Graphics g, int px, int py) {
		double posx = px * draw_cell_width;
		double posy = py * draw_cell_height;
		g.setColor(wallColor);
		g.fillRect((int)posx, (int)posy, (int)(draw_cell_width + 1), (int)(draw_cell_height + 1));

		g.setColor(wallColor2);
		if (!m.isWall(px+1, py)) g.fillRect((int)(posx + (1-wallThickness) * draw_cell_width + 1), (int)posy, (int)(wallThickness * draw_cell_width + 1), (int)(draw_cell_height + 1));
		if (!m.isWall(px-1, py)) g.fillRect((int)posx, (int)posy, (int)(wallThickness * draw_cell_width + 1), (int)(draw_cell_height + 1));
		if (!m.isWall(px, py+1)) g.fillRect((int)posx, (int)(posy + (1-wallThickness) * draw_cell_height + 1), (int)(draw_cell_width + 1), (int)(wallThickness * draw_cell_height + 1));
		if (!m.isWall(px, py-1)) g.fillRect((int)posx, (int)posy, (int)(draw_cell_width + 1), (int)(wallThickness * draw_cell_height + 1));
	}

	public Maze getMaze(){
		return m;
	}
	
	public void setMaze(Maze maze){
		this.m = maze;
	}
	public void setTurn(int turn){
		this.turn = turn;
	}
	
	public void setGhostsScarred(boolean ghostsScarred) {
		this.ghostsScarred = ghostsScarred;
	}

	public ArrayList<PositionAgent> getPacmans_pos() {
		return pacmans_pos;
	}

	public void setPacmans_pos(ArrayList<PositionAgent> pacmans_pos) {
		this.pacmans_pos = pacmans_pos;				
	}

	public ArrayList<PositionAgent> getGhosts_pos() {
		return ghosts_pos;
	}

	public void setGhosts_colors(ArrayList<Color> ghosts_colors) {
		this.ghosts_colors = ghosts_colors;
	}

	public void setGhosts_pos(ArrayList<PositionAgent> ghosts_pos) {
		this.ghosts_pos = ghosts_pos;
	}

	public void setTarget(PositionAgent target) {
		this.target_agent = target;
	}
}
