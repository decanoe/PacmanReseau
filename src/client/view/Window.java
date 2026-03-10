package client.view;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.view.states.ConnectionState;
import client.view.states.WindowState;

public class Window {
    public static final Dimension WINDOW_DEFAULT_DIMENSION = new Dimension(700, 700);
    public static final String WINDOW_TITLE = "Pacman";

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
        resize_window(WINDOW_DEFAULT_DIMENSION);
        frame.setVisible(true);
        frame.setFocusable(true);

        panel = new JPanel();
        frame.add(panel);

        changeState(new ConnectionState(this));
    }

    public void changeState(WindowState state) {
        frame.setJMenuBar(null);
        frame.remove(panel);
        tryRemoveKeyListener(this.state);
        
        panel = new JPanel();
        frame.add(panel);
        this.state = state;
        resize_window(WINDOW_DEFAULT_DIMENSION);
        this.state.createInterface(panel, frame);
        repaint();

        tryAddKeyListener(state);
    }
    public void repaint() {
        frame.revalidate();
        frame.repaint();
    }

    public void resize_window(Dimension dimension) {
        frame.setSize(dimension);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int dx = ge.getCenterPoint().x - dimension.width / 2;
        int dy = 0;
        frame.setLocation(dx, dy);
    }

    protected void tryRemoveKeyListener(WindowState s) {
        if (s instanceof KeyListener) frame.removeKeyListener((KeyListener)s);
    }
    protected void tryAddKeyListener(WindowState s) {
        if (s instanceof KeyListener) frame.addKeyListener((KeyListener)s);
    }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
