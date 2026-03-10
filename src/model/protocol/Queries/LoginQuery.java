package model.protocol.Queries;

import org.json.JSONObject;

import model.protocol.QueryClaim;

public final class LoginQuery extends QueryClaim {
    private static final class TOKEN {
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
    }
    
    protected String login;
    protected String password;

    public LoginQuery(String login, String password) {
        this.login = login;
        this.password = password;
    }
    public LoginQuery(JSONObject json) {
        super(json);
        
        this.login = json.getString(TOKEN.LOGIN);
        this.password = json.getString(TOKEN.PASSWORD);
    }

    public static final String ACTION = "login";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        json.put(TOKEN.LOGIN, this.login);
        json.put(TOKEN.PASSWORD, this.password);

        return json;
    }

    public String getLogin () {
        return login;
    }
    public String getPassword () {
        return password;
    }
}
