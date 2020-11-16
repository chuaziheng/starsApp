package project2.starsApp;


import java.util.*;
import java.io.Console;
import java.io.Serializable;

public class MainApp implements Serializable{
	
	final static long serialVersionUID = 123;
	public static void main(String[] args) throws Exception{
		
		int choice;
		
		Console cons = System.console();
		
	
//		Lesson l1 = new Lesson("ARC", "10:30", "11:59", "Monday", "LECT");
//	    Lesson l2 = new Lesson("HIVE", "15:30", "17:29", "Tuesday", "TUT");
//	    Lesson l3 = new Lesson("TR29", "12:00", "13:29", "Monday", "SEM");
//	    ArrayList<Lesson> lessons = new ArrayList<Lesson>();
//	    lessons.add(l1);
//	    lessons.add(l2);
//	    //lessons.add(l3);
//	    Index i1 = new Index(1234, lessons, 35); // init
//	    ArrayList<Index> indices = new ArrayList<Index>();
//	    indices.add(i1);
//	    Course c1 = new Course("CZ2002", indices, "SCSE", 3); // init
//
//	    Object[] mod = new Object[2];
//	    mod[0] = c1;
//	    mod[1] = i1;
//	    ArrayList<Object[]> modules = new ArrayList<Object[]>();
//	    modules.add(mod);
//		Student student = new Student("a","a","Adam","U1823498E", "Singaporean",'M',"6598765432","SCBE", modules, 0, "17:00", "18:30", "10/11/2020");
//		
		Admin admin = new Admin();
		Student student = new Student();
		
		//Load all the dat files into the respective arrayLists
		Utils.populate();
//		
		Scanner sc = new Scanner(System.in);
		//admin.getAdminID() == null || student.getStudentID() == null
		//ask user to choose either student or admin
		do {
			System.out.println("**********Welcome to NTU STARS Planner!**********");
			System.out.println("Please select option.");
			System.out.println("1: Student");
			System.out.println("2: Admin");
			System.out.println("3: Exit");
			System.out.println("Enter choice: ");
			
			choice = sc.nextInt();
			
			if (choice == 1) {
					System.out.println("Enter studentID: ");										//user will enter both username and password during login
					String studentID = sc.next();
					
//					if(cons != null) {
//						
//						char[] studentPassword = cons.readPassword("Please enter your password: ");}
//					System.out.println("Please enter your password: ");
					String studentPassword = "a";
					//String studentPasswordStr = new String(studentPassword);
//					String studentPasswordHash = PasswordHashController.hash(studentPassword);
					
//					if (PasswordHashController.checkUsernameAndPassword(studentID, studentPasswordHash)) { 		//returns a boolean ie. checks if the username and password matches
					if (true) { 
						student = Utils.getStudentFromStuID(studentID);
																		//check if student is accessing during his own access time, nothing happens if during access time, if not during access time, print message, then return to main menu/terminate	
						if (student.checkAccessTime()) {
							StudentApp.StudentMenu(student);
						}
					
					} else {
							System.out.println("Invalid studentID or password!\n");
						}
					
			} else if (choice == 2){
				
					System.out.println("Enter adminID: ");
					String adminID = sc.next();
					
//					char[] adminPassword = cons.readPassword("Please enter your password: ");
//					String adminPasswordStr = new String(adminPassword);
//					String adminPasswordHash = PasswordHashController.hash(adminPasswordStr);
					
//					if (PasswordHashController.checkUsernameAndPassword(adminID, adminPasswordHash)) {
//						for (Admin a: Utils.getAdminList()) {
//							if (a.getAdminID().equals(adminID)) {
//								admin = a; // Get student object from studentID
//							}
//						}		
//						AdminApp.AdminMenu(admin);
//					} else {
//						System.out.println("Invalid adminID or password!\n");
//					}
					String adminPasswordStr = "d";
					if (true) { 
						admin = Utils.getAdminFromAdminID(adminID);
						//if (student.checkAccessTime()) {	 //NEW: Check by school												//check if student is accessing during his own access time, nothing happens if during access time, if not during access time, print message, then return to main menu/terminate	
						if (true) {
							AdminApp.AdminMenu(admin);
						}
					} else {
							System.out.println("Invalid AdminID or password!\n");
						}
					
			} else {
					System.out.println("Invalid Input! Please choose either 1 or 2!");

			}
		} while(choice<3);
				
	}		
}


