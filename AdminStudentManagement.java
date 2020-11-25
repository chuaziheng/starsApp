package project2.starsApp;
public interface AdminStudentManagement{
	public abstract void addStudent();
	public abstract void deleteStudent() throws Exception;
	public abstract void studentAccessPeriod(Student s);
}
