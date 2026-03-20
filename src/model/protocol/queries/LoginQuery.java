package model.protocol.queries;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.json.JSONObject;

import model.protocol.QueryClaim;

public final class LoginQuery extends QueryClaim {
    private static final class TOKEN {
        public static final String LOGIN = "login";
        public static final String PASSWORD_HASH = "passwordHash";
    }
    
    protected String login;
    protected String passwordHash;

    public LoginQuery(String login, String password, String salt) {
        this.login = login;

        this.passwordHash = "";
        try {
            this.passwordHash = hashPBKDF2(password, salt);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    public LoginQuery(JSONObject json) {
        super(json);
        
        this.login = json.getString(TOKEN.LOGIN);
        this.passwordHash = json.getString(TOKEN.PASSWORD_HASH);
    }

    public static final String ACTION = "login";
    @Override
    protected String getAction() { return ACTION; }
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();

        json.put(TOKEN.LOGIN, this.login);
        json.put(TOKEN.PASSWORD_HASH, this.passwordHash);

        return json;
    }

    public String getLogin () {
        return login;
    }
    public String getPasswordHash () {
        return passwordHash;
    }

    private static String hashPBKDF2(String password, String saltBase64) throws Exception {

		byte[] salt = Base64.getDecoder().decode(saltBase64);

        int iterations = 100000;
        int keyLength = 256;

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                keyLength
        );

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] derived = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(derived);
    }
}
