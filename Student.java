package project2.starsApp;

import java.util.*;
import java.io.Serializable;

public class Student implements Serializable {
	final static long serialVersionUID = 123;
	private String studentID;
	private String passwordHash;
	private String name;
	private String matricNum;
	private String nationality;
	private char gender;
	private String schoolName;
	private String date; //DD/MM/YYYY
	private String startTime; //HH:MM
	private String endTime; //HH:MM
	private int acadUnit;
	transient static Scanner sc = new Scanner(System.in);
	private ArrayList<String[]> modules;

	// more basic constructor without courseList, indexGroupList, schedule
	public Student(){}
	public Student(String studentID, String passwordHash, String name, String matricNum, String nationality, char gender, String schoolName, String startTime, String endTime, String date) {
		this.studentID = studentID;
		this.passwordHash = passwordHash;
		this.name = name;
		this.matricNum = matricNum;
		this.nationality = nationality;
		this.gender = gender;
		this.schoolName = schoolName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
		this.modules = new ArrayList <String[]>();
	}

	public Student(String studentID, String passwordHash, String name, String matricNum, String nationality, char gender, String schoolName) {
		this.studentID = studentID;
		this.passwordHash = passwordHash;
		this.name = name;
		this.matricNum = matricNum;
		this.nationality = nationality;
		this.gender = gender;
		this.schoolName = schoolName;
		this.modules = new ArrayList <String[]>();
	}
	
	// full constructor
	public Student(String studentID, String passwordHash, String name, String matricNum, String nationality, char gender, String schoolName, ArrayList<String[]> modules, String startTime, String endTime, String date) {
		this.studentID = studentID;
		this.passwordHash = passwordHash;
		this.name = name;
		this.matricNum = matricNum;
		this.nationality = nationality;
		this.gender = gender;
		this.schoolName = schoolName;
		this.modules = modules;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
	}

	public boolean equals(Student s){
		if (s.getStudentID().equals(this.studentID)) return true;
		return false;
	}

	public int addModule()throws Exception{
		String courseCode;
		ArrayList<String> indexNums;

		if (this.getAcadUnit() >= 21) {
			System.out.println("\nMaximum academic units of 21 allocated. Not allowed to add courses!");
			return 0; //failure
		}
		System.out.println("Enter course code to add: ");
		courseCode = sc.next();
		indexNums = Utils.getIndexNumsFromCourseCode(courseCode);

		if(this.getAcadUnit() + Utils.getIndexFromIndexNum(indexNums.get(0)).getAcadUnit() >= 21) {
			System.out.printf("Adding %s exceeds maximum allowed academic units of 21. Not allowed to add course!", courseCode);
			return 0;
		}

		System.out.printf("Avaliable indices for course code %s are: \n", courseCode);

		// list vacancy for each index in selected course
		int counter = 1;
		for (String indexNum: indexNums){
			Index index = Utils.getIndexFromIndexNum(indexNum);
			System.out.printf("(%d) index number: %s vacancy: %d\n", counter, index.getIndexNo(), index.getVacancy());
			counter++;
		}
		// user selection of index
		int idxChoice;
		System.out.println("Enter option (1/2/...): ");
		idxChoice = sc.nextInt();
		idxChoice--;
		sc.nextLine();
		if (idxChoice >= indexNums.size()) throw new Exception("option too big");

		Index indexToAdd = null;
		// get the respective index object
		indexToAdd = Utils.getIndexFromIndexNum(indexNums.get(idxChoice));

		for (String[] mod : modules) {
			if (mod[1].equals(indexToAdd.getIndexNo())){
				System.out.println("Sorry, ure alr registered for this index");
				return 0; // tryna reg the same index
			}
		}

		if(this.isOverlappingSchedule(indexToAdd)) {
			System.out.println("\nOverlapping Schedule");
			return 0; // failure
		}
		else if (promptToJoinWaitList(indexToAdd)) {}
		else { // success so add module
			String[] mod = {courseCode, indexToAdd.getIndexNo()};
			// add to this student which is from DB
			this.modules.add(mod);
			// reference from courseList in Utils
			indexToAdd.appendToStuList(studentID);
			System.out.printf("Successfully added course %s, index %s\n", courseCode, indexToAdd.getIndexNo());
		}
		return 1;
	}

