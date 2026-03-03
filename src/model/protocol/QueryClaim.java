package model.protocol;

import org.json.JSONObject;

public abstract class QueryClaim extends Query {
    private static final class TOKEN {
        public static final String STATE = "state";
        
        public static final String SUCCESS = "success";
        public static final String DENIED = "denied";
    }

    boolean success;
    public QueryClaim() {}
    public QueryClaim(JSONObject json) {
        super(json);

        if (isAnswer()) {
            this.success = TOKEN.SUCCESS.equals(json.getString(TOKEN.STATE));
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        if (isAnswer()) {
            json.put(TOKEN.STATE, success ? TOKEN.SUCCESS : TOKEN.DENIED);
        }

        return json;
    }
    
    public QueryClaim fillAccept() {
        this.setAnswer();
        success = true;

        return this;
    }
    public QueryClaim fillDenie() {
        this.setAnswer();
        success = false;

        return this;
    }

    public boolean getSuccess() { return success; }
}
