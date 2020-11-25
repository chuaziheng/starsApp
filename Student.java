import java.util.*;
import java.io.Serializable;
import java.io.Console;

/**
 * <h2>Logic for studentApp</h2>
 * Contains methods for the various student-based implementations
 * such as adding, dropping and swapping modules.
 * <p>
 * 
 * @author  Pooja Nag, Goh Nicholas
 * @version 1.0
 * @since   2020-11-20
 */

public class Student extends User implements Serializable {
	final static long serialVersionUID = 123;
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
	/** 
	 * Empty constructor.
	 * @param
	 */
	public Student(){}
	/** 
	 * Full constructor with access time
	 */
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
	//public Student(String studentID, String passwordHash, String name, String matricNum, String nationality, char gender, String schoolName) {
	//super(studentID,passwordHash);
	//this.name = name;
	//this.matricNum = matricNum;
	//this.nationality = nationality;
	//this.gender = gender;
	//this.schoolName = schoolName;
	//this.modules = new ArrayList <String[]>();
	//}
	/**
	 * constructor for generating of .dat files
	 */
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
	/**
	 *Compares the student username string with that of the current student object.
	 *@param s	Student object to check
	 *@return 	<code>true</code> if they are the same, otherwise false
	 */
	public boolean equals(Student s){
		if (s.getUsername().equals(getUsername())) return true;
		return false;
	}

