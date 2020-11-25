package project2.starsApp;

/**
* <h2>Control: Admin Interface for Student related methods</h2>
* Interface containing the abstract methods of the admin's functions 
* that are related to managing the students.
 * @author  Chua Zi Heng
 * @version 2.2
 * @since   2020-11-23
 * */
public interface AdminStudentManagement{
	public abstract void addStudent();
	public abstract void deleteStudent() throws Exception;
	public abstract void studentAccessPeriod(Student s);
}
