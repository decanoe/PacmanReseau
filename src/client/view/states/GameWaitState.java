package client.view.states;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.protocol.Queries.GameStateQuery;

public class GameWaitState extends GameRoomState {

    public GameWaitState(Window window, ClientSocketThread soket, String room_name) {
        super(window, soket, room_name);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        super.createInterface(panel, frame);

        panel.add(new JLabel("En attente de la fin de la partie en cours..."));
    }

    @Override
    protected boolean onReceiveGameState(GameStateQuery query, ClientSocketThread socket) {
        if (!query.isGameRunning()) window.changeState(new GameRoleState(window, socket, room_name));
        return true;
    }
}
