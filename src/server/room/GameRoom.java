package server.room;

import java.util.HashMap;

import model.game.maze.Maze;
import model.protocol.Queries.ChoseRoleQuery;
import model.protocol.Queries.GameStateQuery;
import model.protocol.Queries.GoToRoomQuery;
import server.socket.RoomSocketThread;

public class GameRoom extends Room {
    private static int ID_GENERATOR = 0;
    LobyRoom loby;

    boolean running = false;
    Maze maze = null;

    HashMap<RoomSocketThread, ChoseRoleQuery.Choice> choices;

    public GameRoom(LobyRoom loby) {
        super("GameRoom-" + ID_GENERATOR++);
        this.loby = loby;

        this.choices = new HashMap<>();
    }

    @Override
    protected void onRoomExit(RoomSocketThread socket) {
        choices.remove(socket);
        checkReady();
    }

    @Override
    protected boolean onReceiveGoToRoom(GoToRoomQuery query, RoomSocketThread socket) {
        if (query.toLoby()) {
            query.fillAccept().send(socket);
            socket.setRoom(loby);
        }
        else {
            query.fillDenie().send(socket);
        }
        return true;
    }
    @Override
    protected boolean onReceiveGameState(GameStateQuery query, RoomSocketThread socket) {
        if (isRunning()) {
            query.fillAnswerRunning(maze).send(socket);
        }
        else {
            query.fillAnswerNotRunning().send(socket);
        }
        return true;
    }
    @Override
    protected boolean onReceiveChoseRole(ChoseRoleQuery query, RoomSocketThread socket) {
        if (isRunning()) {
            query.fillDenie().send(socket);
            return true;
        }

        choices.put(socket, query.getChoice());
        query.fillAccept().send(socket);
        checkReady();
        return true;
    }
    
    protected void checkReady() {
        if (isRunning()) return;
        for (RoomSocketThread socket : this.opened_sokets) {
            if (choices.getOrDefault(socket, ChoseRoleQuery.Choice.None) == ChoseRoleQuery.Choice.None) return;
        }

        try {
            maze = new Maze("./layouts/bigMaze.lay");
            running = true;
            sendToAll(new GameStateQuery().fillAnswerRunning(maze));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public boolean isRunning() { return running; }
}
