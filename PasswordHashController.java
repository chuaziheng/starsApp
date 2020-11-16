package project2.starsApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashController {
	public static String hash(String stringToEncrypt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(stringToEncrypt.getBytes());

			byte byteData[] = md.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			stringToEncrypt = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return stringToEncrypt;
	}
	
	public static boolean checkUsernameAndPassword(String username, String passHash) {
		try {
			//Student student = read.retrieve(username);
	    } catch (Exception e) {System.out.println("pass err");}
		return false;
	}
}
