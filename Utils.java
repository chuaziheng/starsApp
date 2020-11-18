package project2.starsApp;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.io.Serializable;

public class Utils implements Serializable
{
	// Initialize arrayLists that acts as the database for the app
	final static long serialVersionUID = 123; 
	private static Scanner sc = new Scanner(System.in);
	private static ArrayList<Student> stuList = new ArrayList<Student>();
	private static ArrayList<Admin> adminList = new ArrayList<Admin>();
	private static ArrayList<Index> indexList = new ArrayList<Index>();
	//call these arrayList getters to retrieve/change information from DB
	public static ArrayList<Student> getStuList() {
		return stuList;
	}
	public static ArrayList<Index> getIndexList() {
		return indexList;
	}
	public static ArrayList<Admin> getAdminList() {
		return adminList;
	}

	// special getters ---------------------------------------
	public static ArrayList<String> getIndexNumsFromCourseCode(String courseCode) throws Exception{
		courseCode.toUpperCase();
		ArrayList<String> indexNums = new ArrayList<String>();
		for (Index i: indexList){
			if (i.getCourseCode().equals(courseCode)){
				indexNums.add(i.getIndexNo());
			}
		}
		if (indexNums.equals(new ArrayList<String>())){
			throw new Exception(String.format("\nCourse %s does not exist!\n", courseCode));
		}
		return indexNums;
	}

	public static Index getIndexFromIndexNum(String indexNum) throws Exception{
		for (Index index: indexList){
			if (index.getIndexNo().equals(indexNum)){
				return index;
			}
		}
		throw new Exception(String.format("\nNo index with index number: " + indexNum));
	}

	public static Set<String> getAllCourseCodes(){
		Set<String> courseCodes = new HashSet<String>();
		for (Index index: indexList){
			courseCodes.add(index.getCourseCode());
		}
		return courseCodes;
	}

	public static Student getStudentFromStuID(String studentID){
		while (!checkExistingStudent(studentID)) {
			System.out.println("Please enter valid student ID!: ");
			String temp = sc.nextLine();
			studentID = temp;
		}
		for (Student s: getStuList()) {
			if (s.getStudentID().equals(studentID)) {
				return s;
			}
		} return null; // will never be executed
	}
	public static Admin getAdminFromAdminID(String adminID) throws Exception{
		for(Admin a: getAdminList()) {
			if(a.getAdminID().equals(adminID)){
				return a;
			}
		}
		System.out.printf("\nUtils.getAdminFromAdminID(String adminID):\n\tadminID: %s not found\n", adminID);
		throw new Exception("no admin with that id");
	}

	public static int getTotalVacancyForACourse(String courseCode) throws Exception{
		ArrayList<String> indexNums = getIndexNumsFromCourseCode(courseCode);

		int totalVacancy = 0;
		for (String indexNum: indexNums){
			totalVacancy += Utils.getIndexFromIndexNum(indexNum).getVacancy();
		}
		return totalVacancy;
	}

	// utility methods --------------------------------------------------------------
	public static void checkVacancy() throws Exception{
		System.out.println("Enter course code of course to view its vacancies: ");
		String courseCode = sc.next();
		courseCode.toUpperCase();
		int totalVacancy = Utils.getTotalVacancyForACourse(courseCode);
		System.out.printf("Total vacancies in course with course code %s: %d\n", courseCode, totalVacancy);
	}

	public static boolean checkExistingStudent(String studentID) {
		ArrayList<Student> stuList = Utils.getStuList();
		for (Student student : stuList){
			if (student.getStudentID().equals(studentID)){
				return true; 
			}
		}
		System.out.println("no existing student");
		return false;
	}
	public static boolean checkExistingIndex(String indexNo) {
		ArrayList<Index> indexList = Utils.getIndexList();
		for (Index index : indexList){
			if (index.getIndexNo().equals(indexNo)){
				return true; 
			}
		}
		System.out.println("no existing index");
		return false;
	}

