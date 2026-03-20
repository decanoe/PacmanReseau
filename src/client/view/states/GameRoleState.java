package client.view.states;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.socket.ClientSocketThread;
import model.protocol.queries.ChoseRoleQuery;
import model.protocol.queries.GameStateQuery;

public class GameRoleState extends GameState {
    protected ChoseRoleQuery.Choice choice = ChoseRoleQuery.Choice.None;

    public GameRoleState(GameState previous_state) {
        super(previous_state);
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
    protected boolean onReceiveGameState(GameStateQuery query, ClientSocketThread socket) {
        if (query.isGameRunning()) {
            if (choice == ChoseRoleQuery.Choice.None) {
                window.changeState(new GameWaitState(this));
            }
            else {
                window.changeState(new GamePlayState(this, query.getMaze()));
            }
        }
        return true;
    }
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, ClientSocketThread socket) {
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
