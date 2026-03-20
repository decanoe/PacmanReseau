package client.view.states;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.protocol.data.RoomInfo;
import model.protocol.queries.GoToRoomQuery;
import model.protocol.queries.RoomListQuery;

public class LobbyState extends WindowState {
    JPanel room_list;

    public LobbyState(Window window, ClientSocketThread socket) {
        super(window, socket);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        frame.setTitle(Window.WINDOW_TITLE + " - " + "lobby");
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.5;

        JLabel label = new JLabel("Choisissez une partie", SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 20));
        panel.add(label, c);
        
        room_list = new JPanel();
        room_list.setLayout(new BoxLayout(room_list, BoxLayout.Y_AXIS));
        room_list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        c.gridy = 1;
        c.weighty = 2;
        panel.add(new JScrollPane(room_list), c);

        JButton refresh_button = new JButton("Rafraichir");
        refresh_button.setFont(new Font(label.getFont().getName(), Font.PLAIN, 20));
        c.gridy = 2;
        c.weighty = 0.25;
        panel.add(refresh_button, c);

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
    protected void refreshRoomList(ArrayList<RoomInfo> infos) {
        room_list.removeAll();

        // new room option
        {
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
        }
        
        for (RoomInfo info : infos) {
            JPanel room = new JPanel();
            room.setLayout(new BoxLayout(room, BoxLayout.X_AXIS));

            room.add(new JLabel(info.name));
            room.add(new JLabel(" (" + info.player_count + " joueurs) "));
            JButton button = new JButton("rejoindre");
            room.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    new GoToRoomQuery(info.name).send(socket);
                }
            });

            room_list.add(room);
        }

        window.repaint();
    }
}
