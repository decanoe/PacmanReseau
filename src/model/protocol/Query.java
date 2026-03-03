package model.protocol;

import org.json.JSONObject;

import model.socket.SocketThread;

public abstract class Query {
    private static final class TOKEN {
        public static final String TYPE = "type";
    }
    
    public enum QueryType { Query, Answer }
    protected QueryType queryType = QueryType.Query;
    
    public Query() {
    }
    public Query(JSONObject json) {
        this.queryType = QueryType.valueOf(json.getString(TOKEN.TYPE));
    }

    protected void setAnswer() { this.queryType = QueryType.Answer; }
    public boolean isAnswer() { return this.queryType == QueryType.Answer; }

    public static final String ACTION = "action";
    protected abstract String getAction();
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(ACTION, getAction());
        json.put(TOKEN.TYPE, queryType.toString());
        return json;
    }
    public String toString() { return toJson().toString(); }
    
    public void send(SocketThread socket) {
        socket.send(toJson());
    }
}