	public String[] dropModule(boolean drop)throws Exception{
		if (modules.isEmpty()) throw new Exception("u got no index to drop");
		int removeChoice;
		String[] mod = new String[2];
		printModules();

		System.out.println("Enter choice of course code to drop(1/2/...): ");
		removeChoice = sc.nextInt();
		sc.nextLine();
		removeChoice--;

		if (removeChoice >= modules.size()){
			System.out.println("pls choose a correct option");
			return new String[]{"", ""};
		}

		if (drop) {
			mod = modules.remove(removeChoice);

			//Remove student from Index (Remove from courseList DB)
			Utils.getIndexFromIndexNum(mod[1]).dropStud(studentID);
			Utils.getIndexFromIndexNum(mod[1]).popWaitListedStud();
			System.out.printf("course %s, index %s successfully dropped\n", mod[0], mod[1]);
		}
		else mod = modules.get(removeChoice);
		return mod;
	}

	public int swapIndex()throws Exception{
		//if (modules.isEmpty()) throw new Exception("u got no index to swap");
		String[] mod = dropModule(false);
		if (mod==new String[]{"", ""})throw new Exception("u got no index to swap"); 
		System.out.println("Enter username of student to swap index with: ");
		String username = sc.next();
		Student s = Utils.getStudentFromStuID(username);

		// get my details
		String myCourseCode = mod[0];
		String myIndexNum = mod[1];
		Index myIndex = getIndexFromCourseCode(myCourseCode);

		// myCourseCode is shared between me and s
		Index sIndex = s.getIndexFromCourseCode(myCourseCode);

		// swap the Index in DB
		myIndex.dropStud(studentID);
		sIndex.dropStud(s.getStudentID());
		myIndex.appendToStuList(s.getStudentID());
		sIndex.appendToStuList(studentID);

		//editing my modules
		modules.remove(mod);
		mod[1] = sIndex.getIndexNo();
		modules.add(mod);

		// Remove old module pair from s module arrayList
		s.removeMyModuleByIdxNum(sIndex.getIndexNo());
		s.getModules().add(new String[]{myCourseCode, myIndexNum});

		System.out.printf("successfully swapped course %s, index %s with %s course %s, index %s\n", myCourseCode, myIndexNum, s.getStudentID(), myCourseCode, sIndex.getIndexNo());
		return 1;
	}

	public int changeIndex() throws Exception{
		if (modules.isEmpty()) throw new Exception("u got no index to drop");
		int removeChoice;
		String courseCode;
		printModules();

		// get choice of course to change index
		System.out.println("Enter choice of course code to change index(1/2/...): ");
		removeChoice = sc.nextInt();
		sc.nextLine();
		removeChoice--;
		String[] oldMod = modules.get(removeChoice);
		courseCode = oldMod[0];

		// print indices:
		System.out.printf("Avaliable indices for course code %s are: \n", courseCode);

		// list vacancy for each index in selected course
		int count = 1;
		ArrayList<String> indexNums = Utils.getIndexNumsFromCourseCode(courseCode);
		for (String indexNum: indexNums){
			Index index = Utils.getIndexFromIndexNum(indexNum);
			System.out.printf("(%d) index number: %s vacancy: %d\n", count, index.getIndexNo(), index.getVacancy());
			count++;
		}

		// get index to change to
		int idxChoice;
		System.out.println("Enter choice of index number to change to(1/2/...): ");
		idxChoice = sc.nextInt();
		idxChoice--;
		if (idxChoice >= indexNums.size()) throw new Exception("invaliud option");
		sc.nextLine();

		// get the respective index object
		Index indexToAdd = Utils.getIndexFromIndexNum(indexNums.get(idxChoice));
		for (String[] mod : modules) {
			if (mod[1].equals(indexToAdd.getIndexNo())){
				System.out.println("alr reg for this index");
				return 0;
			}
		}

		if(this.isOverlappingSchedule(indexToAdd)) {
			System.out.println("\nOverlapping schedule!");
			return 0; // failure
		}
		else if (promptToJoinWaitList(indexToAdd)) {}
		else { // success so add module
			modules.remove(oldMod);
			modules.add(new String[]{oldMod[0], indexToAdd.getIndexNo()});
			Utils.getIndexFromIndexNum(oldMod[1]).dropStud(studentID);
			indexToAdd.appendToStuList(studentID);
			System.out.printf("successfully changed course %s, index %s with course %s, index %s\n", oldMod[0], oldMod[1], oldMod[0], indexToAdd.getIndexNo());
		}
		return 1;
	}
	
	public void printModules(){
		System.out.println("Current enrolled modules and respective indices are: ");
		int counter = 1;
		for (String[] mod: modules){ // mod is {course, index}
			System.out.printf("(%d) course code: %s, index number: %s\n", counter, mod[0], mod[1]);
			counter++;
		}
	}

