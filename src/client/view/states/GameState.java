package client.view.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.game.maze.Maze;
import model.protocol.data.PlayerInfo;
import model.protocol.queries.ChoseRoleQuery.Choice;
import model.protocol.queries.CosmeticsQuery;
import model.protocol.queries.GameStateQuery;
import model.protocol.queries.GoToRoomQuery;
import model.protocol.queries.PlayerListQuery;

public abstract class GameState extends WindowState {
    String room_name;
    protected Color[] maze_colors;

    public GameState(Window window, ClientSocketThread socket, String room_name) {
        super(window, socket);
        this.room_name = room_name;

        new GameStateQuery().send(socket);
        new CosmeticsQuery().send(socket);
    }
    public GameState(GameState previous_state) {
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
    JPanel sidePlayerList = null;
    public JPanel createPlayerListPanel() {
        sidePlayerList = new JPanel();
        sidePlayerList.setLayout(new BoxLayout(sidePlayerList, BoxLayout.Y_AXIS));
        sidePlayerList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return sidePlayerList;
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
    @Override
    protected boolean onReceivePlayerList(PlayerListQuery query, ClientSocketThread socket) {
        refreshPlayerList(query.getPlayersInfos());
        return true;
    }
    protected void refreshPlayerList(ArrayList<PlayerInfo> infos) {
        if (sidePlayerList == null) return;
        sidePlayerList.removeAll();
        
        for (PlayerInfo info : infos) {
            JPanel player = new JPanel();
            player.setLayout(new BoxLayout(player, BoxLayout.X_AXIS));

            JLabel name = new JLabel(info.name);
            if (info.name.equals(this.socket.getPlayerLogin())) {
                Font font = name.getFont();
                Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                name.setFont(font.deriveFont(attributes));
            }
            player.add(name);

            if (info.choice == Choice.Ghost) player.add(new JLabel(" (Fantôme)"));
            else if (info.choice == Choice.Pacman) player.add(new JLabel(" (Pacman)"));

            sidePlayerList.add(player);
        }

        window.repaint();
    }
}
