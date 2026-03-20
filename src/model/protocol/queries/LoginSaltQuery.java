package model.protocol.queries;

import org.json.JSONObject;

import model.protocol.Query;

public final class LoginSaltQuery extends Query {
    private static final class TOKEN {
        public static final String LOGIN = "login";
        public static final String SALT = "salt";
    }

    protected String login;
    protected String salt;
    public LoginSaltQuery(String login) {
        this.login = login;
    }
    public LoginSaltQuery(JSONObject json) {
        super(json);

        this.login = json.getString(TOKEN.LOGIN);
        if (isAnswer()) {
            this.salt = json.getString(TOKEN.SALT);
        }
    }

    public static final String ACTION = "login_salt";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        json.put(TOKEN.LOGIN, login);
        if (isAnswer()) {
            json.put(TOKEN.SALT, salt);
        }

        return json;
    }

    public LoginSaltQuery fillAnswer(String salt) {
        super.setAnswer();
        this.salt = salt;
        
        return this;
    }

    public String getLogin() {
        return login;
    }
    public String getSalt() {
        return salt;
    }
}
