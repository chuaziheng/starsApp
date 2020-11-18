// package project2;

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
		if (indexNums.isEmpty()){
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
	public static void checkVacancy() {
		System.out.println("Enter course code to view its vacancies: ");
		String courseCode = sc.next();
		courseCode = courseCode.toUpperCase();
		try {
			int totalVacancy = Utils.getTotalVacancyForACourse(courseCode);
			System.out.printf("Total vacancies in course with course code %s: %d\n", courseCode, totalVacancy);
		} 
		catch (Exception e) {
			System.out.printf("Course %s does not exist!", courseCode.toUpperCase());
		}
		
	}

	public static boolean checkExistingStudent(String studentID) {
		ArrayList<Student> stuList = Utils.getStuList();
		for (Student student : stuList){
			if (student.getStudentID().equals(studentID)){
				return true; 
			}
		}
		System.out.println("Invalid StudentID");
		return false;
	}
	public static boolean checkExistingIndex(String indexNo) {
		ArrayList<Index> indexList = Utils.getIndexList();
		for (Index index : indexList){
			if (index.getIndexNo().equals(indexNo)){
				return true; 
			}
		}
		System.out.println("No existing index");
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
		Lesson l1 = new Lesson("HWLAB1", "10:30", "11:59", "Monday", "LAB"); // i1 
		Lesson l2 = new Lesson("HIVE", "15:30", "17:29", "Wednesday", "TUT"); //i1
		Lesson l3 = new Lesson("HWLAB1", "11:00", "13:29", "Tuesday", "LAB"); //i2
		Lesson l4 = new Lesson("HIVE", "10:30", "11:59", "Thursday", "TUT"); //i2

		Lesson l5 = new Lesson("HIVE", "13:30", "15:29", "Monday", "TUT"); //i3
		Lesson l6 = new Lesson("TR26", "11:30", "12:29", "Thursday", "SEM"); //i3    l6(i3) and l4(i2) clash
		Lesson l7 = new Lesson("HIVE", "13:30", "15:29", "Wednesday", "TUT"); //i4
		Lesson l8 = new Lesson("TR26", "11:30", "12:29", "Friday", "SEM"); //i4
		
		Lesson l9 = new Lesson("LT1", "15:30", "17:29", "Friday", "LECT");  //i5
		Lesson l10 = new Lesson("TR29", "09:00", "10:29", "Monday", "SEM"); //i5     l10(i5) and l15(i8) clash
		Lesson l11 = new Lesson("LT1", "15:30", "17:29", "Friday", "LECT");  //i6
		Lesson l12 = new Lesson("TR29", "09:00", "10:29", "Tuesday", "SEM"); //i6

		Lesson l13 = new Lesson("TR26", "11:30", "12:29", "Thursday", "SEM"); //i7
		Lesson l14 = new Lesson("HIVE", "16:00", "17:59", "Friday", "TUT");//i7
		Lesson l15 = new Lesson("TR26", "09:30", "10:29", "Monday", "SEM");//i8
		Lesson l16 = new Lesson("HIVE", "13:30", "15:29", "Tuesday", "TUT");//i8

		//cz2001 2 indices vacancy = 20
		ArrayList<Lesson> lessons1 = new ArrayList<Lesson>(); //for cz2001 i1
		lessons1.add(l1);
		lessons1.add(l2);
		ArrayList<Lesson> lessons2 = new ArrayList<Lesson>(); //for cz2001 i2
		lessons2.add(l3);
		lessons2.add(l4);

		Index i1 = new Index("CZ2001", "SCSE", 3, "1001", lessons1, 10); // init
		Index i2 = new Index("CZ2001", "SCSE", 3, "1002", lessons2, 10);

		//mod 3 will have 0 vacancy
		//cz2002 2 indices vacancy = 20
		ArrayList<Lesson> lessons3 = new ArrayList<Lesson>(); //for cz2002 i3
		lessons3.add(l5);
		lessons3.add(l6);
		ArrayList<Lesson> lessons4 = new ArrayList<Lesson>(); //for cz2002 i4
		lessons4.add(l7);
		lessons4.add(l8);

		Index i3 = new Index("CZ2002", "SCSE", 3, "1100", lessons3, 6); // init should have 0 vacancy
		Index i4 = new Index("CZ2002", "SCSE", 3, "1101", lessons4, 10);
		
		//MH2500 i5 vacancy set to zero to check waitlist logic 
		//mh2500 2 indices vacancy =20
		ArrayList<Lesson> lessons5 = new ArrayList<Lesson>();
		lessons5.add(l9);
		lessons5.add(l10);
		ArrayList<Lesson> lessons6 = new ArrayList<Lesson>();
		lessons6.add(l11);
		lessons6.add(l12);		
		
		Index i5 = new Index("MH2500", "SPMS", 3, "2100", lessons5, 10); 
		Index i6 = new Index("MH2500", "SPMS", 3, "2101", lessons6, 10); 
		
		//hw0128 2 indices vacancy =20
		ArrayList<Lesson> lessons7 = new ArrayList<Lesson>();
		lessons7.add(l13);
		lessons7.add(l14);
		ArrayList<Lesson> lessons8 = new ArrayList<Lesson>();
		lessons8.add(l15);
		lessons8.add(l16);	
		Index i7 = new Index("HW0128", "SBS", 3, "3100", lessons7, 10); 
		Index i8 = new Index("HW0128", "SBS", 3, "3101", lessons8, 10); 

		String[] mod1 = {i1.getCourseCode(), i1.getIndexNo()};
		String[] mod2 = {i2.getCourseCode(), i2.getIndexNo()};
		String[] mod3 = {i3.getCourseCode(), i3.getIndexNo()};
		String[] mod4 = {i4.getCourseCode(), i4.getIndexNo()};
		String[] mod5 = {i5.getCourseCode(), i5.getIndexNo()};
		String[] mod6 = {i6.getCourseCode(), i6.getIndexNo()};
		String[] mod7 = {i7.getCourseCode(), i7.getIndexNo()};
		String[] mod8 = {i8.getCourseCode(), i8.getIndexNo()};

		ArrayList<String[]> modA = new ArrayList<String[]>();
		ArrayList<String[]> modB = new ArrayList<String[]>();
		ArrayList<String[]> modC = new ArrayList<String[]>();
		ArrayList<String[]> modD = new ArrayList<String[]>();
		ArrayList<String[]> modE = new ArrayList<String[]>();
		ArrayList<String[]> modF = new ArrayList<String[]>();
		ArrayList<String[]> modG = new ArrayList<String[]>();
		ArrayList<String[]> modH = new ArrayList<String[]>();
		ArrayList<String[]> modI = new ArrayList<String[]>();
		ArrayList<String[]> modJ = new ArrayList<String[]>();
		ArrayList<String[]> modK = new ArrayList<String[]>();
		ArrayList<String[]> modL = new ArrayList<String[]>();
		ArrayList<String[]> modM = new ArrayList<String[]>();
		ArrayList<String[]> modN = new ArrayList<String[]>();
		ArrayList<String[]> modO = new ArrayList<String[]>();
		
		modA.add(mod1);
		modA.add(mod5);
		modB.add(mod2);
		modB.add(mod6); 
		modC.add(mod3);
		modC.add(mod5);
		modD.add(mod4);
		modD.add(mod7); 
		modE.add(mod3);
		modE.add(mod6);
		modF.add(mod1);
		modF.add(mod7); 
		modG.add(mod2);
		modG.add(mod8);
		modH.add(mod3);
		modH.add(mod6); 
		modI.add(mod4);
		modI.add(mod8);
		modJ.add(mod2);
		modJ.add(mod7); 
		modK.add(mod3);
		modK.add(mod7);
		modL.add(mod1);
		modL.add(mod3); 
		modM.add(mod2);
		modM.add(mod4);
		modN.add(mod3);
		modN.add(mod7); 
		modO.add(mod5);
		modO.add(mod8);
		
		//NOTE: 6 students taking mod 3
		
		Student s1 = new Student("aa01", PasswordHashController.hash("aa01"), "Adam", "U1823498E", "Singaporean", 'M', "SCSE", modA, "12:30", "23:30", "18/11/2020");
		Student s2 = new Student("bb01", PasswordHashController.hash("b01"), "Benny", "U1827392Y", "Malaysian", 'M', "SPMS", modB, "14:30", "16:30", "20/11/2020"); 
		Student s3 = new Student("cc01", PasswordHashController.hash("cc01"), "Cindy", "U1928372F", "Malaysian", 'F', "SPMS", modC, "14:30", "16:30", "20/11/2020");
		Student s4 = new Student("dd01", PasswordHashController.hash("dd01"), "David", "U1720394B", "Singaporean", 'M',"SCSE", modD, "14:30", "16:30", "20/11/2020");
		Student s5 = new Student("ee01", PasswordHashController.hash("ee01"), "Evelyn", "U1817294C", "American", 'F'," EEE", modE, "10:00", "12:00", "19/11/2020");
		Student s6 = new Student("ff01", PasswordHashController.hash("ff01"), "Frankie", "U1790390L", "Singaporean", 'M', "SCBE", modF, "12:30", "14:30", "22/11/2020");
		Student s7 = new Student("gg01", PasswordHashController.hash("gg01"), "Gwen", "U1892038P", "Singaporean", 'F'," MAE", modG, "09:00", "11:30", "13/11/2020");
		Student s8 = new Student("hh01", PasswordHashController.hash("hh01"), "Harry", "U1864537H", "Malaysian", 'M', "WKW", modH, "13:00", "15:00", "18/11/2020"); 
		Student s9 = new Student("ii01", PasswordHashController.hash("ii01"), "Ivan", "U1728394N", "Singaporean", 'M',"SCSE", modI, "14:30", "16:30", "20/11/2020");
		Student s10 = new Student("jj01", PasswordHashController.hash("jj01"), "Jennie", "U1983744J", "Singaporean", 'F',"ADM", modJ, "11:00", "23:00", "18/11/2020");
		Student s11 = new Student("kk01", PasswordHashController.hash("kk01"), "Katie", "U1709903T", "Indonesian", 'F', "EEE", modK, "10:00", "12:00", "19/11/2020"); 
		Student s12 = new Student("ll01", PasswordHashController.hash("ll01"), "Lisa", "U1938822R", "Singaporean", 'F',"SCSE", modL, "14:30", "16:30", "20/11/2020");
		Student s13 = new Student("mm01", PasswordHashController.hash("mm01"), "Mandy", "U1827263Y", "Malaysian", 'F', "ADM", modM, "11:00", "13:00", "21/11/2020"); 
		Student s14 = new Student("nn01", PasswordHashController.hash("nn01"), "Nigel", "U1822113N", "Singaporean", 'M',"MAE", modN, "09:00", "11:30", "13/11/2020");
		Student s15 = new Student("oo01", PasswordHashController.hash("oo01"), "Oscar", "U1899773D", "American", 'M'," SCSE", modO, "14:30", "16:30", "20/11/2020");
		Admin a1 = new Admin("d", PasswordHashController.hash("d"));

		indexList = new ArrayList<Index>();
		stuList = new ArrayList<Student>();
		adminList = new ArrayList<Admin>();

		indexList.add(i1);
		indexList.add(i2);
		indexList.add(i3);
		indexList.add(i4);
		indexList.add(i5);
		indexList.add(i6);
		indexList.add(i7);
		indexList.add(i8);
		// sav in DB
		save("index");
		load("index");
		stuList.add(s1);
		stuList.add(s2);
		stuList.add(s3);
		stuList.add(s4);
		stuList.add(s5);
		stuList.add(s6);
		stuList.add(s7);
		stuList.add(s8);
		stuList.add(s9);
		stuList.add(s10);
		stuList.add(s11);
		stuList.add(s12);
		stuList.add(s13);
		stuList.add(s14);
		stuList.add(s15);

		adminList.add(a1);
		// call by reference from DB
		// shld update indexList
		for (Student stud: getStuList()) {
			for (String[] mod: stud.getModules()) {
				Index ii = Utils.getIndexFromIndexNum(mod[1]);
				ii.appendToStuList(stud.getStudentID());
				}
		}

		
		
		

		// save both to DB again
		// we save("index") to update the file
		save("student");
		load("student");
		save("index");
		load("index");
		System.out.println();
	}
}