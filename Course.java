package project2.starsApp;
import java.io.Serializable;

public abstract class Course implements Serializable {
	final static long serialVersionUID = 123;
	protected int acadUnit;
	protected String courseCode;
	protected String school;

	protected String getCourseCode() {
		return courseCode;
	}
	protected String getSchool() {
		return school;
	}
	protected int getAcadUnit() {
		return acadUnit;
	}
	protected void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
	protected void setSchool(String school) {
		this.school = school;
	}
	protected void setAcadUnit(int acadUnit) {
		this.acadUnit = acadUnit;
	}
}
