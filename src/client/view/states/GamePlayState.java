package client.view.states;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.socket.ClientSocketThread;
import client.view.PanelPacmanGame;
import model.game.agent.AgentAction.Direction;
import model.game.maze.Maze;
import model.protocol.queries.AgentMovementQuery;
import model.protocol.queries.ChoseRoleQuery;
import model.protocol.queries.FullMazeQuery;
import model.protocol.queries.GameStateQuery;
import model.protocol.queries.GameStateQuery.WinState;
import model.protocol.queries.PartialMazeQuery;

public class GamePlayState extends GameState implements KeyListener {
    protected ChoseRoleQuery.Choice choice = ChoseRoleQuery.Choice.None;
    protected Maze maze;

    public GamePlayState(GameState previous_state, Maze maze) {
        super(previous_state);
        this.maze = maze;
    }

    PanelPacmanGame pacman_panel;
    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        super.createInterface(panel, frame);
        panel.setLayout(null);

        frame.revalidate();
        frame.repaint();
        int menu_height = frame.getHeight() - panel.getHeight();
        Dimension window_dimension = compute_dimensions(maze.getSizeX(), maze.getSizeY(), menu_height);
        window.resize_window(window_dimension);

        pacman_panel = new PanelPacmanGame(maze);
        panel.add(pacman_panel);
        pacman_panel.setBounds(0, 0, window_dimension.width, window_dimension.height - menu_height);
    }
    protected Dimension compute_dimensions(int maze_width, int maze_height, int menu_height) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int max_height = ge.getMaximumWindowBounds().height - menu_height;
        int max_width = ge.getMaximumWindowBounds().width;
        
        if (max_height * maze_width / maze_height > max_width) return new Dimension(max_width, max_width * maze_height / maze_width + menu_height);
        else return new Dimension(max_height * maze_width / maze_height, max_height + menu_height);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Direction d = Direction.STOP;
        int key = e.getKeyCode();
        if (key ==      KeyEvent.VK_LEFT)   d = Direction.WEST;
        else if (key == KeyEvent.VK_RIGHT)  d = Direction.EAST;
        else if (key == KeyEvent.VK_UP)     d = Direction.NORTH;
        else if (key == KeyEvent.VK_DOWN)   d = Direction.SOUTH;

        if (d != Direction.STOP) {
            new AgentMovementQuery(d).send(socket);
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    protected boolean onReceiveFullMaze(FullMazeQuery query, ClientSocketThread socket) {
        maze = query.getMaze();
        maze.set_colors(maze_colors);
        pacman_panel.setMaze(maze);
        pacman_panel.repaint();
        return true;
    }
    @Override
    protected boolean onReceivePartialMaze(PartialMazeQuery query, ClientSocketThread socket) {
        if (!maze.applyPartialJSON(query.getPartialMaze())) new FullMazeQuery().send(socket);
        pacman_panel.repaint();
        return true;
    }
    @Override
    protected boolean onReceiveGameState(GameStateQuery query, ClientSocketThread socket) {
        if (!query.isGameRunning()) {
            WinState state = query.getWinState();
            if (state == WinState.None) window.changeState(new GameWaitState(this));
            window.changeState(new GameResultState(this, state == WinState.Pacman));
        }
        return true;
    }
}
