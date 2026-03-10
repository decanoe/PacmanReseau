package client.view.states;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.json.JSONArray;
import org.json.JSONObject;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.protocol.Queries.GoToRoomQuery;
import model.protocol.Queries.RoomListQuery;

public class LobyState extends WindowState {
    JPanel room_list;

    public LobyState(Window window, ClientSocketThread socket) {
        super(window, socket);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        frame.setTitle(Window.WINDOW_TITLE + " - " + "loby");
        panel.setLayout(new GridLayout(3, 1));

        panel.add(new JLabel("Choisissez une partie"));
        
        room_list = new JPanel();
        room_list.setLayout(new BoxLayout(room_list, BoxLayout.Y_AXIS));
        panel.add(new JScrollPane(room_list));

        JButton refresh_button = new JButton("Rafraichir");
        panel.add(refresh_button);

        refresh_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new RoomListQuery().send(socket);
            }
        });
    }

    @Override
    protected boolean onReceiveGoToRoom(GoToRoomQuery query, ClientSocketThread socket) {
        if (query.getSuccess()) {
            window.changeState(new GameWaitState(window, socket, query.getRoomName()));
        }
        return true;
    }
    @Override
    protected boolean onReceiveRoomList(RoomListQuery query, ClientSocketThread socket) {
        refreshRoomList(query.getRoomsInfos());
        return true;
    }
    protected void refreshRoomList(JSONArray rooms) {
        room_list.removeAll();

        for (int i = 0; i < rooms.length(); i++) {
            JSONObject room_info = rooms.getJSONObject(i);

            JPanel room = new JPanel();
            room.setLayout(new BoxLayout(room, BoxLayout.X_AXIS));

            room.add(new JLabel(room_info.getString("name")));
            room.add(new JLabel("(" + room_info.getInt("nb_players") + " joueurs)"));
            JButton button = new JButton("entrer");
            room.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    new GoToRoomQuery(room_info.getString("name")).send(socket);
                }
            });

            room_list.add(room);
        }

        // new room option
        JPanel new_room = new JPanel();
        new_room.setLayout(new BoxLayout(new_room, BoxLayout.X_AXIS));
        JButton button = new JButton("nouvelle salle");
        new_room.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new GoToRoomQuery(GoToRoomQuery.RoomType.New).send(socket);
            }
        });
        room_list.add(new_room);

        window.repaint();
    }
}
