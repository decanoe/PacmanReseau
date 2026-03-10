package server.room;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import model.protocol.Queries.LoginQuery;
import model.protocol.Queries.LoginSaltQuery;
import server.socket.RoomSocketThread;

public class LoginRoom extends Room {
    private static final String DEFAULT_SALT = "LFpo+y6kGx/HU/34OOhjdQ==";

    LobyRoom loby;
    public LoginRoom(LobyRoom loby) {
        super("login room");
        this.loby = loby;
    }

    protected String get_salt(String login) {
        return DEFAULT_SALT;
    }
    protected boolean validate(String login, String password_hash) {
        String default_password = "test";
        try {
            default_password = hashPBKDF2(default_password, DEFAULT_SALT);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }

        return default_password.equals(password_hash);
    }
    
    @Override
    protected boolean onReceiveLoginSalt(LoginSaltQuery query, RoomSocketThread socket) {
        query.fillAnswer(get_salt(query.getLogin())).send(socket);
        return true;
    }
    @Override
    protected boolean onReceiveLogin(LoginQuery query, RoomSocketThread socket) {
        if (validate(query.getLogin(), query.getPassword())) {
            query.fillAccept().send(socket);
            socket.setRoom(loby);
        }
        else {
            query.fillDenie().send(socket);
        }
        return true;
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
