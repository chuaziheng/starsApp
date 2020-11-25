package project2.starsApp;
/**
* <h2>Logic for AdminApp</h2>
* Contains the menu for the admin to pick the function
* they wish to execute, and calls the respective functions
* that the admin chooses.
* @author  Mun Kei Wuai, Tan Wen Xiu
* @version 1.0
* @since   2020-11-20
*/


import java.util.Scanner;

public class AdminApp {
	/**
	 * This method executes the menu that an admin login will see.
	 * @param admin
	 * @throws Exception
	 */
	public static void AdminMenu(Admin admin) throws Exception {
		
		int choice;
		Scanner sc = new Scanner(System.in);
		
		do {
			System.out.println("\nWelcome, Admin " + admin.getUsername());
			System.out.println("1: Add a new student");
			System.out.println("2: Delete student");
			System.out.println("3: Add a new course");
			System.out.println("4: Delete course");
			System.out.println("5: Add index groups"); 
			System.out.println("6: Delete index groups"); 
			System.out.println("7: Print list of course and index ");
			System.out.println("8: Check vacancy for an existing index group"); 
			System.out.println("9: Edit student access periods");
			System.out.println("10: Print list of all students");
			System.out.println("11: Print list of students by course");
			System.out.println("12: Print list of students by index group number");
			System.out.println("13: Change Password");
			System.out.println("14: Logout");
			System.out.println("Please choose one of the options above.");
			
			choice = sc.nextInt();
			
			switch (choice) {
				case 1:
					admin.addStudent();
					admin.printStuList("all");
					break;
				case 2:
					admin.deleteStudent();
					break;
				case 3:
					admin.addModule();
					admin.printCourseIndexList();
					break;
				case 4:
					admin.deleteIndexOrCourse(false);
					break;
				case 5:
					admin.addIndex();
					break;
				case 6:
					admin.deleteIndexOrCourse(true);
					break;
				case 7:
					admin.printCourseIndexList();
					break;
				case 8: 
					ErrorHandling.checkVacancy();
					break;
				case 9:
					sc.nextLine();
					System.out.println("Enter username of student: ");
					String username = sc.nextLine();
					Student student1 = DataBase.getStudentFromStuID(username);
					admin.studentAccessPeriod(student1);
					break;
				case 10:
					admin.printStuList("all");
					break;
				case 11:
					admin.printStuList("course");
					break;
				case 12:
					admin.printStuList("index");
					break;
				case 13:
					admin.resetPassword();
					break;
				default:
					System.out.println("Please choose a proper option");
					break;
			}
			DataBase.save("index");
			DataBase.save("student");
		} while (choice > 0 && choice < 14);
	}
}
