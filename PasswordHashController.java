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
	
	public static boolean checkUsernameAndPassword(String id, String type, String passHash) throws Exception{
		if (type.equals("student")){
			Student s = Utils.getStudentFromStuID(id);
			if (s.getPasswordHash().equals(passHash)) return true;
		}
		else if (type.equals("admin")){
			Admin a = Utils.getAdminFromAdminID(id);
			if (a.getAdminPassword().equals(passHash)) return true;
		}
		return false;
	}
}
