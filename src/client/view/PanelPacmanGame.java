package client.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

import model.game.agent.Agent;
import model.game.agent.AgentAction.Direction;
import model.game.agent.PositionAgent;
import model.game.maze.Maze;

public class PanelPacmanGame extends JPanel {
	private static final long serialVersionUID = 1L;

	private double wallThickness = .15;

	private double sizeAgents = .9;
	private double pacmanMaxAngle = 55;
	private double sizeGhostWidth = .8;
	private double sizeGhostEyeWidth = .1;
	private double sizeGhostEyeHeight = .3;

	private Color ghostScarredColor = Color.white;

	private double sizeFood = 0.3;
	private Color colorFood = Color.white;

	private double sizeCapsule = 0.7;
	private Color colorCapsule = Color.red;

	private Maze m;

	private ArrayList<Agent> pacmans;
	private ArrayList<Agent> ghosts;

	public PanelPacmanGame(Maze maze) {
		this.m = maze;
		pacmans = this.m.getPacmans();
		ghosts = this.m.getGhosts();
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
		for (Agent a : pacmans) {
			drawPacmans(g, a, sizeAgents);
		}
		for (Agent a : ghosts) {
			drawGhosts(g, a, sizeAgents);
		}
	}
	void clear_image_agents(Graphics g) {
		for (Agent agent : pacmans) {
			PositionAgent pos = agent.get_position();
			int x = pos.getX();
			int y = pos.getY();
			g.setColor(Color.black);
			g.fillRect((int)(x * draw_cell_width), (int)(y * draw_cell_height), (int)draw_cell_width + 1, (int)draw_cell_height + 1);
			
			for (int a = -1; a <= 1; a++)
			for (int b = -1; b <= 1; b++)
				drawCell(g, x+a, y+b);
		}

		for (Agent agent : ghosts) {
			PositionAgent pos = agent.get_position();
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

	void drawPacmans(Graphics g, Agent agent, double size) {
		int px = agent.get_position().getX();
		int py = agent.get_position().getY();
		Color[] colors = agent.get_colors();

		if((px != -1) || (py != -1)){
			double posx = px * draw_cell_width;
			double posy = py * draw_cell_height;
	
			g.setColor(colors[0]);
			double nsx = draw_cell_width * size;
			double nsy = draw_cell_height * size;
			double npx = (draw_cell_width - nsx) / 2.0;
			double npy = (draw_cell_height - nsy) / 2.0;
			
			double angle = 0;
			if (m.getTurn() % 4 == 0) angle = 0;
			else if (m.getTurn() % 4 == 2) angle = pacmanMaxAngle;
			else angle = pacmanMaxAngle * .5;
			
			double start;
			double length = (angle - 360);
			if (agent.get_position().getDir() == Direction.NORTH) {
				start = 90 - angle * .5;
			}
			else if (agent.get_position().getDir() == Direction.SOUTH) {
				start = 270 - angle * .5;
			}
			else if (agent.get_position().getDir() == Direction.EAST) {
				start = 360 - angle * .5;
			}
			else if (agent.get_position().getDir() == Direction.WEST) {
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
	void drawGhosts(Graphics g, Agent agent, double size) {
		int px = agent.get_position().getX();
		int py = agent.get_position().getY();
		Color[] colors = agent.get_colors();

		if((px != -1) || (py != -1)){
			double posx = (px + .5 - size * sizeGhostWidth / 2) * draw_cell_width;
			double posy = (py + .5 - size / 2) * draw_cell_height;
			
			g.setColor(m.getGhostsScarred() ? ghostScarredColor : colors[0]);
			
			double width = draw_cell_width * size * sizeGhostWidth;
			double height = draw_cell_height * size;
			
			
			g.fillArc((int)posx, (int)posy, (int)width, (int)(height * sizeGhostWidth), 0, 180);
			g.fillRect((int)posx, (int)(posy + height * sizeGhostWidth / 2 - 1), (int)width + 1, (int)(height * (1 - sizeGhostWidth / 2)) + 1);
			
			g.setColor(colors[1]);
			double centerx = (px + .5) * draw_cell_width;
			if (agent.get_position().getDir() == Direction.WEST) centerx -= .1 * draw_cell_width;
			if (agent.get_position().getDir() == Direction.EAST) centerx += .1 * draw_cell_width;
			double centery = (py + .5) * draw_cell_height;
			if (agent.get_position().getDir() == Direction.SOUTH) centery += .1 * draw_cell_height;
			if (agent.get_position().getDir() == Direction.NORTH) centery -= .1 * draw_cell_height;

			double offset = .2 * size * sizeGhostWidth * draw_cell_width;
			double w = sizeGhostEyeWidth * draw_cell_width;
			double h = sizeGhostEyeHeight * draw_cell_height;

			g.fillOval((int)(centerx + offset - w * .5), (int)(centery - h * .5), (int)w, (int)h);
			g.fillOval((int)(centerx - offset - w * .5), (int)(centery - h * .5), (int)w, (int)h);
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
		Color[] colors = m.get_colors();

		g.setColor(colors[0]);
		g.fillRect((int)posx, (int)posy, (int)(draw_cell_width + 1), (int)(draw_cell_height + 1));

		g.setColor(colors[1]);
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
		setPacmans(maze.getPacmans());
		setGhosts(maze.getGhosts());
	}
	
	public void setPacmans(ArrayList<Agent> pacmans) {
		this.pacmans = pacmans;				
	}
	public void setGhosts(ArrayList<Agent> ghosts) {
		this.ghosts = ghosts;
	}
}
