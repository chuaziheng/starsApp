package project2.starsApp;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Scanner;

/**
* <h2>Control: Logic for ErrorHandling</h2>
* Contains various Methods to handle errors due to user input.  
* Some Methods allow for user to try again with a valid input, 
*	 such as incorrect date/time formats, string or integer inputs. 
* Other Methods check whether user has entered options within existing range, 
	such as whether the student or module exists. If they do not exist, exception is thrown. 
* <p>
* <b>Note:</b> Giving proper comments in your program makes it more
* user friendly and it is assumed as a high quality code.
*
* @author  Pooja Nag, Goh Nicholas
* @version 2.2
* @since   2020-11-20
*/

public class ErrorHandling {
	transient static Scanner sc = new Scanner(System.in);
	
	/** Method to check whether user has entered either of the 2 options for gender */
	public static char checkGender(char gender) {
		boolean done = false;
		do {
			if (gender=='m' ||gender == 'f') {
				done = true;
			}
			else {
				System.out.println("Enter M or F only: ");
				gender = Character.toLowerCase(sc.nextLine().charAt(0));
			}
		}while (!done);
		return gender;
	}

	/** Method to check whether user input is a valid integer
	 * 	returns int */
	public static int checkInteger(String string) {
		int i = 0;
		boolean done = false;
		do{
	       try {
			i = Integer.parseInt(string);
			done = true;
			
	        
	    } catch (NumberFormatException nfe) {
	        System.out.println("Please enter a valid integer.");
	        string = sc.nextLine();
	    	}
		}while (!done); 
		return i;
	}

