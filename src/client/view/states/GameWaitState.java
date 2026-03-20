package client.view.states;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.protocol.queries.GameStateQuery;
import model.protocol.queries.PlayerListQuery;

public class GameWaitState extends GameState {

    public GameWaitState(Window window, ClientSocketThread socket, String room_name) {
        super(window, socket, room_name);
        new PlayerListQuery().send(socket);
    }
    public GameWaitState(GameState previous_state) {
        super(previous_state);
        new PlayerListQuery().send(socket);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        super.createInterface(panel, frame);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("En attente de la fin de la partie en cours...", SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 20));
        panel.add(label, BorderLayout.PAGE_START);

        panel.add(new JScrollPane(createPlayerListPanel()), BorderLayout.LINE_END);
    }

    @Override
    protected boolean onReceiveGameState(GameStateQuery query, ClientSocketThread socket) {
        if (!query.isGameRunning()) window.changeState(new GameRoleState(this));
        return true;
    }
}
