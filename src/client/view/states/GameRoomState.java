package client.view.states;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.game.maze.Maze;
import model.protocol.Queries.CosmeticsQuery;
import model.protocol.Queries.GameStateQuery;
import model.protocol.Queries.GoToRoomQuery;

public abstract class GameRoomState extends WindowState {
    String room_name;
    protected Color[] maze_colors;

    public GameRoomState(Window window, ClientSocketThread socket, String room_name) {
        super(window, socket);
        this.room_name = room_name;

        new GameStateQuery().send(socket);
        new CosmeticsQuery().send(socket);
    }
    public GameRoomState(GameRoomState previous_state) {
        super(previous_state.window, previous_state.socket);
        this.room_name = previous_state.room_name;
        this.maze_colors = previous_state.maze_colors;

        new GameStateQuery().send(socket);
        new CosmeticsQuery().send(socket);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        frame.setTitle(Window.WINDOW_TITLE + " - " + room_name);

        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("options");
        JMenuItem quit = new JMenuItem("quitter");
        menu.add(quit);
        menubar.add(menu);
        frame.setJMenuBar(menubar);

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                new GoToRoomQuery(GoToRoomQuery.RoomType.Loby).send(socket);
            }
        });
    }

    @Override
    protected boolean onReceiveGoToRoom(GoToRoomQuery query, ClientSocketThread socket) {
        if (query.getSuccess() && query.toLoby()) {
            window.changeState(new LobyState(window, socket));
        }
        return true;
    }
    @Override
    protected boolean onReceiveCosmeticsQuery(CosmeticsQuery query, ClientSocketThread socket) {
        this.maze_colors = query.getMazeColors(Maze.get_default_colors());
        System.out.println("maze_colors[0]: " + maze_colors[0].toString());
        System.out.println("maze_colors[1]: " + maze_colors[1].toString());
        return true;
    }
}
