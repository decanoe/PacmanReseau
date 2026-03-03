package client.view;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.view.states.ConnectionState;
import client.view.states.WindowState;

public class Window {
    public static String WINDOW_TITLE = "Pacman";

    WindowState state;
    JFrame frame;
    JPanel panel;

    public Window() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                state.onClose();
            }
        });
        frame.setTitle(WINDOW_TITLE);
        frame.setSize(new Dimension(700, 700));
        frame.setVisible(true);

        panel = new JPanel();
        frame.add(panel);

        changeState(new ConnectionState(this));
    }

    public void changeState(WindowState state) {
        frame.setJMenuBar(null);
        panel.removeAll();
        this.state = state;
        this.state.createInterface(panel, frame);
        repaint();
    }
    public void repaint() {
        frame.revalidate();
        frame.repaint();
    }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
