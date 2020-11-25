/**
 * Interface for Admin that manages Student related methods
 * @author  Zi Heng
 * @version 1.0
 * @since   2020-11-20
 * */
public interface AdminStudentManagement{
	public abstract void addStudent();
	public abstract void deleteStudent() throws Exception;
	public abstract void studentAccessPeriod(Student s);
}
