import java.util.ArrayList;

public interface AdminCourseManagement{
	public abstract void addIndex() throws Exception;
	public abstract ArrayList<Lesson> addLesson();
	public abstract void addModule();
	public abstract void deleteIndexOrCourse(boolean byIndex) throws Exception;
	public abstract void printCourseIndexList();
	public abstract void printStuList(String byWhat) throws Exception;
}
