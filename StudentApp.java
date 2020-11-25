
import java.util.Scanner;
/**
* <h2>Application for student functions</h2>
* Interface that allows users to choose from the various functionalities on the student menu.
* <p>
* 
* @author  Pooja Nag, Chua Zi Heng
* @version 1.0
* @since   2020-11-20
*/
public class StudentApp {
	/** 
	* Student Menu containing switch statement to choose function.
	* @param me 	Student object, determined from main app
	*/
	public static void StudentMenu(Student me) throws Exception {

		int choice = 0;
		Scanner sc = new Scanner(System.in);

		do {
			System.out.println("\nStudent Menu:");
			System.out.println("1. Add course");
			System.out.println("2. Drop course");
			System.out.println("3. Swap index");
			System.out.println("4. Change index");
			System.out.println("5. Check vacancy");
			System.out.println("6. Print enrolled courses");
			System.out.println("7. Print timetable");
			System.out.println("8. Reset Password");
			System.out.println("9. Log out");
			System.out.println("Enter choice: ");
				
			choice = sc.nextInt();
			sc.nextLine();

			switch (choice) {
				case 1:
					me.askAddModule();
					break;
				case 2:
					me.askDropModule(true);
					break;
				case 3:
					me.swapIndex();
					break;
				case 4:
					me.changeIndex();
					break;
				case 5:
					ErrorHandling.checkVacancy();
					break;
				case 6:
					me.printModules();
					break;
				case 7: 
					me.printTimetable(); 
					break;
				case 8: 
					me.resetPassword(); 
					break;
			}
			DataBase.save("index");
			DataBase.save("student");
		} while (choice < 9);
	System.out.println("Logging out of student");
	}
}
