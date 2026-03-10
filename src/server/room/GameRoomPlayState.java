package server.room;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.game.PacmanGame;
import model.protocol.Queries.ChoseRoleQuery;
import model.protocol.Queries.GameStateQuery;
import server.socket.RoomSocketThread;

public class GameRoomPlayState extends GameRoomState implements PropertyChangeListener {
    PacmanGame game;
    
    public GameRoomPlayState(GameRoom room) {
        super(room);

        game = new PacmanGame("./layouts/originalClassic_warp.lay", 0);
        game.init();
        game.set_speed(.25);
        game.launch();

        game.addPropertyChangeListener("turn", this);
        game.addPropertyChangeListener("game_over", this);

        room.sendToAll(new GameStateQuery().fillAnswerRunning(game.get_maze()));
    }

    protected void sendUpdate() {
        room.sendToAll(new GameStateQuery().fillAnswerRunning(game.get_maze()));
    }

    @Override
    protected void onPlayerLeave(RoomSocketThread socket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onPlayerLeave'");
    }
    @Override
    protected boolean onReceiveGameState(GameStateQuery query, RoomSocketThread socket) {
        query.fillAnswerRunning(game.get_maze()).send(socket);
        return true;
    }
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, RoomSocketThread socket) {
        query.fillDenie().send(socket);
        return true;
    }



    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("turn")) sendUpdate();
        if (evt.getPropertyName().equals("game_over")) {
            System.out.println("End of game !");
            if (room.state == this) room.setState(new GameRoomRoleState(room));
        }
    }
}
