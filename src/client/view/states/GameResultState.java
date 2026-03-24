package client.view.states;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.protocol.queries.GoToRoomQuery;

public class GameResultState extends GameState {
    boolean pacmans_won;

    public GameResultState(GameState previous_state, boolean pacmans_won) {
        super(previous_state);
        this.pacmans_won = pacmans_won;
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        super.createInterface(panel, frame);
        panel.setLayout(new GridLayout(3, 1));

        panel.add(new JLabel("Les " + (pacmans_won ? "Pacmans" : "Fantômes") + " ont gagnés !"));

        JButton buttonRejouer = new JButton("Rejouer");
        panel.add(buttonRejouer);
        JButton buttonQuitter = new JButton("Lobby");
        panel.add(buttonQuitter);

        buttonRejouer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                play();
            }
        });

        buttonQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                new GoToRoomQuery(GoToRoomQuery.RoomType.Lobby).send(socket);
            }
        });
    }

    protected void play() {
        window.changeState(new GameRoleState(this));
    }
}
