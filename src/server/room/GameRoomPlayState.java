package server.room;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import model.game.PacmanGame;
import model.game.agent.Agent;
import model.game.agent.behavior.ThreadControlledBehavior;
import model.game.maze.Maze.EntityType;
import model.protocol.Queries.AgentMovementQuery;
import model.protocol.Queries.ChoseRoleQuery;
import model.protocol.Queries.CosmeticsQuery;
import model.protocol.Queries.ChoseRoleQuery.Choice;
import model.protocol.Queries.GameStateQuery.WinState;
import model.protocol.Queries.GameStateQuery;
import server.socket.RoomSocketThread;
import server.web_interface.WebInterface;

public class GameRoomPlayState extends GameRoomState implements PropertyChangeListener {
    PacmanGame game;
    HashMap<RoomSocketThread, ThreadControlledBehavior> agentBehaviors;

    public GameRoomPlayState(GameRoom room, HashMap<RoomSocketThread, ChoseRoleQuery.Choice> choices) {
        super(room);

        game = new PacmanGame("./layouts/originalClassic_warp.lay", 0);
        game.init();
        mapPlayersToAgents(choices);

        game.set_speed(.5);
        game.launch();

        game.addPropertyChangeListener("turn", this);
        game.addPropertyChangeListener("game_over", this);

        room.sendToAll(new GameStateQuery().fillAnswerRunning(game.get_maze()));
    }

    protected void mapPlayerToAgent(RoomSocketThread thread, Agent agent) {
        ThreadControlledBehavior behavior = new ThreadControlledBehavior();
        agent.set_behavior(behavior);
        agentBehaviors.put(thread, behavior);

        CosmeticsQuery query = new CosmeticsQuery().fillAnswer(WebInterface.getActiveCosmetics(thread.getPlayerLogin()));

        agent.set_colors(query.getAgentColors(agent.get_type(), agent.get_colors()));
    }
    protected void mapPlayersToAgents(HashMap<RoomSocketThread, ChoseRoleQuery.Choice> choices) {
        this.agentBehaviors = new HashMap<>();

        ArrayList<Agent> ghosts = game.get_maze().getGhosts();
        ArrayList<Agent> pacmans = game.get_maze().getPacmans();
        Collections.shuffle(ghosts);
        Collections.shuffle(pacmans);

        int ghosts_counter = 0, pacmans_counter = 0;
        for (Entry<RoomSocketThread,Choice> entry : choices.entrySet()) {
            switch (entry.getValue()) {
                case Choice.Pacman:
                    if (pacmans_counter >= pacmans.size()) break;
                    mapPlayerToAgent(entry.getKey(), pacmans.get(pacmans_counter++));
                    break;
                case Choice.Ghost:
                    if (ghosts_counter >= ghosts.size()) break;
                    mapPlayerToAgent(entry.getKey(), ghosts.get(ghosts_counter++));
                    break;
                default:
                    break;
            }
        }
    }

    protected void sendUpdate() {
        game.get_maze().setTurn(game.turn);
        game.get_maze().setGhostsScarred(game.are_ghost_scared());
        room.sendToAll(new GameStateQuery().fillAnswerRunning(game.get_maze()));
    }

    @Override
    protected void onPlayerLeave(RoomSocketThread socket) {
        if (!agentBehaviors.containsKey(socket)) return;

        Agent a = agentBehaviors.get(socket).get_agent();
        a.set_behavior(game.getDefaultBehavior(a));

        agentBehaviors.remove(socket);
        if (agentBehaviors.isEmpty()) {
            game.pause();
            System.out.println("No players !");
            if (room.state == this) room.setState(new GameRoomRoleState(room));
        }
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
    protected boolean onReceiveAgentMovement(AgentMovementQuery query, RoomSocketThread socket) {
        if (!agentBehaviors.containsKey(socket)) return true;

        agentBehaviors.get(socket).setNextDirection(query.getDirection());
        return true;
    }

    protected void onGameOver(boolean pacman_win) {
        System.out.println("End of game !");

        EntityType winners = pacman_win ? EntityType.Pacman : EntityType.Ghost;
        for (Entry<RoomSocketThread, ThreadControlledBehavior> entry : agentBehaviors.entrySet()) {
            WebInterface.updateInfos(entry.getKey().getPlayerLogin(), entry.getValue().get_agent().get_type() == winners);
            new GameStateQuery().fillAnswerNotRunning(pacman_win ? WinState.Pacman : WinState.Ghost).send(entry.getKey());
        }

        if (room.state == this) room.setState(new GameRoomRoleState(room));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("turn")) sendUpdate();
        if (evt.getPropertyName().equals("game_over")) onGameOver((boolean)evt.getNewValue());
    }
}
