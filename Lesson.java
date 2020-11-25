 package project2.starsApp;
import java.io.Serializable;
import java.util.Calendar;

/**
* <h2>Logic for Lesson</h2>
*  Contains attributes for Lesson object such as the location, 
		lesson timing and class type 
	Contains methods that allow students to be added or dropped from 
		index and waitlist 
* <p>
* <b>Note:</b> Giving proper comments in your program makes it more
* user friendly and it is assumed as a high quality code.
*
* @author  Tan Wen Xiu
* @version 1.0
* @since   2020-11-20
*/

public class Lesson implements Serializable {
	final static long serialVersionUID = 123;
	private String location;
	private String startTime; // HHmm
	private String endTime; // HHmm
	private String day; // MON TUE WED THU FRI SAT SUN
	private String classType; // LEC, TUT, SEM, LAB

	/** Lesson constructor */
	public Lesson(String location, String startTime, String endTime, String day, String classType) {
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
		this.classType = classType;
	}

	/** method to convert lesson time to a list */
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

	// setters and getters // 
	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getDay() {
		return day;
	}

	public String getClassType() {
		return classType;
	}

	// unused setters and getters // 
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public void setDay(String day) {
		this.day = day;
	}
	
	public void setClassType(String classType) {
		this.classType = classType;
	}
}
