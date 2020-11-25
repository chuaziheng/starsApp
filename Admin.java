package project2.starsApp;
import java.io.Serializable;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Set;

/**
* <h2>Logic for Admin</h2>
* Contains methods that allow the various implementations
* of an administrator such as creating and deleting a 
* a course or student, updating the student access time,
* and printing details of students and indices.
*
* @author  Mun Kei Wuai, Tan Wen Xiu, Goh Nicholas
* @version 1.0
* @since   2020-11-20
*/

public class Admin extends User implements Serializable, AdminCourseManagement, AdminStudentManagement{
	
	final static long serialVersionUID = 123; 
	transient static Scanner sc = new Scanner(System.in);
	private static String courseCode;
	
	public Admin() {}
	
	public Admin(String adminID, String adminPasswordHash) {
		super(adminID,adminPasswordHash);
	}


	// ********************************************************case 1**********************************************************
	/** 
	 * This method is used to create a new student and add it to the database
	 **/
	public void addStudent() {
		System.out.println("Enter the new student's ID: ");
		String studentID = sc.nextLine();

		if (!ErrorHandling.checkExistingStudent(studentID, false)){
			System.out.println("Enter the new student's password: ");
			String passwordHash = sc.nextLine();

			System.out.println("Enter the new student's name: ");
			String name = sc.nextLine();

			System.out.println("Enter the new student's matriculation number: ");
			String matricNum = sc.nextLine();

			System.out.println("Enter the new student's nationality: ");
			String nationality = sc.nextLine();

			System.out.println("Enter the new student's gender (M/F): ");
			char gender = ErrorHandling.checkGender(Character.toLowerCase(sc.nextLine().charAt(0)));

			System.out.println("Enter the new student's school (eg. SCSE): ");
			String schoolName = sc.nextLine();

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
						DataBase.getStuList().add(newStudent);
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
	/** 
	 * This method is used to delete a student from the database.
	 **/
	public void deleteStudent() throws Exception {
		System.out.println("Enter the ID of the student to be deleted: ");
		String studentID = sc.next();
		Student student = DataBase.getStudentFromStuID(studentID);

		for (String[] mod : student.getModules()) {
			Index index = DataBase.getIndexFromIndexNum(mod[1]);
			index.dropStud(studentID);
		}
		DataBase.getStuList().remove(student);
		
	}
	
	// ********************************************************case 3**********************************************************
	/** 
	 * This method is used to add a new module to the course database for students to add or drop.
	 **/
	public void addModule() {
			
		System.out.println("Course code: "); 
		String courseCode = sc.nextLine(); 
		courseCode = courseCode.toUpperCase();

		   while (ErrorHandling.checkExistingCourse(courseCode, false)) {
		    System.out.println("Please enter a new course code!"); 
		    courseCode = sc.nextLine();
			courseCode = courseCode.toUpperCase();
		   } 
		
		System.out.println("School (eg. SCSE): "); 
		String school = sc.nextLine(); 
		

    	System.out.println("Number of AUs: "); 

    	int acadUnit = ErrorHandling.checkInteger(sc.nextLine());
    	
		System.out.print("Number of indices: ");
		int noOfIndices = ErrorHandling.checkInteger(sc.nextLine());
		
		ArrayList<Index> indexList = DataBase.getIndexList();
		for (int i=0; i<noOfIndices; i++) {

		    System.out.printf("Vacancy for Index %d: ", (i+1)); 
			int vacancy = ErrorHandling.checkInteger(sc.nextLine());
		    
		    System.out.printf("Index number %d: ", (i+1)); 
		    String temp2 = sc.nextLine(); 
		    while (ErrorHandling.checkExistingIndex(temp2, false)){
		     System.out.println("Index exists. Please enter a new index number!"); 
		     temp2 = sc.nextLine();
		    }
		    String indexNo = temp2; 
		    
		    Index newIndex = new Index(courseCode, school, acadUnit, indexNo, addLesson(), vacancy);
		    indexList.add(newIndex);
		    }
		}
	
	// ********************************************************case 4 or 6**********************************************************
	/** 
	 * This method is used to delete a course or index from the course database.
	 *  @param byIndex  If true, delete the course, including all the index; if false, delete index from course 
	 *  @throws Exception
	 * */
	public void deleteIndexOrCourse(boolean byIndex) throws Exception{
		// !byIndex means remove all index in this courseCode
		// otherwise remove specific one
		if (!byIndex){
			System.out.println("Enter the course code to be deleted: ");
			courseCode = sc.next();
			ArrayList<String> indexNums = DataBase.getIndexNumsFromCourseCode(courseCode);
			
			for (String indexNum : indexNums) {
				Index index = DataBase.getIndexFromIndexNum(indexNum);
				DataBase.getIndexList().remove(index);
				for (Student s : DataBase.getStuList()) {
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
			   
			   while(!ErrorHandling.checkExistingIndex(temp4, false)) {
				   System.out.println("Please enter valid index number!");
					temp4 = sc.nextLine(); 
			   }
			   
			   Index removeIndex = DataBase.getIndexFromIndexNum(temp4);
			   DataBase.getIndexList().remove(removeIndex);
			   for (Student s : DataBase.getStuList()) {
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
	/** 
	 * This method is used to add an index to the course database.
	 *  @throws Exception
	 */
	public void addIndex() throws Exception {
		System.out.println("Enter option of existing course: ");
		int counter = 1;
		Set<String> courseCodes = DataBase.getAllCourseCodes();
		for (String courseCode: courseCodes){
			System.out.printf("\t(%d) %s\n", counter, courseCode);
			counter++;
		}
		int option = sc.nextInt();
		sc.nextLine();
		option--;
		int i = 0;
		for (String cc : courseCodes) {
			if (i == option){
				courseCode = cc;
				break;
			}
			i++;
		}
		ArrayList<String> indexNums = DataBase.getIndexNumsFromCourseCode(courseCode);
		System.out.println("Current indices are: ");
		for (String indexNum: indexNums){
			System.out.println("\t" + indexNum);
		}
		System.out.printf("Enter index number to be added to course %s: \n", courseCode);
		String temp3 = sc.nextLine(); 
	    while (ErrorHandling.checkExistingIndex(temp3, true)){
	     System.out.println("Please enter a new index number!"); 
	     temp3 = sc.nextLine();
	    }
	    String newIndexNum = temp3; 


		System.out.println("Enter vacancy for this index: "); 
		int vacancy = sc.nextInt(); 
		sc.nextLine();
		
		Index anExistingIndex = DataBase.getIndexFromIndexNum(indexNums.get(0));
		Index newIndex = new Index(courseCode, anExistingIndex.getSchool(), anExistingIndex.getAcadUnit(), newIndexNum, addLesson(), vacancy);
		DataBase.getIndexList().add(newIndex);
	}
	// ********************************************************case 7**********************************************************
	/**
	 * This method is used to add lessons, of an index to the course database.
	 * @return the ArrayList of Lessons for the index selected.
	 */
	public ArrayList<Lesson> addLesson() { 
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
	
	// ********************************************************case 9**********************************************************
	/**
	 * This method accepts input of the access start and end time to save to the student.
	 * @param s This is the Student object whose access time is to be updated.
	 */
	public void studentAccessPeriod(Student s) {
		
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
	/**
	 * This method prints all the courses and their respective indices.
	 */
	public void printCourseIndexList() {
		System.out.println("-----------------------------------------------------------------------------");
	    System.out.printf("%-20s %-15s", "COURSE", "INDEX");
	    System.out.println();
	    System.out.println("-----------------------------------------------------------------------------");
	    for(Index index: DataBase.getIndexList() ){
	        System.out.format("%-20s %-15s",
	                index.getCourseCode(), index.getIndexNo());
	        System.out.println();
	    }
	    System.out.println("-----------------------------------------------------------------------------");
	}
	/**
	 * This method prints a list of students from the database.
	 * @param byWhat  takes values "all","course" and "index"
	 * @throws Exception
	 */
	public void printStuList(String byWhat) throws Exception{
		switch (byWhat) {
			case "all":
				System.out.println("List of all current students: ");
				System.out.println("-----------------------------------------------------------------------------");
			    System.out.printf("%-15s %-8s %-15s", "NAME", "GENDER", "NATIONALITY");
			    System.out.println();
			    System.out.println("-----------------------------------------------------------------------------");
			    for(Student student:DataBase.getStuList() ){
			        System.out.format("%-15s %-8c %-15s",
			                student.getName(), student.getGender(), student.getNationality());
			        System.out.println();
			    }
			    System.out.println("-----------------------------------------------------------------------------");
				break;
			case "course":
				System.out.println("Enter courseCode to see which students in this course: ");
				String courseCode = sc.nextLine();
				courseCode = courseCode.toUpperCase();
				System.out.printf("Students in course %s:\n", courseCode);
				for (Index index : DataBase.getIndexList()) {
					if (index.getCourseCode().equals(courseCode)){
						index.printStuListInfo();
					}
					System.out.println();
				}
				break;
			case "index":
				System.out.println("Enter index number to see which students in this index: ");
				String indexNo = sc.nextLine();
				System.out.printf("Students in index %s:\n", indexNo);
				for (Index index : DataBase.getIndexList()) {
					if (index.getIndexNo().equals(indexNo)){
						index.printStuListInfo();
					}
					System.out.println();
				}
				break;
		}
		
	}
}
