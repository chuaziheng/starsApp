package project2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Set;

public class Admin implements Serializable {
	
	final static long serialVersionUID = 123; 
	transient static Scanner sc = new Scanner(System.in);
	private String adminID;
	private String adminPasswordHash;
	//private static ArrayList<Object[]> modules = new ArrayList <Object[]>(); 
	//private static ArrayList<Integer> indexGroupList = new ArrayList<Integer>();
	private static String courseCode;
	
	// constructors
	public Admin() {}
	
	public Admin(String adminID, String adminPasswordHash) {
		this.adminID = adminID;
		this.adminPasswordHash = adminPasswordHash;
	}
	
	public String getAdminID() {
		return adminID;
	}
	public void setAdminID(String adminID) {
		this.adminID = adminID;
	}
	public String getAdminPassword() {
		return adminPasswordHash;
	}
	
	// ********************************************************case 1**********************************************************
	public static void addStudent() {
		System.out.println("Enter the new student's ID: ");
		String studentID = sc.nextLine();

		if (!Utils.checkExistingStudent(studentID)){
			System.out.println("Enter the new student's password: ");
			String passwordHash = sc.nextLine();
			//passwordHash = PasswordHashController.hash(passwordHash);

			System.out.println("Enter the new student's name: ");
			String name = sc.nextLine();

			System.out.println("Enter the new student's matriculation number: ");
			String matricNum = sc.nextLine();

			System.out.println("Enter the new student's nationality: ");
			String nationality = sc.nextLine();

			System.out.println("Enter the new student's gender (M/F): ");
			char gender = ErrorHandling.checkGender(Character.toLowerCase(sc.nextLine().charAt(0)));
			

			System.out.println("Enter the new student's school (eg. SCSE): ");
			String schoolName = sc.nextLine(); //.toLowerCase();

			System.out.println("Enter the new student's access date (in DD/MM/YYYY): ");
			String date = ErrorHandling.checkDateFormat(sc.nextLine());
			
			System.out.print("Enter the new student's access start time in 24H format HH:MM: ");
			String startTime = ErrorHandling.checkTimeFormat(sc.nextLine()); 

			System.out.print("Enter the new student's access end time in format HH:MM: ");
			String endTime = ErrorHandling.checkTimeFormat(sc.nextLine());
			
			while (!ErrorHandling.checkTimePeriod(startTime, endTime, date)) {
				System.out.println("Start time after end time. Please try again!");
				System.out.print("Enter the new student's access start time in 24H format HH:MM: ");
				startTime = ErrorHandling.checkTimeFormat(sc.nextLine()); 

				System.out.print("Enter the new student's access end time in format HH:MM: ");
				endTime = ErrorHandling.checkTimeFormat(sc.nextLine());
			}
			System.out.println("Confirm to add student " + name + " (Y/N): ");
			char option = sc.next().charAt(0);
		
			boolean quit = false;
			while (!quit){
				switch (option){
					case 'N':
					case 'n':
						System.out.println("Operation Cancelled. Student is not added. Returning to main menu...");
						quit = true;
						break;
					case 'Y':
					case 'y':
						Student newStudent = new Student(studentID, passwordHash, name, matricNum, nationality, gender, schoolName);
						Utils.getStuList().add(newStudent);
						System.out.println("Student successfully added. Returning to main menu.");
						quit = true;
						break;
					default:
						System.out.println("Invalid choice! Enter Y/N");
						option = sc.next().charAt(0);
				}
			}
		}
		else System.out.println("Student already exists! Returning to main menu.");
	}
	
	// ********************************************************case 2**********************************************************
	public static void deleteStudent() throws Exception {
		System.out.println("Enter the ID of the student to be deleted: ");
		String studentID = sc.next();
		Student student = Utils.getStudentFromStuID(studentID);

		// drop from index
		for (String[] mod : student.getModules()) {
			Index index = Utils.getIndexFromIndexNum(mod[1]);
			index.dropStud(studentID);
		}

		// drop student
		Utils.getStuList().remove(student);
	}
	
