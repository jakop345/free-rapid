package cz.cvut.felk.timejuggler.db;
import java.sql.*;
/**
 * @version 0.1
 * @created 14-IV-2007 17:18:43
 */
public class CalComponent extends DbElement{

	/**
	 * povinny parametr! (globalne unikatni (MAILTO://email@...))
	 */
	private String uid="";
	private String url="";
	/**
	 * klasifikace (PUBLIC/PRIVATE...)
	 */
	private String clazz="";
	/**
	 * popis komponenty
	 */
	private String description="";
	private String organizer="";
	private int sequence=0;
	private String status="";
	private String summary="";
	private Timestamp recurrenceid;
	private Timestamp dtstamp;
	/*public DateTime m_DateTime; not implemented */
	/*public VCalendar m_VCalendar; not implemented */ 

	public CalComponent(){

	}
	public CalComponent(int id){
		super(id);
	}
	public void finalize() throws Throwable {

	}

	/**
	 * povinny parametr! (globalne unikatni (MAILTO://email@...))
	 */
	public String getuid(){
		return uid;
	}

	/**
	 * povinny parametr! (globalne unikatni (MAILTO://email@...))
	 * 
	 * @param newVal
	 */
	public void setuid(String newVal){
		uid = newVal;
	}

	public String geturl(){
		return url;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void seturl(String newVal){
		url = newVal;
	}

	/**
	 * klasifikace (PUBLIC/PRIVATE...)
	 */
	public String getclazz(){
		return clazz;
	}

	/**
	 * klasifikace (PUBLIC/PRIVATE...)
	 * 
	 * @param newVal
	 */
	public void setclazz(String newVal){
		clazz = newVal;
	}

	/**
	 * popis komponenty
	 */
	public String getdescription(){
		return description;
	}

	/**
	 * popis komponenty
	 * 
	 * @param newVal
	 */
	public void setdescription(String newVal){
		description = newVal;
	}

	public String getorganizer(){
		return organizer;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setorganizer(String newVal){
		organizer = newVal;
	}

	public int getsequence(){
		return sequence;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setsequence(int newVal){
		sequence = newVal;
	}

	public String getstatus(){
		return status;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setstatus(String newVal){
		status = newVal;
	}

	public String getsummary(){
		return summary;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setsummary(String newVal){
		summary = newVal;
	}

	public Timestamp getrecurrenceid(){
		return recurrenceid;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setrecurrenceid(Timestamp newVal){
		recurrenceid = newVal;
	}

	public Timestamp getdtstamp(){
		return dtstamp;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setdtstamp(Timestamp newVal){
		dtstamp = newVal;
	}
	
	public void store(){
		
	}

}