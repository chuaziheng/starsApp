package project2.starsApp;


import java.io.Serializable;
import java.util.Calendar;

public class Lesson implements Serializable {
	final static long serialVersionUID = 123;
	private String location;
	private String startTime; // HHmm
	private String endTime; // HHmm
	private String day; // MON TUE WED THU FRI SAT SUN
	private String classType; // LEC, TUT, SEM, LAB

	public Lesson(String location, String startTime, String endTime, String day, String classType) {
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
		this.classType = classType;
	}
	public Object[] convertTime() {
		String[] startList = getStartTime().split(":");
		String[] endList = getEndTime().split(":");
		String date = "01/01/2000";
		String[] dateList = date.split("/");
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(Integer.parseInt(dateList[2]), Integer.parseInt(dateList[1]), Integer.parseInt(dateList[0]), Integer.parseInt(startList[0]), Integer.parseInt(startList[1]));
		end.set(Integer.parseInt(dateList[2]), Integer.parseInt(dateList[1]), Integer.parseInt(dateList[0]), Integer.parseInt(endList[0]), Integer.parseInt(endList[1]));

		Object[] details = new Object[] {day, start, end};
		return details;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}
}
