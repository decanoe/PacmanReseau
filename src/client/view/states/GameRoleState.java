package client.view.states;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.socket.ClientSoketThread;
import client.view.Window;
import model.protocol.Queries.ChoseRoleQuery;
import model.protocol.Queries.GameStateQuery;

public class GameRoleState extends GameRoomState {
    ChoseRoleQuery.Choice choice = ChoseRoleQuery.Choice.None;

    public GameRoleState(Window window, ClientSoketThread soket, String room_name) {
        super(window, soket, room_name);
    }

    JButton pacmanButton, ghostButton;
    JLabel choiceLabel;
    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        super.createInterface(panel, frame);

        panel.add(new JLabel("Choisisez votre rôle :"));

        JPanel roles = new JPanel(new GridLayout(1, 2));
        pacmanButton = new JButton("Pacman");
        ghostButton = new JButton("Fantôme");
        roles.add(pacmanButton);
        roles.add(ghostButton);
        panel.add(roles);

        choiceLabel = new JLabel("");
        panel.add(choiceLabel);

        pacmanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new ChoseRoleQuery(ChoseRoleQuery.Choice.Pacman).send(socket);
            }
        });
        ghostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new ChoseRoleQuery(ChoseRoleQuery.Choice.Ghost).send(socket);
            }
        });
    }

    @Override
    protected boolean onReceiveGameState(GameStateQuery query, ClientSoketThread socket) {
        if (query.isGameRunning()) {
            if (choice == ChoseRoleQuery.Choice.None) {
                window.changeState(new GameWaitState(window, socket, room_name));
            }
            else {
                System.out.println("Game launched !");
            }
        }
        return true;
    }
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, ClientSoketThread socket) {
        if (!query.getSuccess()) return true;

        choice = query.getChoice();

        this.ghostButton.setEnabled(this.choice != ChoseRoleQuery.Choice.Ghost);
        this.pacmanButton.setEnabled(this.choice != ChoseRoleQuery.Choice.Pacman);

        switch (this.choice) {
            case ChoseRoleQuery.Choice.Ghost: 
                choiceLabel.setText("Fantôme choisi");
                break;
            case ChoseRoleQuery.Choice.Pacman: 
                choiceLabel.setText("Pacman choisi");
                break;
            default:
                choiceLabel.setText("");
                break;
        }

        return true;
    }
}
