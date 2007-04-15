package cz.cvut.felk.timejuggler.db;

import java.sql.Timestamp;

/**
 * @version 0.1
 * @created 14-IV-2007 21:47:56
 */
public class VToDo extends CalComponent {

	private Timestamp due;
	private String geo="";
	private String location="";
	private int priority=0;
	private int percentcomplete=0;
	private Timestamp completed;
	//public Alarms m_Alarms;
	//public Categories m_Categories;

	public VToDo(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public Timestamp getdue(){
		return due;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setdue(Timestamp newVal){
		due = newVal;
	}

	public String getgeo(){
		return geo;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setgeo(String newVal){
		geo = newVal;
	}

	public String getlocation(){
		return location;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setlocation(String newVal){
		location = newVal;
	}

	public int getpriority(){
		return priority;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setpriority(int newVal){
		priority = newVal;
	}

	public int getpercentcomplete(){
		return percentcomplete;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setpercentcomplete(int newVal){
		percentcomplete = newVal;
	}

	public Timestamp getcompleted(){
		return completed;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setcompleted(Timestamp newVal){
		completed = newVal;
	}

}