	// helper functions -------------------------------------------------
	public void removeMyModuleByIdxNum(String indexNum) {
		int counter = -1;
		for (String[] mod : modules) {
			counter++;
			if (mod[1].equals(indexNum)){
				modules.remove(counter);
				break;
			}
		}
	}
	public boolean checkAccessTime() { //update wrt School class
		Calendar fixedStart = getStartTime();
		Calendar fixedEnd = getEndTime();
		Calendar now = Calendar.getInstance();
		Date x = now.getTime();
		//System.out.print("-----------------------------------");
		//System.out.print("\nstart:\t" + fixedStart.getTime());
		//System.out.print("\nend:\t" + fixedEnd.getTime());
		//System.out.println("\nnow:\t" + x);
		//System.out.println("---------------------------------");

		if (x.after(fixedStart.getTime()) && x.before(fixedEnd.getTime())) {
			System.out.println("login within time! welcome");
			return true;
		}
		System.out.println("Not within access period");
		return false;
	}

	public boolean isOverlappingSchedule(Index newIndex) throws Exception{
		//		Index index = read.retrieve(newIndex);
		for (String[] mod: modules) {
			Index i1 = Utils.getIndexFromIndexNum(mod[1]);

			for (Lesson lesson: i1.getLesson()) {

				Object[] lessonDetails = lesson.convertTime();

				Calendar startTime = (Calendar) lessonDetails[1];
				Calendar endTime = (Calendar) lessonDetails[2];

				for(Lesson newLesson: newIndex.getLesson()) {

					Object[] newLessonDetails = newLesson.convertTime();

					Calendar newStartTime = (Calendar) newLessonDetails[1];
					Calendar newEndTime = (Calendar) newLessonDetails[2];

					Date newStartTime1 = newStartTime.getTime();
					Date newEndTime1 = newEndTime.getTime();
					if (lessonDetails[0] == newLessonDetails[0]) {
						if (!newStartTime1.after(endTime.getTime()) && !newEndTime1.before(startTime.getTime())) {
							System.out.printf("\nnew index: %s,\t", newIndex.getIndexNo());
							System.out.print(newStartTime.getTime());
							System.out.print('\t');
							System.out.print(newEndTime.getTime());
							System.out.printf("\nold index: %s,\t", i1.getIndexNo());
							System.out.print(startTime.getTime());
							System.out.print('\t');
							System.out.print(endTime.getTime());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean promptToJoinWaitList(Index indexToAdd)throws Exception{
		if (indexToAdd.getVacancy() == 0) {
			System.out.println("No vacancies! Add to waitlist? Y/N");
			char waitlistChoice = sc.next().charAt(0);
			boolean quit = false;

			while (!quit){
				switch (waitlistChoice) {
					case 'Y':
					case 'y':
						indexToAdd.appendToWaitList(studentID);
						quit = true;
						break;
					case 'N':
					case 'n':
						System.out.println("Not adding to waitlist.");
						quit = true;
						break;
					default:
						System.out.println("Invalid choice! Enter Y/N");
						waitlistChoice = sc.next().charAt(0);
						break;
				}
			}
			return true;
		}
		return false;
	}

	// getters -------------------------------------------------------
	public String getStudentID() {	return studentID; }
	public String getPasswordHash() { return passwordHash; }
	public String getName() { return name; }
	public String getMatricNum() { return matricNum; }
	public String getNationality() { return nationality; }
	public char getGender() { return gender; }
	public String getSchoolName() { return schoolName; }
	public int getAcadUnit() throws Exception{
		acadUnit = 0;
		for (String[] mod: modules){
			Index index = Utils.getIndexFromIndexNum(mod[1]);
			acadUnit += index.getAcadUnit();
		}
		return acadUnit;
	}
	public Index getIndexFromCourseCode(String courseCode) throws Exception{
		for (String[] mod: modules){
			if (mod[0].equals(courseCode)){
				return Utils.getIndexFromIndexNum(mod[1]);
			}
		}
		System.out.println("\nstudent.getIndexFromCourseCode(String courseCode):\n\tindex with that courseCode cannot be found in student");
		return null;
	}
	public ArrayList<String[]> getModules() {
		return this.modules;
	}
	public Calendar getStartTime() {
		String[] startList = startTime.split(":");
		String[] dateList = date.split("/");
		Calendar start = Calendar.getInstance();
		start.set(Integer.parseInt(dateList[2]), (Integer.parseInt(dateList[1]))-1, Integer.parseInt(dateList[0]), Integer.parseInt(startList[0]), Integer.parseInt(startList[1]));
		return start;
	}
	public Calendar getEndTime() {
		String[] endList = endTime.split(":");
		String[] dateList = date.split("/");
		Calendar end = Calendar.getInstance();
		end.set(Integer.parseInt(dateList[2]), (Integer.parseInt(dateList[1]))-1, Integer.parseInt(dateList[0]), Integer.parseInt(endList[0]), Integer.parseInt(endList[1]));
		return end;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	//	 setters ------------------------------------------------
	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
