package client.view.states;

import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.socket.ClientSocketThread;
import client.view.Window;
import model.protocol.Queries.InfosQuery;
import model.protocol.Queries.LoginQuery;
import model.protocol.Queries.LoginSaltQuery;

public class LoginState extends WindowState {
    JLabel debug_label;
    TextField nameField;
    TextField pwdField;
    JButton button;
    public LoginState(Window window, ClientSocketThread socket) {
        super(window, socket);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        panel.setLayout(new GridLayout(4, 1));

        panel.add(new JLabel("Entrez vos informations de connection"));

        JPanel info_panel = new JPanel();
        info_panel.setLayout(new GridLayout(3, 2));
        panel.add(info_panel);

        nameField = new TextField("");
        info_panel.add(new JLabel("Pseudo : "));
        info_panel.add(nameField);
        pwdField = new TextField("");
        info_panel.add(new JLabel("Mot de passe : "));
        info_panel.add(pwdField);

        debug_label = new JLabel("");
        panel.add(debug_label);

        button = new JButton("Valider");
        panel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                login();
            }
        });
    }

    public void login() {
        button.setEnabled(false);
        nameField.setEnabled(false);
        pwdField.setEnabled(false);

        new LoginSaltQuery(nameField.getText()).send(socket);
    }
    @Override
    protected boolean onReceiveLoginSalt(LoginSaltQuery query, ClientSocketThread socket) {
        if (query.getSalt().equals("null")) {
            debug_label.setText("Informations de connection incorrectes");
            button.setEnabled(true);
            nameField.setEnabled(true);
            pwdField.setEnabled(true);
        }
        else {
            new LoginQuery(query.getLogin(), pwdField.getText(), query.getSalt()).send(socket);
        }

        return true;
    }
    @Override
    protected boolean onReceiveLogin(LoginQuery query, ClientSocketThread socket) {
        if (query.getSuccess()) {
            socket.setPlayerName(query.getLogin());
            new InfosQuery().fillAnswer(socket).send(socket);
            window.changeState(new LobyState(window, socket));
        }
        else {
            debug_label.setText("Informations de connection incorrectes");
            button.setEnabled(true);
            nameField.setEnabled(true);
            pwdField.setEnabled(true);
        }

        return true;
    }
}
