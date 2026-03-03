package client.view.states;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.socket.ClientSoketThread;
import client.view.Window;
import model.protocol.QueryManager;

public abstract class WindowState extends QueryManager<ClientSoketThread> {
    Window window;
    ClientSoketThread socket;

    public WindowState(Window window, ClientSoketThread socket) {
        this.window = window;
        this.socket = socket;

        if (socket != null) socket.setWindowState(this);
    }

    public abstract void createInterface(JPanel panel, JFrame frame);
    public void onClose() {
        if (socket != null) socket.sendStop();
    }
    public void forceClose() {
        window.close();
    }
}
