
package project2.starsApp;
import java.util.*;

public class Index extends Course {
	final static long serialVersionUID = 123;
	private int VACANCY;
	private String indexNo; // note this is unique across all courses
	private ArrayList<String> stuList = new ArrayList<String>();
	private ArrayList<String> waitList = new ArrayList<String>();
	private ArrayList<Lesson> lessons = new ArrayList<Lesson>();

	public Index() {}
	public Index(String courseCode, String school, int acadUnit, String indexNo, ArrayList<Lesson> lessons,int VACANCY) {
		this.courseCode = courseCode;
		this.school = school;
		this.acadUnit = acadUnit;
		this.indexNo = indexNo;
		this.lessons = lessons;
		this.VACANCY = VACANCY;
	}
	public void appendToStuList(String studentID) {
		stuList.add(studentID);
		if (VACANCY == 0) System.out.println("appendToStuList trying to make VACANCY == -1");
		VACANCY--;
	}
	public void appendToWaitList(String studentID) {
		waitList.add(studentID);
	}
	public void dropStud(String studentID) {
		stuList.remove(studentID);
		VACANCY++;
	}
	public void printStuListInfo() throws Exception {
		System.out.println("Printing Student List in Index " + indexNo);
		System.out.println("-----------------------------------------------------------------------------");
	    System.out.printf("%-15s %-8s %-15s", "NAME", "GENDER", "NATIONALITY");
	    System.out.println();
	    System.out.println("-----------------------------------------------------------------------------");
		for (String sid: stuList){
			Student stud = DataBase.getStudentFromStuID(sid);
			System.out.printf("%-15s %-8c %-15s", stud.getName() , stud.getGender() , stud.getNationality() );
			System.out.println();
		}
	}
	public void popWaitListedStud() {
		if (VACANCY>0 && waitList.size()>0) {
			String sid = waitList.get(0);
			Student cindy = DataBase.getStudentFromStuID(sid);
			stuList.add(sid);
			waitList.remove(0);
			cindy.doAddModule(this);
			System.out.printf("Student added to index %s successfully\n", this.indexNo);
			SendEmail.SendSchEmail(getIndexNo(),getCourseCode(),cindy.getName());
		}
	}
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