	// ********************************************************case 3**********************************************************
	public static void addModule() {
			
		System.out.println("Course code: "); 
		String temp1 = sc.nextLine(); 
		   while (Utils.checkExistingCourse(temp1)) {
		    System.out.println("Please enter a new course code!"); 
		    temp1 = sc.nextLine();
		   } 
		
		System.out.println("School (eg. SCSE): "); 
		String school = sc.nextLine(); 
		

    	System.out.println("Number of AUs: "); 

    	int acadUnit = ErrorHandling.checkInteger(sc.nextLine());
    	
		System.out.print("Number of indices: ");
		int noOfIndices = sc.nextInt(); 
		
		// updating this shld update DB list
		ArrayList<Index> indexList = Utils.getIndexList();
		for (int i=0; i<noOfIndices; i++) {

		    System.out.printf("Vacancy for Index %d: ", (i+1)); 
		    int vacancy = sc.nextInt(); 
		    sc.nextLine();
		    
		    System.out.printf("Index number %d: ", (i+1)); 
		    String temp2 = sc.nextLine(); 
		    while (Utils.checkExistingIndex(temp2)){
		     System.out.println("Index exists. Please enter a new index number!"); 
		     temp2 = sc.nextLine();
		    }
		    String indexNo = temp2; 
		    
		    Index newIndex = new Index(courseCode, school, acadUnit, indexNo, addLesson(), vacancy);
		    indexList.add(newIndex); // shld be updated in DB
		    }
		}
	
	// ********************************************************case 4 or 6**********************************************************
	public static void deleteIndexOrCourse(boolean byIndex) throws Exception {
		
		// !byIndex means remove all index in this courseCode
		// otherwise remove specific one
		if (!byIndex){
			System.out.println("Enter the course code to be deleted: ");
			courseCode = sc.next();
			ArrayList<String> indexNums = Utils.getIndexNumsFromCourseCode(courseCode); // list of all index numbers in courseCode
			
			for (String indexNum : indexNums) {
				Index index = Utils.getIndexFromIndexNum(indexNum);
				Utils.getIndexList().remove(index);
				for (Student s : Utils.getStuList()) {
					int count1 = 0;
					  for (String[] str: s.getModules()) {
					   if (index.getIndexNo().equals(str[1])) {
					    s.getModules().remove(count1);
					    break;
					   }
					   count1++;
					  }
				}
			}
		}
		else{
			printCourseIndexList(); 
			   
			   System.out.println("Enter index number to be deleted: ");
			   String temp4 = sc.nextLine(); 
			   
			   while(!Utils.checkExistingIndex(temp4)) {
			    System.out.println("Please enter valid index number!"); 
			    temp4 = sc.nextLine(); 
			   }
			   
			   Index removeIndex = Utils.getIndexFromIndexNum(temp4);
			   Utils.getIndexList().remove(removeIndex);
			   for (Student s : Utils.getStuList()) {
			    int count1 = 0;
			      for (String[] str: s.getModules()) {
			       if (removeIndex.getIndexNo().equals(str[1])) {
			        s.getModules().remove(count1);
			        break;
			       }
			       count1++;
			      }
			   }
			   System.out.println("Successfully removed!");
		}
	}
	
	// ********************************************************case 5**********************************************************
	public static void addIndex() throws Exception {
		System.out.println("Enter option of existing course: ");
		int counter = 1;
		Set<String> courseCodes = Utils.getAllCourseCodes();
		for (String courseCode: courseCodes){
			System.out.printf("\t(%d) %s\n", counter, courseCode);
			counter++;
		}
		int option = sc.nextInt();
		sc.nextLine(); //v12
		option--;
		int i = 0;
		for (String cc : courseCodes) {
			if (i == option){
				courseCode = cc;
				break;
			}
			i++;
		}
		ArrayList<String> indexNums = Utils.getIndexNumsFromCourseCode(courseCode);
		System.out.println("Current indices are: ");
		for (String indexNum: indexNums){
			System.out.println("\t" + indexNum);
		}
		System.out.printf("Enter index number to be added to course %s: \n", courseCode);
		String temp3 = sc.nextLine(); 
	    while (Utils.checkExistingIndex(temp3)){
	     System.out.println("Please enter a new index number!"); 
	     temp3 = sc.nextLine();
	    }
	    String newIndexNum = temp3; 


		System.out.println("Enter vacancy for this index: "); 
		int vacancy = sc.nextInt(); 
		sc.nextLine(); //v12
		
		Index anExistingIndex = Utils.getIndexFromIndexNum(indexNums.get(0));
		Index newIndex = new Index(courseCode, anExistingIndex.getSchool(), anExistingIndex.getAcadUnit(), newIndexNum, addLesson(), vacancy);
		Utils.getIndexList().add(newIndex); // shld be updated in DB
	}
	// ********************************************************case 7**********************************************************
	public static ArrayList<Lesson> addLesson() { 
		System.out.print("Number of Lessons for this index: ");
		  int noOfLessons = sc.nextInt(); 
		  sc.nextLine();
		  
		  ArrayList<Lesson> lessons = new ArrayList<Lesson>(); 
		  
		  for (int i=0; i<noOfLessons; i++) {
		   
		   System.out.printf("Class type (eg LEC, TUT, SEM, LAB): "); 
		   String classType = sc.nextLine(); 
		   
		   System.out.printf("Class location: "); 
		   String location = sc.nextLine(); 
		   
		   System.out.print("Start time in 24H format HH:MM: ");
		   String startTime = ErrorHandling.checkTimeFormat(sc.nextLine()); 
			
		   System.out.print("End time in 24H format HH:MM: ");
		   String endTime = ErrorHandling.checkTimeFormat(sc.nextLine());
		   
		   String date = "01/01/2000";
		   while (!ErrorHandling.checkTimePeriod(startTime, endTime, date)) {
				System.out.println("Start time after end time. Please try again!");
				System.out.print("Start time in 24H format HH:MM: ");
				startTime = ErrorHandling.checkTimeFormat(sc.nextLine()); 

				System.out.print("End time in 24H format HH:MM: ");
				endTime = ErrorHandling.checkTimeFormat(sc.nextLine());
			}
		   
		   System.out.println("Enter 1 day of the week: ");
		   String day = sc.next();
		   sc.nextLine();
		   
		   Lesson newlesson = new Lesson(location, startTime, endTime, day, classType); 
		   lessons.add(newlesson);
		  }
		  return lessons; 
		 }
	
