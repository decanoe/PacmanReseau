package client.view.states;

import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
    static String[] RANDOM_NAMES = { "Tom", "Paul", "Bob", "Louis", "Charles", "Elise", "Camille", "Valérie" };

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

        panel.add(new JLabel("Entrez les informations de connection"));

        JPanel info_panel = new JPanel();
        info_panel.setLayout(new GridLayout(3, 2));
        panel.add(info_panel);

        Random rand = new Random();
        nameField = new TextField(RANDOM_NAMES[rand.nextInt(RANDOM_NAMES.length)]);
        info_panel.add(new JLabel("Pseudo : "));
        info_panel.add(nameField);
        pwdField = new TextField("");
        info_panel.add(new JLabel("Password : "));
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
        String salt = query.getSalt();
        String hash = "";

        try {
            hash = hashPBKDF2(pwdField.getText(), salt);
        } catch (Exception e) {
            debug_label.setText(e.getMessage());
            button.setEnabled(true);
            nameField.setEnabled(true);
            pwdField.setEnabled(true);
            return true;
        }
        
        new LoginQuery(query.getLogin(), hash).send(socket);

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
            debug_label.setText("Infos incorrectes");
            button.setEnabled(true);
            nameField.setEnabled(true);
            pwdField.setEnabled(true);
        }

        return true;
    }

    private static String hashPBKDF2(String password, String saltBase64) throws Exception {
		byte[] salt = Base64.getDecoder().decode(saltBase64);

        int iterations = 100000;
        int keyLength = 256;

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                keyLength
        );

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] derived = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(derived);
    }
}