	// case 1 --------------------------------------------------------------------------------------------------------
	/** 
	 *Conducts all the checks and considerations before actually adding the module. 
	 *Prompts user to input the desired course code ,and subsequently list out the currently available indices and their respective vacancies.
	 *Then, this method retrieves the chosen Index object from the database, and considers the various exceptions and scenarios like 
	 *overlapping of timetable schedule, adding of existing index etc. If index has no vacancy, student is added into the waitlist.
	 *Otherwise, student is able to successfully add module.
	 *
	 */
	public void askAddModule(){
		try{
			String courseCode;
			ArrayList<String> indexNums;

			ErrorHandling.checkAcadUnit(this);

			System.out.println("\nEnter course code to add: ");
			courseCode = sc.nextLine();
			courseCode = courseCode.toUpperCase();

			ErrorHandling.checkStuExistingCourse(this.getModules(), courseCode);

			indexNums = DataBase.getIndexNumsFromCourseCode(courseCode);
			if (!ErrorHandling.checkExistingCourse(courseCode, true)) throw new Exception("Please enter a correct coursecode!");

			ErrorHandling.checkAcadUnit(this, DataBase.getIndexFromIndexNum(indexNums.get(0)));

			for (String indexNum : indexNums) {
				Index idx = DataBase.getIndexFromIndexNum(indexNum);
				for (String sid : idx.getWaitList()) {
					if (sid.equals(this.accountID)){
						throw new Exception("You are already waitlisted for " + idx.getCourseCode() + ", " + idx.getIndexNo());
					}
				}
			}

			System.out.printf("Avaliable indices for course code %s are: \n", courseCode);

			int counter = 1;
			for (String indexNum: indexNums){ // Prints list of indices and vacancies
				Index index = DataBase.getIndexFromIndexNum(indexNum);
				System.out.printf("(%d) index number: %s vacancy: %d\n", counter, index.getIndexNo(), index.getVacancy());
				counter++;
			}
			int idxChoice;
			boolean result;
			do {
				System.out.println("Enter option (1/2/...): ");
				String temp = sc.nextLine();
				idxChoice = ErrorHandling.convertToInt(temp);
				idxChoice--;

				result = ErrorHandling.isReasonableChoice(indexNums.size(), idxChoice);
			} while (!result);

			Index indexToAdd = DataBase.getIndexFromIndexNum(indexNums.get(idxChoice));

			ErrorHandling.checkStuExistingIndex(this.modules, indexToAdd);

			isOverlappingSchedule(indexToAdd);

			if (promptToJoinWaitList(indexToAdd)) {}
			else {
				doAddModule(indexToAdd);
				System.out.printf("\nCourse %s, index %s is successfuly added!\n", indexToAdd.getCourseCode(), indexToAdd.getIndexNo());
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
			return;
		}
	}
	/** 
	 *Executes the adding of module. Adds the new module to the student's module list,
	 *and at the same time, adds student into the respective Index's student list.
	 *@param indexToAdd		Desired index to be added
	 */
	public void doAddModule(Index indexToAdd){
		String[] mod = {indexToAdd.getCourseCode(), indexToAdd.getIndexNo()};
		this.modules.add(mod);
		indexToAdd.appendToStuList(getUsername());
	}
	// case 1 --------------------------------------------------------------------------------------------------------

	// case 2 --------------------------------------------------------------------------------------------------------
	/** 
	 *Conducts all the checks and considerations before dropping the module. 
	 *Prints all existing courses that student is taking, prompts user to input choice.
	 *For the dropping of module, the implementation method doDropModule() is called.
	 *Otherwise, the desired module is not dropped but returned.
	 *
	 *<b>Note:</b> 
	 *This method fulfills 2 actions: (1) Drop module and a part of (2) Swap module
	 *
	 *@param drop	<code>true</code> to drop module, <code>false</code> to swap module
	 *@return mod	Array of Strings that contain the module's courseCode and index
	 *@exception	e	Prompts user for valid integer input when choosing desired index
	 */
	public String[] askDropModule(boolean drop) throws Exception{
		try{
			ErrorHandling.isEmpty(this.modules);

			int removeChoice;
			boolean result;
			String[] mod = new String[2];
			printModules();

			do{
				System.out.println("Enter choice of course code to drop(1/2/...): ");
				String temp = sc.nextLine();
				removeChoice = ErrorHandling.convertToInt(temp);
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
			return new String[]{"", ""};
		}
	}
	/** 
	 *Executes the dropping of module. Removes the dropped module from the student's module list,
	 *and at the same time, removes student from the respective Index's student list.
	 *If there is a non-empty waitlist for that index, the first element will be popped and added into the index.
	 *@param removeChoice		Integer that represents the array index of the student's module list of the module to be dropped
	 */
	public String[] doDropModule(int removeChoice) throws Exception{
		String[] mod = new String[2];
		mod = modules.remove(removeChoice);

		DataBase.getIndexFromIndexNum(mod[1]).dropStud(getUsername());
		DataBase.getIndexFromIndexNum(mod[1]).popWaitListedStud();
		return mod;
	}
	// case 2 --------------------------------------------------------------------------------------------------------

	// case 3 ---------------------------------------------------------------------------------------------------------
	/** 
	 *Executes the swapping of index with another student. Prompts user to choose which module
	 *to be dropped, and the username of the other student.
	 *Performs swapping of students in the respective indices student lists.
	 */
	public void swapIndex(){
		try{
			ErrorHandling.isEmpty(this.modules);

			String[] mod = askDropModule(false);

			if (mod==new String[]{"", ""}) throw new Exception();

			System.out.println("Enter username of student to swap index with: ");
			String username = sc.nextLine();
			username = username.toLowerCase();
			Student s = DataBase.getStudentFromStuID(username);
			Console cons = System.console();
			char[] studentPassword = cons.readPassword("Enter " + s.getName() + "'s" + " password: ");
			String studentPasswordStr = String.valueOf(studentPassword);
			String studentPasswordHash = PasswordHashController.hash(studentPasswordStr); 
			boolean match = PasswordHashController.checkUsernameAndPassword(s.getUsername(),"student", studentPasswordHash);
			if (match) {
				String myCourseCode = mod[0];
				String myIndexNum = mod[1];
				Index myIndex = getIndexFromCourseCode(myCourseCode);

				Index sIndex = s.getIndexFromCourseCode(myCourseCode);
				ErrorHandling.sameIndexCannotSwap(myIndex, sIndex);

				myIndex.dropStud(getUsername());
				sIndex.dropStud(s.getUsername());
				myIndex.appendToStuList(s.getUsername());
				sIndex.appendToStuList(getUsername());

				modules.remove(mod);
				mod[1] = sIndex.getIndexNo();
				modules.add(mod);

				s.removeMyModuleByIdxNum(sIndex.getIndexNo());
				s.getModules().add(new String[]{myCourseCode, myIndexNum});

				System.out.printf("successfully swapped course %s, index %s with %s course %s, index %s\n", myCourseCode, myIndexNum, s.getUsername(), myCourseCode, sIndex.getIndexNo());
			} else {
				System.out.println("Wrong password!");
			}

		} catch (Exception e){
			return;
		}
	}
	// case 3 ---------------------------------------------------------------------------------------------------------

	// case 4 --------------------------------------------------------------------------------------------------------
	/** 
	 *Executes the changing of index with another index of the same module. 
	 *Prints all existing courses that student is taking, prompts user to input choice.
	 *Performs error handling to ensure that it is a valid change, and updates the student lists
	 *of the relevant indices.
	 *
	 */
	public void changeIndex() {
		try{
			ErrorHandling.isEmpty(this.modules);

			int removeChoice;
			String courseCode;
			printModules();

			System.out.println("Enter choice of course code to change index(1/2/...): ");
			String temp = sc.nextLine();
			removeChoice = ErrorHandling.convertToInt(temp);
			removeChoice--;

			ErrorHandling.isReasonableChoice(this.modules.size(), removeChoice);

			String[] oldMod = modules.get(removeChoice);
			courseCode = oldMod[0];

			System.out.printf("Avaliable indices for course code %s are: \n", courseCode);

			int count = 1;
			ArrayList<String> indexNums = DataBase.getIndexNumsFromCourseCode(courseCode);
			for (String indexNum: indexNums){
				Index index = DataBase.getIndexFromIndexNum(indexNum);
				System.out.printf("(%d) index number: %s vacancy: %d\n", count, index.getIndexNo(), index.getVacancy());
				count++;
			}

			int idxChoice;
			System.out.println("Enter choice of index number to change to(1/2/...): ");
			temp = sc.nextLine();
			idxChoice = ErrorHandling.convertToInt(temp);
			idxChoice--;

			ErrorHandling.isReasonableChoice(indexNums.size(), idxChoice);

			Index indexToAdd = DataBase.getIndexFromIndexNum(indexNums.get(idxChoice));

			this.isOverlappingSchedule(indexToAdd);

			if (promptToJoinWaitList(indexToAdd)) {}

			else {
				modules.remove(oldMod);
				modules.add(new String[]{oldMod[0], indexToAdd.getIndexNo()});
				DataBase.getIndexFromIndexNum(oldMod[1]).dropStud(getUsername());
				indexToAdd.appendToStuList(getUsername());
				System.out.printf("successfully changed course %s, index %s with course %s, index %s\n", oldMod[0], oldMod[1], oldMod[0], indexToAdd.getIndexNo());
			}
		} catch(Exception e){
			return;
		}
	}
	// case 4 --------------------------------------------------------------------------------------------------------
	/** 
	 *Prints all the course codes and indices taken by the student in a table format.
	 */
	public void printModules(){
		System.out.println("Current enrolled modules and respective indices are: ");
		int counter = 1;
		for (String[] mod: modules){
			System.out.printf("(%d) course code: %s, index number: %s\n", counter, mod[0], mod[1]);
			counter++;
		}
	}
	/** 
	 *Removes module from student's module list based on the index number provided

	 *@param indexNum	index to be removed (string)
	 */
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
	/** 
	 *Checks if user is allowed access based on predetermined access times.

	 *@return	<code>true</code> if within access time, otherwise <code>false</code>.
	 */
	public boolean checkAccessTime() {
		Calendar fixedStart = getStartTime();
		Calendar fixedEnd = getEndTime();
		Calendar now = Calendar.getInstance();
		Date x = now.getTime();

		if (x.after(fixedStart.getTime()) && x.before(fixedEnd.getTime())) {
			System.out.println("\nWelcome, " + this.getName().toUpperCase() + "!");
			return true;
		}
		System.out.println("Not within access period!");
		System.out.println("Please log in during your access period!");
		System.out.println("Your access period is " + getStartTime().getTime() + " to " + getEndTime().getTime());
		System.out.println();
		return false;
	}
	/** 
	 *Checks the lesson timings of this index against the other lesson timings of existing indices

	 *@param newIndex	check if this index clashes with the existing indices in the schedule
	 *@exception			If clashes with other lesson timings, print error message.
	 */
	public void isOverlappingSchedule(Index newIndex) throws Exception{
		for (String[] mod: modules) {
			Index i1 = DataBase.getIndexFromIndexNum(mod[1]);

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
							System.out.printf("\nCourse %s to be added:\n\tindex: %s,\t", newIndex.getCourseCode(), newIndex.getIndexNo());
							System.out.print(newStartTime.getTime());
							System.out.print('\t');
							System.out.print(newEndTime.getTime());
							System.out.printf("\nbut clashes with Course %s:\n\tindex: %s,\t", i1.getCourseCode(), i1.getIndexNo());
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
	/** 
	 *Checks if index vacancy is zero and prompts user to join waitlist if there is no vacancy in the desired index.
	 *If yes is chosen, append user to waitlist.

	 *@param indexToAdd	index to be added
	 *@return 			<code>true</code> if vacancy of desired index is 0, otherwise <code>false</code>
	 */
	public boolean promptToJoinWaitList(Index indexToAdd)throws Exception{
		if (indexToAdd.getVacancy() == 0) {
			System.out.println("No vacancies! Add to waitlist? Y/N");
			char waitlistChoice = sc.nextLine().charAt(0);
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
						waitlistChoice = sc.nextLine().charAt(0);
						break;
				}
			}
			return true;
		}
		return false;
	}
	public ArrayList<String[]> checkWaitlistStatus(){
		ArrayList<String[]> stuWaitList = new ArrayList<>();
		for (Index i : DataBase.getIndexList()){
			for (String s: i.getWaitList()){
				if (accountID.equals(s)){
					String[] mod = {i.getCourseCode(), i.getIndexNo()};
					stuWaitList.add(mod);
				}
			}
		}
		return stuWaitList;
	}
	/** 
	 *Prints timetable which includes lesson details of the indices that the user is taking.
	 */
	public void printTimetable() throws Exception {
		try{
			ErrorHandling.isEmpty(this.modules);
			System.out.println("--------------------------TIMETABLE--------------------------"); 
			System.out.printf("%-8s %-8s %-8s %-8s %-10s", "COURSE", "INDEX", "CLASS", "DAY", "TIME");
			System.out.println();
			ArrayList<String[]> stuWaitList = checkWaitlistStatus();
			for (String[] m: getModules()) {  
				for (Lesson l: DataBase.getIndexFromIndexNum(m[1]).getLesson()) {
					String timing = l.getStartTime() + " - "  + l.getEndTime();
					System.out.format("%-8s %-8s %-8s %-8s %-10s", m[0], m[1], l.getClassType(), l.getDay(), timing); 
					System.out.println(); 
				}
				System.out.println(); 
			}
			if (!stuWaitList.isEmpty()){
				System.out.println("--------------------------WAITLIST--------------------------"); 
				System.out.printf("%-8s %-8s %-8s %-8s %-10s", "COURSE", "INDEX", "CLASS", "DAY", "TIME");
				for (String[] m: stuWaitList) {
					System.out.println(); 
					for (Lesson l: DataBase.getIndexFromIndexNum(m[1]).getLesson()) {
						String timing = l.getStartTime() + " - "  + l.getEndTime();
						System.out.format("%-8s %-8s %-8s %-8s %-10s", m[0], m[1], l.getClassType(), l.getDay(), timing); 
						System.out.println(); 
					}
					System.out.println(); 
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	public String getName() { return name; }
	public String getMatricNum() { return matricNum; }
	public String getNationality() { return nationality; }
	public char getGender() { return gender; }
	public String getSchoolName() { return schoolName; }
	/** 
	 * Calculates total academic units taken by the student.
	 * @return	total academic units
	 */
	public int getAcadUnit() throws Exception {
		acadUnit = 0;
		for (String[] mod: modules){
			Index index = DataBase.getIndexFromIndexNum(mod[1]);
			acadUnit += index.getAcadUnit();
		}
		return acadUnit;
	}

	/** 
	 * Returns index from its respective course code in students module list.
	 * @return	index object
	 */
	public Index getIndexFromCourseCode(String courseCode) throws Exception{
		for (String[] mod: modules){
			if (mod[0].equals(courseCode)){
				return DataBase.getIndexFromIndexNum(mod[1]);
			}
		}
		throw new Exception("\nStudent has no index in course " + courseCode);
	}
	public ArrayList<String[]> getModules() {
		return this.modules;
	}
	/** 
	 * Returns start of student's access period
	 * @return	calendar object for start time
	 */
	public Calendar getStartTime() {
		String[] startList = startTime.split(":");
		String[] dateList = date.split("/");
		Calendar start = Calendar.getInstance();
		start.set(Integer.parseInt(dateList[2]), (Integer.parseInt(dateList[1]))-1, Integer.parseInt(dateList[0]), Integer.parseInt(startList[0]), Integer.parseInt(startList[1]));
		return start;
	}
	/** 
	 * Returns end of student's access period
	 * @return	calendar object for end time
	 */
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
	public void setDate(String date) {
		this.date = date;
	}
}
