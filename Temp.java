package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Temp {
	
	public static String hashPBKDF2(String password, String saltBase64) throws Exception {

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

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://localhost:8080/PacmanReseau/LoginPage");
		HttpURLConnection http = (HttpURLConnection) url.openConnection();

		http.setRequestMethod("POST");
		http.setDoOutput(true);

		// Headers
		http.setRequestProperty("Content-Type", "application/json");
		// Corps de la requête
		String salt = "LFpo+y6kGx/HU/34OOhjdQ==";
        String password = "test123*";

        String hash = hashPBKDF2(password, salt);
        System.out.println("Hash : " + hash);
		String data = "{ \"action\": \"login\", \"user\": \"test10\", \"pwd\": \""+hash+"\", \"session\": false }";

		// Envoi du corps
		try (OutputStream os = http.getOutputStream()) {
		    byte[] input = data.getBytes("UTF-8");
		    os.write(input, 0, input.length);
		}

		// Lecture de la réponse
		int status = http.getResponseCode();
		InputStream is = (status < 400) ? http.getInputStream() : http.getErrorStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null) {
		    response.append(line);
		}

		br.close();

		System.out.println("Status: " + status);
		System.out.println("Réponse: " + response.toString());
	}

}