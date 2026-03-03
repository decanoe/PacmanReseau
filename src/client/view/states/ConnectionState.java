package client.view.states;

import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.socket.ClientSoketThread;
import client.view.Window;

public class ConnectionState extends WindowState {
    static String DEFAULT_ADRESS = "127.0.0.1";
    static int DEFAULT_PORT = 2000;
    static String[] RANDOM_NAMES = { "Tom", "Paul", "Bob", "Louis", "Charles", "Elise", "Camille", "Valérie" };

    public ConnectionState(Window window) {
        super(window, null);
    }

    @Override
    public void createInterface(JPanel panel, JFrame frame) {
        panel.setLayout(new GridLayout(4, 1));

        panel.add(new JLabel("Entrez les informations de connection"));

        JPanel info_panel = new JPanel();
        info_panel.setLayout(new GridLayout(3, 2));
        panel.add(info_panel);

        Random rand = new Random();
        TextField nameField = new TextField(RANDOM_NAMES[rand.nextInt(RANDOM_NAMES.length)]);
        info_panel.add(new JLabel("Pseudo : "));
        info_panel.add(nameField);
        TextField adressField = new TextField(DEFAULT_ADRESS);
        info_panel.add(new JLabel("Adresse serveur : "));
        info_panel.add(adressField);
        TextField portField = new TextField("" + DEFAULT_PORT);
        info_panel.add(new JLabel("Port serveur : "));
        info_panel.add(portField);

        JLabel debug_label = new JLabel("");
        panel.add(debug_label);

        JButton button = new JButton("Valider");
        panel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    socket = new ClientSoketThread(new Socket(adressField.getText(), Integer.parseInt(portField.getText())), nameField.getText());
                    socket.start();
                    
                    window.changeState(new LobyState(window, socket));
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
