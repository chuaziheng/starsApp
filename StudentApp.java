package project2.starsApp;
import java.util.Scanner;

public class StudentApp {
	public static void StudentMenu(Student me) throws Exception {

		int choice = 0;
		Scanner sc = new Scanner(System.in);
		
		do {
			System.out.println("\n1. Add course");
			System.out.println("2. Drop course");
			System.out.println("3. Swap index");
			System.out.println("4. Change index");
			System.out.println("5. Check vacancy");
			System.out.println("6. Print enrolled courses");
			System.out.println("7. Log out");
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
					Utils.checkVacancy();
					break;
				case 6:
					me.printModules();
					break;
			}
			Utils.save("index");
			Utils.save("student");
			Utils.prettyPrint();
		} while (choice < 7);
	System.out.println("Logging out of student");
	}
}
