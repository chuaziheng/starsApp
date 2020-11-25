package project2.starsApp;

import java.util.ArrayList;
/**
* <h2>Control: Admin Interface for Course related methods</h2>
* Interface containing the abstract methods of the admin's functions 
* that are related to managing the courses. 
* @author  Mun Kei Wuai
* @version 2.2
* @since   2020-11-23
*/

public interface AdminCourseManagement{
	public abstract void addIndex() throws Exception;
	public abstract ArrayList<Lesson> addLesson();
	public abstract void addModule();
	public abstract void deleteIndexOrCourse(boolean byIndex) throws Exception;
	public abstract void printCourseIndexList();
	public abstract void printStuList(String byWhat) throws Exception;
}
