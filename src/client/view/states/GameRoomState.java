package client.view.states;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.protocol.Queries.GameStateQuery;
import model.protocol.Queries.GoToRoomQuery;

public abstract class GameRoomState extends WindowState {
    String room_name;

    public GameRoomState(Window window, ClientSocketThread socket, String room_name) {
        super(window, socket);
        this.room_name = room_name;

        new GameStateQuery().send(socket);
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
}