	/** Method to check whether user input is a valid integer
	 * 	returns boolean */
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	        
	    } catch (NumberFormatException nfe) {
	        
	    	return false;
		}
	    return true;
	}

	/** Method to check whether user has entered a choice within the valid choices */
	public static boolean isReasonableChoice(int listSize, int indexChoice) throws Exception{
		if (indexChoice >= listSize || indexChoice < 0){
			System.out.println("Chosen option does not exist!");
			return false;
		}
		return true;
	}

	/** Method to check whether user has entered time in required format */
	public static String checkTimeFormat(String time) {
		String temp = time; 
		boolean x = false; 
		do {
			if (temp.contains(":")) {
				String[] timeList = temp.split(":");
				if (isNumeric(timeList[0]) && isNumeric(timeList[1])){
					if(Integer.parseInt(timeList[0])>24 || (Integer.parseInt(timeList[0]))<0) {
						System.out.println("Hours must be between 0 and 24!");
						System.out.println("Please enter HH:MM: ");
						temp = sc.nextLine(); 
					}
					else if(Integer.parseInt(timeList[1])>59 || (Integer.parseInt(timeList[0]))<0) {
						System.out.println("Minutes must be between 0 and 60!");
						System.out.println("Please enter HH:MM: ");
						temp = sc.nextLine(); 
					}
					else {
						x = true; 
					}
			}
				else {
					System.out.println("Hours and minutes must be integers!");
					System.out.println("Please enter HH:MM: ");
					temp = sc.nextLine(); 
				}
			}
			else {
				System.out.println("Include colon!");
				System.out.println("Please enter HH:MM: ");
				temp = sc.nextLine(); 
			}
		} while(!x);	
		return temp; 
	}
	public static void checkStuExistingMod(ArrayList<String[]> modules, Index indexToAdd) throws Exception{
		for (String[] mod : modules) {
		 if (mod[0].equals(indexToAdd.getCourseCode())){
		  throw new Exception("Sorry, you are already registered for this course!");
		 }
		}
	   }
	/** Method to check whether user has entered date in required format */
	public static String checkDateFormat(String date) {

        boolean x = false;
        do {
        	try {
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")); 
                x = true;
            } catch (DateTimeParseException e) {
                System.out.println("Please enter DD/MM/YYYY: ");
                date = sc.nextLine();
            }
        } while (!x);
        return date; 
	}
	
	/** Method to check whether user has entered valid time period 
	 * 	such that startTime is before endTime */
	public static boolean checkTimePeriod(String startTime, String endTime, String date) {
	
		String[] startList = startTime.split(":");
		String[] dateList = date.split("/");
		Calendar start = Calendar.getInstance();
		start.set(Integer.parseInt(dateList[2]), (Integer.parseInt(dateList[1]))-1, Integer.parseInt(dateList[0]), Integer.parseInt(startList[0]), Integer.parseInt(startList[1]));
		
		String[] endList = endTime.split(":");
		Calendar end = Calendar.getInstance();
		end.set(Integer.parseInt(dateList[2]), (Integer.parseInt(dateList[1]))-1, Integer.parseInt(dateList[0]), Integer.parseInt(endList[0]), Integer.parseInt(endList[1]));
		
		if(start.after(end)) {
			return false; 
		}
		return true; 
	}
	
	/** Method to check whether student already has the existing course
	 *  and disallows student from adding the course again */
    public static void checkStuExistingCourse(ArrayList<String[]> modules, String courseCode) throws Exception{
        for (String[] mod : modules){
            if (mod[0].equals(courseCode)){
				throw new Exception("\nYou are already registered for this course!");
			}
		}
	}

	/** Method to check whether user is already registered for existing index
	 *  and disallows student from adding index */
	public static void checkStuExistingIndex(ArrayList<String[]> modules, Index indexToAdd) throws Exception{
		for (String[] mod : modules) {
			if (mod[0].equals(indexToAdd.getCourseCode())){
				throw new Exception("Sorry, you are already registered for this index!");
			}
		}
	}

	/** Method to check whether student already has maximum academic units
	 *  and disallows student from adding course */
	public static void checkAcadUnit(Student s) throws Exception{
		if (s.getAcadUnit() >= 21) {
			throw new Exception("\nMaximum academic units of 21 allocated. Not allowed to add courses!");
		}
	}

	/** Method to check whether student has exceeded maximum academic units 
	 * 	after attempting to add a new course
	 *  and disallows student from adding course */
	public static void checkAcadUnit(Student s, Index i) throws Exception{
		if(s.getAcadUnit() + i.getAcadUnit() >= 21) {
			throw new Exception(String.format("Adding %s exceeds maximum allowed academic units of 21. Not allowed to add course!", i.getCourseCode()));
		}
	}

	/** Method to check whether student has modules before performing action that 
	 *  requires student to have existing modules */
	public static void isEmpty(ArrayList<String[]> modules) throws Exception{
		if (modules.isEmpty()){
			throw new Exception("You have no existing modules!");
		}
	}

	/** Method to check whether student has modules before performing action that 
	 *  requires student to have existing modules */
	public static void sameIndexCannotSwap(Index myIndex, Index sIndex) throws Exception{
		if (myIndex.getIndexNo().equals(sIndex.getIndexNo())){
			throw new Exception("Student has same index as you, cannot swap");
		}
	}

	/** Method to check whether course code exists before printing total vacancy of course */
	public static void checkVacancy(){
		System.out.println("Enter course code to view its vacancies: ");
		String courseCode = sc.nextLine();
		courseCode = courseCode.toUpperCase();

		if (checkExistingCourse(courseCode, true)){
			ArrayList<String> indexNums = DataBase.getIndexNumsFromCourseCode(courseCode);

			int counter = 1;
			for (String indexNum: indexNums){ // Prints list of indices and vacancies
				Index index = DataBase.getIndexFromIndexNum(indexNum);
				System.out.printf("(%d) index number: %s vacancy: %d\n", counter, index.getIndexNo(), index.getVacancy());
				counter++;
			}
		}
		else 
			System.out.printf("Course code does not exist!"); 
	}

	/** Method to check whether student is an existing student in database 
	 *  via checking the unique student ID */
	public static boolean checkExistingStudent(String studentID, boolean printError) {
		ArrayList<Student> stuList = DataBase.getStuList();
		for (Student student : stuList){
			if (student.getUsername().equals(studentID)){
				return true; 
			}
		}
		if (printError) System.out.println("Invalid StudentID");
		return false;
	}

	/** Method to check whether index input is an existing index in database 
	 *  via checking the unique index number */
	public static boolean checkExistingIndex(String indexNo, boolean print) {
		ArrayList<Index> indexList = DataBase.getIndexList();
		for (Index index : indexList){
			if (index.getIndexNo().equals(indexNo)){
				return true; 
			}
		}
		if (print) System.out.println("No existing index");
		return false;
	}

	/** Method to check whether course input is an existing course in database 
	 *  via checking the unique course code */
	public static boolean checkExistingCourse(String courseCode, boolean print) {
		for (String c : DataBase.getAllCourseCodes()){
			if (c.equals (courseCode)){
				return true; 
			}
		}
		if (print) System.out.println("No existing course " + courseCode);
		return false;
	}

	/** Method to check whether input is a valid class type
	 *  and changes input to a suitable format to be saved */
	public static String checkClassType(String classType) {
		String temp = classType.toLowerCase();
		boolean x = false; 
		do{
			switch(temp){
				case "lec":
				case "lecture":
					classType = "LEC";
					x = true; 
					break;
				case "tut":
				case "tutorial":
					classType = "TUT";
					x = true; 
					break;
				case "sem":
				case "seminar":
					classType = "SEM";
					x = true; 
					break;
				case "lab":
					classType = "LAB"; 
					x = true; 
					break;
				default: 
					System.out.println("Invalid input! Please type LEC/TUT/SEM/LAB only: ");
					temp = sc.nextLine();
					break;
			}
		} while (!x);
		return classType;
	}

	/** Method to check whether input is a valid day
	 *  and changes input to a suitable format to be saved */
	public static String checkClassDay(String classDay) {
		String temp = classDay.toLowerCase();
		boolean x = false; 
		switch(temp){
			case "mon":
			case "monday":
				classDay = "Monday"; 
				x = true; 
				break; 
			case "tue":
			case "tuesday":
				classDay = "Tuesday"; 
				x = true; 
				break; 
			case "wed":
			case "wednesday":
				classDay = "Wednesday"; 
				x = true; 
				break;
			case "thu":
			case "thursday":
				classDay = "Thursday"; 
				x = true; 
				break;
			case "fri":
			case "friday":
				classDay = "Friday"; 
				x = true; 
				break; 
			default: 
				System.out.println("Invalid input! Please type MON/TUE/WED/THU/FRI: ");
				temp = sc.nextLine();
				break; 
		}
		return classDay; 
	}
	public static int convertToInt(String idxChoice) throws Exception{
		if (!idxChoice.matches("[0-9]+")){
			throw new Exception("Please enter a number!");
		}
		else return Integer.parseInt(idxChoice);
	}
}
