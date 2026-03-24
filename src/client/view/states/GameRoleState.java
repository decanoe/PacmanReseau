package client.view.states;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import client.socket.ClientSocketThread;
import model.protocol.queries.ChoseRoleQuery;
import model.protocol.queries.FullMazeQuery;
import model.protocol.queries.GameStateQuery;
import model.protocol.queries.PlayerListQuery;

public class GameRoleState extends GameState {
    protected ChoseRoleQuery.Choice choice = ChoseRoleQuery.Choice.None;

    public GameRoleState(GameState previous_state) {
        super(previous_state);
        new PlayerListQuery().send(socket);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        super.createInterface(panel, frame);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("En attente des joueurs...", SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 20));
        panel.add(label, BorderLayout.PAGE_START);

        panel.add(new JScrollPane(createCenterPanel()), BorderLayout.CENTER);
        panel.add(new JScrollPane(createPlayerListPanel()), BorderLayout.LINE_END);
    }
    JButton pacmanButton, ghostButton;
    public JPanel createCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // roles
        {
            JPanel roles = new JPanel();
            roles.setLayout(new BoxLayout(roles, BoxLayout.X_AXIS));

            roles.add(new JLabel("Choisisez votre rôle : "));
            pacmanButton = new JButton("Pacman");
            ghostButton = new JButton("Fantôme");
            roles.add(pacmanButton);
            roles.add(ghostButton);
            
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

            center.add(roles);
        }

        return center;
    }
    
    @Override
    protected boolean onReceiveGameState(GameStateQuery query, ClientSocketThread socket) {
        if (query.isGameRunning()) {
            if (choice == ChoseRoleQuery.Choice.None) {
                window.changeState(new GameWaitState(this));
            }
        }
        return true;
    }
    @Override
    protected boolean onReceiveFullMaze(FullMazeQuery query, ClientSocketThread socket) {
        if (choice != ChoseRoleQuery.Choice.None) {
            window.changeState(new GamePlayState(this, query.getMaze()));
        }
        return true;
    }
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, ClientSocketThread socket) {
        if (!query.getSuccess()) return true;

        choice = query.getChoice();

        this.ghostButton.setEnabled(this.choice != ChoseRoleQuery.Choice.Ghost);
        this.pacmanButton.setEnabled(this.choice != ChoseRoleQuery.Choice.Pacman);

        return true;
    }
}
