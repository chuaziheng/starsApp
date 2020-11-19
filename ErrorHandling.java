// package project2;

import java.util.ArrayList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class ErrorHandling {
	transient static Scanner sc = new Scanner(System.in);
	
	public static char checkGender(char gender) {
		boolean done = false;
		do {
			if (gender=='m' ||gender == 'f' ) {
				done = true;
			}
			else {
				System.out.println("Enter M or F only: ");
				gender = Character.toLowerCase(sc.nextLine().charAt(0));
			}
		}while (!done);
		return gender;
	}
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
//	public static int isInteger(String)
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
				throw new Exception("Sorry, you are already registered for this course!");
			}
		}
	}

	public static void checkStuExistingCourse(Student s, String courseCode) throws Exception{
		for (String[] mod : s.getModules()) {
			if (mod[0].equals(courseCode)){
				throw new Exception("You are already registered for this index");
			}
		}
	}

	 // public static boolean checkIfStudentHasExistingCourse(Student s, String courseCode) throws Exception{
	// 	for ()
    //     int count = 0;
    //     for (String course : Utils.getAllCourseCodes()){
    //         if (course.equals(courseCode)){
    //             break;
    //         } else {
    //             count += 1;
    //         }
    //     }
    //     if (count == Utils.getAllCourseCodes().size()) {
	// 		throw new Exception("\nYou are already registered for this course!");
	// 	}
	// }

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
					temp = sc.next();
					break;
			}
		} while (!x);
		return classType;
	}

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
				temp = sc.next();
				break; 
		}
		return classDay; 
	}
}