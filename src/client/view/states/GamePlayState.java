package client.view.states;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.socket.ClientSoketThread;
import client.view.PanelPacmanGame;
import client.view.Window;
import model.game.maze.Maze;
import model.protocol.Queries.ChoseRoleQuery;
import model.protocol.Queries.GameStateQuery;

public class GamePlayState extends GameRoomState {
    protected ChoseRoleQuery.Choice choice = ChoseRoleQuery.Choice.None;
    protected Maze maze;

    public GamePlayState(Window window, ClientSoketThread soket, String room_name, Maze maze) {
        super(window, soket, room_name);
        this.maze = maze;
    }

    PanelPacmanGame pacman_panel;
    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        super.createInterface(panel, frame);
        panel.setLayout(null);

        Dimension dimension = compute_dimensions(maze.getSizeX(), maze.getSizeY());
        resize_window(frame, dimension);

        pacman_panel = new PanelPacmanGame(maze);
        panel.add(pacman_panel);
        pacman_panel.setBounds(0, 0, dimension.width, dimension.height);
    }
    protected Dimension compute_dimensions(int maze_width, int maze_height) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int max_height = ge.getMaximumWindowBounds().height;
        int max_width = ge.getMaximumWindowBounds().width;
        
        if (max_height * maze_width / maze_height > max_width) return new Dimension(max_width, max_width * maze_height / maze_width);
        else return new Dimension(max_height * maze_width / maze_height, max_height);
    }
    protected void resize_window(JFrame frame, Dimension dimension) {
        frame.setSize(dimension);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int dx = ge.getCenterPoint().x - dimension.width / 2;
        int dy = ge.getCenterPoint().y - dimension.height / 2;
        frame.setLocation(dx, dy);
    }

    @Override
    protected boolean onReceiveGameState(GameStateQuery query, ClientSoketThread socket) {
        if (!query.isGameRunning()) {
            window.changeState(new GameWaitState(window, socket, room_name));
        }
        return true;
    }
}
