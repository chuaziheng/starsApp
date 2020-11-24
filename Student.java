// package project2;

import java.util.*;

import java.io.Serializable;

public class Student extends Account implements Serializable {
	final static long serialVersionUID = 123;
	// private String getUsername();
	// private String passwordHash;
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
		super(studentID,passwordHash);
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
		super(studentID,passwordHash);
		this.name = name;
		this.matricNum = matricNum;
		this.nationality = nationality;
		this.gender = gender;
		this.schoolName = schoolName;
		this.modules = new ArrayList <String[]>();
	}

	// full constructor
	public Student(String studentID, String passwordHash, String name, String matricNum, String nationality, char gender, String schoolName, ArrayList<String[]> modules, String startTime, String endTime, String date) {
		super(studentID,passwordHash);
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
		if (s.getUsername().equals(getUsername())) return true;
		return false;
	}

	// case 1 --------------------------------------------------------------------------------------------------------
	public void askAddModule(){
		try{
			String courseCode;
			ArrayList<String> indexNums;

			ErrorHandling.checkAcadUnit(this);

			System.out.println("\nEnter course code to add: ");
			courseCode = sc.next();
			courseCode = courseCode.toUpperCase();

			ErrorHandling.checkIfStudentHasExistingCourse(courseCode,this.getModules());

			indexNums = Utils.getIndexNumsFromCourseCode(courseCode);

			ErrorHandling.checkAcadUnit(this, Utils.getIndexFromIndexNum(indexNums.get(0)));

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
			boolean result;
			do {
				System.out.println("Enter option (1/2/...): ");
				idxChoice = sc.nextInt();
				idxChoice--;
				sc.nextLine();

				result = ErrorHandling.isReasonableChoice(indexNums.size(), idxChoice);
			} while (!result);

			// get the respective index object
			Index indexToAdd = Utils.getIndexFromIndexNum(indexNums.get(idxChoice));

			ErrorHandling.checkStuExistingMod(this.modules, indexToAdd);

			isOverlappingSchedule(indexToAdd);

			if (promptToJoinWaitList(indexToAdd)) {} // true means no vacancy
			else { // success so add module
				doAddModule(indexToAdd);
				System.out.printf("\nCourse %s, index %s is successfuly added!\n", indexToAdd.getCourseCode(), indexToAdd.getIndexNo());
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
			return;
		}
	}
	public void doAddModule(Index indexToAdd){
		String[] mod = {indexToAdd.getCourseCode(), indexToAdd.getIndexNo()};
		// add to this student which is from DB
		this.modules.add(mod);
		// reference from courseList in Utils
		indexToAdd.appendToStuList(getUsername());
	}
	// case 1 --------------------------------------------------------------------------------------------------------

	// case 2 --------------------------------------------------------------------------------------------------------
	public String[] askDropModule(boolean drop) throws Exception{
		try{
			ErrorHandling.isEmpty(this.modules);

			int removeChoice;
			boolean result;
			String[] mod = new String[2];
			printModules();

			do{
				System.out.println("Enter choice of course code to drop(1/2/...): ");
				removeChoice = sc.nextInt();
				sc.nextLine();
				removeChoice--;

				result = ErrorHandling.isReasonableChoice(this.modules.size(), removeChoice);

			} while(!result);

			if (drop) {
				mod = doDropModule(removeChoice);
				System.out.printf("\nCourse %s, index %s successfully dropped\n", mod[0], mod[1]);
			}
			else mod = modules.get(removeChoice);
			return mod;
		} catch (Exception e){
			sc.nextLine();
			System.out.println("Please enter a number!");
			return new String[]{"", ""};
		}
	}
	public String[] doDropModule(int removeChoice) throws Exception{
		String[] mod = new String[2];
		mod = modules.remove(removeChoice);

		//Remove student from Index (Remove from courseList DB)
		Utils.getIndexFromIndexNum(mod[1]).dropStud(getUsername());
		System.out.println("HERE!");
		Utils.getIndexFromIndexNum(mod[1]).popWaitListedStud();
		return mod;
	}
	// case 2 --------------------------------------------------------------------------------------------------------

	// case 3 ---------------------------------------------------------------------------------------------------------
	public void swapIndex(){
		try{
			ErrorHandling.isEmpty(this.modules);

			String[] mod = askDropModule(false);

			if (mod==new String[]{"", ""}) throw new Exception();

			System.out.println("Enter username of student to swap index with: ");
			String username = sc.next();
			username.toUpperCase();
			Student s = Utils.getStudentFromStuID(username);
			
			// get my details
			String myCourseCode = mod[0];
			String myIndexNum = mod[1];
			Index myIndex = getIndexFromCourseCode(myCourseCode);

			// myCourseCode is shared between me and s
			// if cannot find, go back to App
			Index sIndex = s.getIndexFromCourseCode(myCourseCode);
			ErrorHandling.sameIndexCannotSwap(myIndex, sIndex);

			// swap the Index in DB
			myIndex.dropStud(getUsername());
			sIndex.dropStud(s.getUsername());
			myIndex.appendToStuList(s.getUsername());
			sIndex.appendToStuList(getUsername());

			//editing my modules
			modules.remove(mod);
			mod[1] = sIndex.getIndexNo();
			modules.add(mod);

			// Remove old module pair from s module arrayList
			s.removeMyModuleByIdxNum(sIndex.getIndexNo());
			s.getModules().add(new String[]{myCourseCode, myIndexNum});

			System.out.printf("successfully swapped course %s, index %s with %s course %s, index %s\n", myCourseCode, myIndexNum, s.getUsername(), myCourseCode, sIndex.getIndexNo());
		} catch (Exception e){
			return;
		}
	}
	// case 3 ---------------------------------------------------------------------------------------------------------

	// case 4 --------------------------------------------------------------------------------------------------------
	public void changeIndex() {
		try{
			ErrorHandling.isEmpty(this.modules);

			int removeChoice;
			String courseCode;
			printModules();

			// get choice of course to change index
			System.out.println("Enter choice of course code to change index(1/2/...): ");
			removeChoice = sc.nextInt();
			sc.nextLine();
			removeChoice--;

			ErrorHandling.isReasonableChoice(this.modules.size(), removeChoice);

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
			sc.nextLine();

			ErrorHandling.isReasonableChoice(indexNums.size(), idxChoice);

			// get the respective index object
			Index indexToAdd = Utils.getIndexFromIndexNum(indexNums.get(idxChoice));

			ErrorHandling.checkStuExistingMod(this.modules, indexToAdd);

			this.isOverlappingSchedule(indexToAdd);

			if (promptToJoinWaitList(indexToAdd)) {}

			else { // success so add module
				modules.remove(oldMod);
				modules.add(new String[]{oldMod[0], indexToAdd.getIndexNo()});
				Utils.getIndexFromIndexNum(oldMod[1]).dropStud(getUsername());
				indexToAdd.appendToStuList(getUsername());
				System.out.printf("successfully changed course %s, index %s with course %s, index %s\n", oldMod[0], oldMod[1], oldMod[0], indexToAdd.getIndexNo());
			}
		} catch(Exception e){
			return;
		}
	}
	// case 4 --------------------------------------------------------------------------------------------------------

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
			System.out.println("\nWelcome, " + this.getUsername() + "!");
			return true;
		}
		System.out.println("Not within access period!");
		System.out.println("Please log in during your access period!");
		System.out.println("Your access period is " + getStartTime().getTime() + " to " + getEndTime().getTime());
		System.out.println();
		return false;
	}

	public void isOverlappingSchedule(Index newIndex) throws Exception{
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
							throw new Exception("\nOverlapping Schedule!");
						}
					}
				}
			}
		}
	}

	public boolean promptToJoinWaitList(Index indexToAdd){
		if (indexToAdd.getVacancy() == 0) {
			System.out.println("No vacancies! Add to waitlist? Y/N");
			char waitlistChoice = sc.next().charAt(0);
			boolean quit = false;

			while (!quit){
				switch (waitlistChoice) {
					case 'Y':
					case 'y':
						indexToAdd.appendToWaitList(getUsername());
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
	public void printTimetable() throws Exception {
		try{
			ErrorHandling.isEmpty(this.modules);
			for (String[] m: getModules()) {
				System.out.println("--------------------------TIMETABLE--------------------------"); 
				System.out.printf("%-8s %-8s %-8s %-8s %-10s", "COURSE", "INDEX", "CLASS", "DAY", "TIME");
				System.out.println(); 
				   
				
				for (Lesson l: Utils.getIndexFromIndexNum(m[1]).getLesson()) {
				  String timing = l.getStartTime() + " - "  + l.getEndTime();
				  System.out.format("%-8s %-8s %-8s %-8s %-10s", m[0], m[1], l.getClassType(), l.getDay(), timing); 
				  System.out.println(); 
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
	   }

	// getters -------------------------------------------------------
	public String getName() { return name; }
	public String getMatricNum() { return matricNum; }
	public String getNationality() { return nationality; }
	public char getGender() { return gender; }
	public String getSchoolName() { return schoolName; }
	public int getAcadUnit() throws Exception {
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
		throw new Exception("\nStudent has no index in course " + courseCode);
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

	//	 setters ------------------------------------------------
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public void setDate(String date) {
		this.date = date;
	}
}