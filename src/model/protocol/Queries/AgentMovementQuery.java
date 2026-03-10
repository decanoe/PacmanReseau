package model.protocol.Queries;

import org.json.JSONObject;

import model.game.agent.AgentAction.Direction;
import model.protocol.Query;

public final class AgentMovementQuery extends Query {
    private static final class TOKEN {
        public static final String DIRECTION = "direction";
    }

    protected Direction direction;
    public AgentMovementQuery(Direction direction) {
        this.direction = direction;
    }
    public AgentMovementQuery(JSONObject json) {
        super(json);

        this.direction = Direction.valueOf(json.getString(TOKEN.DIRECTION));
    }

    public static final String ACTION = "move";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        json.put(TOKEN.DIRECTION, direction.toString());

        return json;
    }

    public Direction getDirection() {
        return direction;
    }
}
