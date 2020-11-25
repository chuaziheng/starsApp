package project2.starsApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <h2>Control: Logic for Hashing of Passwords</h2>
 * Methods that deal with comparing and encrypting passwords
 * @author  Mun Kei Wuai, Tan Wen Xiu
 * @version 1.0
 * @since   2020-11-20
 * */
public class PasswordHashController {

	/**
	* Encrypts password string to a password hash
	* @param stringToEncrypt
	* @return encrypted password string
	 */
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
	
	/**
	* Verifies username and password matches a record in the database
	* @param id admin or student id
	* @param type one of "admin" or "student"
	* @param passHash encrypted input password by user
	* @return success or failure
	* @throws Exception no admin or student with that id in database records
	 */
	public static boolean checkUsernameAndPassword(String id, String type, String passHash) throws Exception{
	    if (type.equals("student")){
		  Student s = DataBase.getStudentFromStuID(id);
		  if (s.getPassword().equals(passHash)) {
			return true;
		  }
		  
	    }
	    else if (type.equals("admin")){
		  Admin a = DataBase.getAdminFromAdminID(id);
	      if (a.getPassword().equals(passHash)) {
			return true;
	    }
	  }
	  return false;
	}
}
