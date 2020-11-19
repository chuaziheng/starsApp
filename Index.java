import java.util.*;

public class Index extends Course { // Serializable inherited from Course
	final static long serialVersionUID = 123;
	private int VACANCY;
	private String indexNo; // note this is unique across all courses
	private ArrayList<String> stuList = new ArrayList<String>();
	private ArrayList<String> waitList = new ArrayList<String>();
	private ArrayList<Lesson> lessons = new ArrayList<Lesson>();

	// constructors ---------------------------
	public Index() {}
	public Index(String courseCode, String school, int acadUnit, String indexNo, ArrayList<Lesson> lessons,int VACANCY) {
		// inherited
		this.courseCode = courseCode;
		this.school = school;
		this.acadUnit = acadUnit;
		// self
		this.indexNo = indexNo;
		this.lessons = lessons;
		this.VACANCY = VACANCY;
	}
	// functions ------------------------------
	public void appendToStuList(String studentID) {
		stuList.add(studentID);
		// System.out.println("vacancy before appendToStuList " + VACANCY);
		if (VACANCY == 0) System.out.println("appendToStuList trying to make VACANCY == -1");
		VACANCY--;
		// System.out.println("vacancy after appendToStuList " + VACANCY);
	}
	public void appendToWaitList(String studentID) {
		waitList.add(studentID);
		// System.out.printf("%s added to waitlist in index %s\n", studentID, indexNo);
	}
	public void dropStud(String studentID) {
		stuList.remove(studentID);
		// System.out.printf("vacancy before dropStud: %d, adding... \n", VACANCY);
		VACANCY++;
		// System.out.printf("vacancy after dropStud: %d, adding... \n", VACANCY);
	}
	public void printStuListInfo() throws Exception {
		System.out.println("Printing Student List in Index " + indexNo);
		System.out.println("-----------------------------------------------------------------------------");
	    System.out.printf("%-15s %-8s %-15s", "NAME", "GENDER", "NATIONALITY");
	    System.out.println();
	    System.out.println("-----------------------------------------------------------------------------");
		for (String sid: stuList){
			Student stud = Utils.getStudentFromStuID(sid);
			System.out.printf("%-15s %-8c %-15s", stud.getName() , stud.getGender() , stud.getNationality() );
			System.out.println();
		}
	}
	public void popWaitListedStud() {
		//add student to index if there is a vacancy and waitlist > 0
		if (VACANCY>0 && waitList.size()>0) {
			String sid = waitList.get(0);
			Student cindy = Utils.getStudentFromStuID(sid);
			stuList.add(sid);
			waitList.remove(0);
			// System.out.printf("vacancy before popWaitListedStud: %d, adding... \n", VACANCY);
			// System.out.printf("vacancy after popWaitListedStud: %d, adding... \n", VACANCY);
			cindy.doAddModule(this);
			System.out.printf("Student added to index %s successfully\n", this.indexNo);
			//SendMailTLS.sendEmail(String.format("You have been added to index %s of course %s successfully\n", this.indexNo, this.courseCode));
		}
	}
	//getters and setters//
	public ArrayList<String> getWaitList(){
		return waitList;
	}
	public ArrayList<String> getStudList(){
		return stuList;
	}
	public ArrayList<Lesson> getLesson (){
		return lessons;
	}
	public String getIndexNo() {
		return indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public int getVacancy() {
		return VACANCY;
	}
	public void vacancyMinusMinus() {
		this.VACANCY--;
	}
	public String getCourseCode() {
		return courseCode;
	}
}
