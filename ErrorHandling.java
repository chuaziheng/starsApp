import java.util.ArrayList;

public class ErrorHandling{
	public static void checkAcadUnit(Student s) throws Exception{
		if (s.getAcadUnit() >= 21) {
			throw new Exception("\nMaximum academic units of 21 allocated. Not allowed to add courses!");
		}
	}
	public static void checkAcadUnit(Student s, Index i) throws Exception{
		if(s.getAcadUnit() + i.getAcadUnit() >= 21) {
			throw new Exception(String.format("Adding %s exceeds maximum allowed academic units of 21. Not allowed to add course!", i.getCourseCode()));
		}
	}
	public static boolean isReasonableChoice(int listSize, int indexChoice) throws Exception{
		if (indexChoice >= listSize || indexChoice < 0){
			System.out.println("Chosen option does not exist!");
			return false;
		}
		return true;
	}
	public static void checkStuExistingMod(ArrayList<String[]> modules, Index indexToAdd) throws Exception{
		for (String[] mod : modules) {
			if (mod[0].equals(indexToAdd.getCourseCode())){
				throw new Exception("Sorry, you are already registered for this index");
			}
		}
	}
	public static void isEmpty(ArrayList<String[]> modules) throws Exception{
		if (modules.isEmpty()){
			throw new Exception("You have no existing modules!");
		}
	}
	public static void sameIndexCannotSwap(Index myIndex, Index sIndex) throws Exception{
		if (myIndex.getIndexNo().equals(sIndex.getIndexNo())){
			throw new Exception("Student has same index as you, cannot swap");
		}
	}
}
