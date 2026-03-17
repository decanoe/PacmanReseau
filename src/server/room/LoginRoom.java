package server.room;

import model.protocol.Queries.LoginQuery;
import model.protocol.Queries.LoginSaltQuery;
import server.socket.RoomSocketThread;
import server.web_interface.WebInterface;

public class LoginRoom extends Room {
    LobyRoom loby;
    public LoginRoom(LobyRoom loby) {
        super("login room");
        this.loby = loby;
    }

    @Override
    protected boolean onReceiveLoginSalt(LoginSaltQuery query, RoomSocketThread socket) {
        query.fillAnswer(WebInterface.getSalt(query.getLogin())).send(socket);
        return true;
    }
    @Override
    protected boolean onReceiveLogin(LoginQuery query, RoomSocketThread socket) {
        if (WebInterface.validatePassword(query.getLogin(), query.getPasswordHash())) {
            query.fillAccept().send(socket);
            socket.setRoom(loby);
        }
        else {
            query.fillDenie().send(socket);
        }
        return true;
    }
}
