package project2.starsApp;

import java.util.*;

/**
* <h2>Entity: Logic for Index</h2>
* Inherited from Course class 
* 	Contains attributes for index object such as the student list, 
*	waitlist and lesson schedules 
*	Contains methods that allow students to be added or dropped from 
*	index and waitlist 
*
* @author  Chua Zi Heng, Pooja Nag
* @version 1.0
* @since   2020-11-20
*/

public class Index extends Course {
	final static long serialVersionUID = 123;
	private int VACANCY;
	private String indexNo; // note this is unique across all courses
	private ArrayList<String> stuList = new ArrayList<String>();
	private ArrayList<String> waitList = new ArrayList<String>();
	private ArrayList<Lesson> lessons = new ArrayList<Lesson>();
 
	/** Index constructors */
	public Index() {}
	public Index(String courseCode, String school, int acadUnit, String indexNo, ArrayList<Lesson> lessons,int VACANCY) {
		this.courseCode = courseCode;
		this.school = school;
		this.acadUnit = acadUnit;
		this.indexNo = indexNo;
		this.lessons = lessons;
		this.VACANCY = VACANCY;
	}

	/** method to add student to student list of index */
	public void appendToStuList(String studentID) {
		stuList.add(studentID);
		VACANCY--;
	}

	/** method to add student to waitlist of index */
	public void appendToWaitList(String studentID) {
		waitList.add(studentID);
	}

	/** method to drop student from index */
	public void dropStud(String studentID) {
		stuList.remove(studentID);
		VACANCY++;
	}

	/** method to print student's name, gender and nationality 
	 *  if they are registered for this index */
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

	/** method to add first student on waitlist if vacancy for the index is more than 0 */
	public void popWaitListedStud() {
		if (VACANCY>0 && waitList.size()>0) {
			String sid = waitList.get(0);
			Student cindy = DataBase.getStudentFromStuID(sid);
			stuList.add(sid);
			waitList.remove(0);
			cindy.doAddModule(this);
			SendEmail.SendSchEmail(getIndexNo(),getCourseCode(),cindy.getName());
		}
	}

	// setters and getters // 
	public ArrayList<Lesson> getLesson (){
		return lessons;
	}
	public String getIndexNo() {
		return indexNo;
	}
	
	public int getVacancy() {
		return VACANCY;
	}

	public String getCourseCode() {
		return courseCode;
	}

	//unused setters and getters // 
	public ArrayList<String> getStudList(){
		return stuList;
	}

	public ArrayList<String> getWaitList(){
		return waitList;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
}