	// case 8 same as Student option 5?
	// ********************************************************case 9**********************************************************
	public static void studentAccessPeriod(Student s) {
		
		System.out.print("Date in format DD/MM/YYYY: ");
		String date = ErrorHandling.checkDateFormat(sc.nextLine()); 
		
		System.out.print("Start time in 24H format HH:MM: ");
		String startTime = ErrorHandling.checkTimeFormat(sc.nextLine()); 
		
		System.out.print("End time in 24H format HH:MM: ");
		String endTime = ErrorHandling.checkTimeFormat(sc.nextLine());
		
		while (!ErrorHandling.checkTimePeriod(startTime, endTime, date)) {
			System.out.println("Start time after end time. Please try again!");
			System.out.print("Start time in 24H format HH:MM: ");
			startTime = ErrorHandling.checkTimeFormat(sc.nextLine()); 

			System.out.print("End time in 24H format HH:MM: ");
			endTime = ErrorHandling.checkTimeFormat(sc.nextLine());
		}
		
		s.setStartTime(startTime);
		s.setEndTime(endTime);
		s.setDate(date);
	}
	// ********************************************************case 7, 10, 11, 12**********************************************************
	public static void printCourseIndexList() {
		System.out.println("-----------------------------------------------------------------------------");
	    System.out.printf("%-20s %-15s", "COURSE", "INDEX");
	    System.out.println();
	    System.out.println("-----------------------------------------------------------------------------");
	    for(Index index: Utils.getIndexList() ){
	        System.out.format("%-20s %-15s",
	                index.getCourseCode(), index.getIndexNo());
	        System.out.println();
	    }
	    System.out.println("-----------------------------------------------------------------------------");
	}
	public static void printStuList(String byWhat) throws Exception{
		switch (byWhat) {
			case "all":
				System.out.println("List of all current students: ");
//				for (Student ss : Utils.getStuList()) {
//					System.out.println("\t" + ss.getName());
//				}
				//table format printing
				System.out.println("-----------------------------------------------------------------------------");
			    System.out.printf("%-15s %-8s %-15s", "NAME", "GENDER", "NATIONALITY");
			    System.out.println();
			    System.out.println("-----------------------------------------------------------------------------");
			    for(Student student:Utils.getStuList() ){
			        System.out.format("%-15s %-8c %-15s",
			                student.getName(), student.getGender(), student.getNationality());
			        System.out.println();
			    }
			    System.out.println("-----------------------------------------------------------------------------");
				break;
			case "course":
				System.out.println("Enter courseCode to see which students in this course: ");
				String courseCode = sc.nextLine();
				System.out.printf("Students in course %s:\n", courseCode);
				for (Index index : Utils.getIndexList()) {
					if (index.getCourseCode().equals(courseCode)){
//						for (String studentID : index.getStudList()) {
//							System.out.println("\t" + studentID);
//						}
						index.printStuListInfo();
					}
					System.out.println();
				}
				break;
			case "index":
				System.out.println("Enter index number to see which students in this index: ");
				String indexNo = sc.nextLine();
				System.out.printf("Students in index %s:\n", indexNo);
				for (Index index : Utils.getIndexList()) {
					if (index.getIndexNo().equals(indexNo)){
//						for (String studentID : index.getStudList()) {
//							System.out.println("\t" + studentID);
//						}
						index.printStuListInfo();
					}
					System.out.println();
				}
				break;
		}
	}
}