	public static boolean checkExistingCourse(String course) {
		for (String c : getAllCourseCodes()){
			if (c.equals (course)){
				return true; 
			}
		}
		System.out.println("no existing course");
		return false;
	}


	// pretty print DB ---------------------------------------
	public static void prettyPrint() throws Exception{
		// courses -----------
		System.out.println("\n\nUtils.prettyPrint()------------");
		for (String courseCode: getAllCourseCodes()){
			ArrayList<String> indexNums = getIndexNumsFromCourseCode(courseCode);
			System.out.printf("coursecode: %s\ttotalVacancy: %d\n", courseCode, getTotalVacancyForACourse(courseCode));
			for (String indexNum: indexNums){
				Index index = getIndexFromIndexNum(indexNum);
				System.out.printf("\tindex: %s\tvacancy: %d\t", index.getIndexNo(), index.getVacancy());
				if (index.getVacancy() == 0) {
					System.out.print("waitlisted: ");
					for (String sid: index.getWaitList()){
						System.out.print(sid + " ");
					}
				}
				System.out.println();
			}
		}
		for (Student s: stuList){
			System.out.printf("student name: %s\n", s.getName());
			for (String[] mod: s.getModules()){
				Index index = getIndexFromIndexNum(mod[1]);
				System.out.printf("\tcoursecode: %s\ttotalVacancy: %d", index.getCourseCode(), getTotalVacancyForACourse(index.getCourseCode()));
				System.out.printf("\tindex: %s\tvacancy: %d\n", index.getIndexNo(), index.getVacancy());
				if (index.getVacancy() == 0) {
					System.out.print("waitlisted: ");
					for (String sid: index.getWaitList()){
						System.out.print(sid + " ");
					}
				}
			}
			System.out.println();
		}
		System.out.println("-----------------------------");
	}
	@SuppressWarnings("unchecked")
	public static void load(String choice) throws Exception{

		FileInputStream fis = null;
		ObjectInputStream in = null;
		choice += ".dat";
		try {
			switch (choice){
				case "student.dat":
					fis = new FileInputStream(choice);
					in = new ObjectInputStream(fis);
					stuList = (ArrayList<Student>) in.readObject();
					break;
				case "admin.dat":
					fis = new FileInputStream(choice);
					in = new ObjectInputStream(fis);
					adminList = (ArrayList<Admin>) in.readObject();
					break;
				case "index.dat":
					fis = new FileInputStream(choice);
					in = new ObjectInputStream(fis);
					indexList = (ArrayList<Index>) in.readObject();
					break;
				default:
					throw new Exception("unknown file");
			}
			fis.close();
			in.close();
			//System.out.println("after loading " + choice);
			//System.out.println();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	public static void save(String choice){
		try {
			// write to file
			choice += ".dat";
			//System.out.println("before saving " + choice);
			FileOutputStream fos = new FileOutputStream(choice);
			ObjectOutputStream out = new ObjectOutputStream(fos);

			switch (choice) {
				case "student.dat":
					out.writeObject(stuList);
					break;
				case "admin.dat":
					out.writeObject(adminList);
					break;
				case "index.dat":
					out.writeObject(indexList);
					break;
				default:
					out.close();
					fos.close();
					throw new Exception("unknown file");
			}
			out.close();
			fos.close();
			//System.out.println("after saving " + choice);
			//System.out.println();
		}  catch (IOException ex) {
			ex.printStackTrace();
		}
		catch ( Exception e ) {
			System.out.println( "Exception >> " + e.getMessage() );
		}
	}
	public static LocalDateTime convertDate  (String s) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime dateTime = null;
		try {
			dateTime = LocalDateTime.parse(s,formatter);
		}
		catch (DateTimeParseException  e) {
			e.printStackTrace();
			return null;
		}
		return dateTime;
	}
	// use this to generate some input
	public static void populate() throws Exception {
		int defaultAU = 0;

		//create lessons 
		Lesson l1 = new Lesson("ARC", "10:30", "11:59", "Monday", "LECT");
		Lesson l2 = new Lesson("HIVE", "15:30", "17:29", "Tuesday", "TUT");
		Lesson l3 = new Lesson("TR29", "11:00", "13:29", "Monday", "SEM");
		Lesson l4 = new Lesson("ARC", "10:30", "11:59", "Thursday", "LECT");
		Lesson l5 = new Lesson("HIVE", "13:30", "15:29", "Tuesday", "TUT");
		Lesson l6 = new Lesson("TR26", "11:30", "12:29", "Thursday", "SEM"); //l6&l4 timing clash
		Lesson l7 = new Lesson("LT2", "14:00", "16:29", "Friday", "LECT");

		//cz2002 2 indices vacancy = 70
		ArrayList<Lesson> lessons = new ArrayList<Lesson>(); //for cz2002 i1
		lessons.add(l1);
		lessons.add(l3);
		ArrayList<Lesson> lessons1 = new ArrayList<Lesson>(); //for cz2002 i2
		lessons1.add(l2);
		Index i1 = new Index("CZ2002", "SCSE", 3, "1234", lessons, 35); // init
		Index i2 = new Index("CZ2002", "SCSE", 3, "1235", lessons1, 35);

		//hw0128 hw0128&cz2001 clash 
		ArrayList<Lesson> lessons2 = new ArrayList<Lesson>();
		lessons2.add(l4);
		lessons2.add(l5);
		Index i3 = new Index("HW0128", "SCSE", 3, "1236", lessons2, 35);
		ArrayList<Lesson> lessons3 = new ArrayList<Lesson>();
		lessons3.add(l6);
		Index i4 = new Index("HW0128", "SCSE", 3, "1237", lessons3, 35);

		//cz2001 vacancy set to zero to check waitlist logic 
		ArrayList<Lesson> lessons4 = new ArrayList<Lesson>();
		lessons4.add(l7);
		Index i5 = new Index("CZ2001", "SCSE", 3, "1238", lessons4, 0); 

		String[] mod = {i1.getCourseCode(), i1.getIndexNo()};
		ArrayList<String[]> modules = new ArrayList<String[]>();
		modules.add(mod);

		String[] mod2 = {i3.getCourseCode(), i3.getIndexNo()};
		ArrayList<String[]> modules2 = new ArrayList<String[]>();
		modules2.add(mod2);

		Student s1 = new Student("a", PasswordHashController.hash("a"), "ADAM", "U1823498E", "Singaporean", 'M', "SCBE", modules, "16:00", "23:59", "16/11/2020"); // init
		Student s2 = new Student("c", PasswordHashController.hash("c"), "CINDY", "U1876839K", "Singaporean", 'F'," SCSE", modules2, "19:00", "22:30", "10/11/2020");
		//Student s3 = new Student("d","d","David","U1742694E", "American",'M',"6598765432","NBS", modules,defaultAU, "17:00", "18:30", "10/11/2020");
		Admin a1 = new Admin("d", PasswordHashController.hash("d"));

		indexList = new ArrayList<Index>();
		stuList = new ArrayList<Student>();
		adminList = new ArrayList<Admin>();

		indexList.add(i1);
		indexList.add(i2);
		indexList.add(i3);
		indexList.add(i4);
		indexList.add(i5);
		// sav in DB
		save("index");
		load("index");

		// call by reference from DB
		// shld update indexList
		s1.getIndexFromCourseCode(i1.getCourseCode()).appendToStuList(s1.getStudentID());
		s2.getIndexFromCourseCode(i3.getCourseCode()).appendToStuList(s2.getStudentID());

		stuList.add(s1);
		stuList.add(s2);
		adminList.add(a1);

		// save both to DB again
		// we save("index") to update the file
		save("student");
		load("student");
		save("index");
		load("index");
		System.out.println();
	}
}

