// package project2;

import java.util.*;

import java.io.Console;
import java.io.Serializable;

public class MainApp implements Serializable{
	
	final static long serialVersionUID = 123;
	public static void main(String[] args) throws Exception{
		
		int choice = 1;
		
		Console cons = System.console();
		Admin admin = new Admin();
		Student student = new Student();
		
		//Load all the dat files into the respective arrayLists
		Utils.populate();
		
		Scanner sc = new Scanner(System.in);
		do {
			try{
				System.out.println("**********Welcome to NTU STARS Planner!**********");
				System.out.println("Please select option.");
				System.out.println("1: Student");
				System.out.println("2: Admin");
				System.out.println("3: Exit");
				System.out.println("Enter choice: ");
				
				choice = sc.nextInt();
				sc.nextLine();
				
				switch (choice){
					case 1:
						if(cons == null) {
							System.out.println("console is null");
						}
						System.out.println("Enter studentID: ");										//user will enter both username and password during login
						String studentID = sc.next();
						student = Utils.getStudentFromStuID(studentID);
						
							char[] studentPassword = cons.readPassword("Please enter your password: ");
							String studentPasswordStr = String.valueOf(studentPassword);
							String studentPasswordHash = PasswordHashController.hash(studentPasswordStr);
							
						// returns a boolean ie. checks if the username and password matches
						if (PasswordHashController.checkUsernameAndPassword(student.getUsername(),"student", studentPasswordHash)) {
							// student = Utils.getStudentFromStuID(studentID);
							// check if student is accessing during his own access time, nothing happens if during access time, if not during access time, print message, then return to main menu/terminate	
							if (student.checkAccessTime()) {
								StudentApp.StudentMenu(student);
							}
						
						} else {
								System.out.println("\nInvalid password!\n");
							}
						break;
						
					case 2:
						System.out.println("Enter adminID: ");
						String adminID = sc.next();
						
						char[] adminPassword = cons.readPassword("Please enter your password: ");
						String adminPasswordStr = String.valueOf(adminPassword);
						String adminPasswordHash = PasswordHashController.hash(adminPasswordStr);
						System.out.println("HERE 1" + adminPasswordHash);
						
						if (PasswordHashController.checkUsernameAndPassword(adminID,"admin", adminPasswordHash)) {
							System.out.println("HERE 2");
							for (Admin a: Utils.getAdminList()) {
								if (a.getUsername().equals(adminID)) {
									admin = a;
									break;
								}
							}		
							AdminApp.AdminMenu(admin);
						} else {
							System.out.println("Invalid adminID or password!\n");
						}
						break;

					case 3:
						System.out.println("System terminating...");
						System.exit(0);
						break;

					default:
						System.out.println("Invalid Input! Please choose either 1 or 2 or 3!");
				}
			}
			catch (Exception e) {
				sc.nextLine();
				System.out.println("Invalid Input! Please choose either 1 or 2 or 3! ");
				continue;
			}
		} while(choice != 3);
	}		
}