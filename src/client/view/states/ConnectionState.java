package client.view.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import client.socket.ClientSocketThread;
import client.view.Window;

public class ConnectionState extends WindowState {
    public static final String DEFAULT_ADRESS = "127.0.0.1";
    public static final int DEFAULT_PORT = 2000;

    String debug_string;
    public ConnectionState(Window window, String debug_string) {
        super(window, null);
        this.debug_string = debug_string;
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        panel.setLayout(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel label = new JLabel("Entrez les informations du serveur de jeu", SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 20));
        panel.add(label);

        JPanel info_panel = new JPanel();
        info_panel.setLayout(new GridLayout(3, 2));
        info_panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        panel.add(info_panel);

        TextField adressField = new TextField(DEFAULT_ADRESS);
        info_panel.add(new JLabel("Adresse serveur : "));
        info_panel.add(adressField);
        TextField portField = new TextField("" + DEFAULT_PORT);
        info_panel.add(new JLabel("Port serveur : "));
        info_panel.add(portField);

        JLabel debug_label = new JLabel(debug_string);
        debug_label.setForeground(Color.RED);
        info_panel.add(debug_label);

        JButton button = new JButton("Valider");
        button.setFont(new Font(button.getFont().getName(), Font.PLAIN, 20));
        panel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    socket = new ClientSocketThread(new Socket(adressField.getText(), Integer.parseInt(portField.getText())), "not_logged_yet");
                    socket.start();
                    
                    window.changeState(new LoginState(window, socket));
                }
                catch(UnknownHostException e) {
                    debug_label.setText(e.getMessage());
                }
                catch (IOException e) {
                    debug_label.setText("Aucun serveur n'est rattaché au port");
                }
            }
        });
    }
}
