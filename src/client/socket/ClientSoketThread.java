package client.socket;

import java.net.Socket;
import org.json.JSONObject;

import client.view.states.WindowState;
import model.socket.SocketThread;

public class ClientSoketThread extends SocketThread {
    WindowState window_state;

    public ClientSoketThread(Socket socket, String name) {
        super(socket, name);
    }
    public void setWindowState(WindowState window_state) {
        this.window_state = window_state;
    }

    @Override
    protected boolean onQuery(JSONObject json) {
        return window_state.onQuery(json, this);
    }
    @Override
    public void onConnectionStart() {
    }
    @Override
    public void onConnectionEnd() {
        window_state.forceClose();
    }
}
