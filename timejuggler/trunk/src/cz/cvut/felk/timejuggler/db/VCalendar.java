package cz.cvut.felk.timejuggler.db;

/**
 * @version 0.1
 * @created 14-IV-2007 16:38:21
 */
public class VCalendar extends DbElement {

	private String prodid = "-//CVUT //TimeJuggler Calendar 0.1//CZ";
	private String version = "2.0";
	private String calscale = "GREGORIAN";
	private String method = "PUBLISH";
	private String name = "";
	
//	private Vector events;

	public VCalendar(){

	}
	public VCalendar(int id){
		super(id);
	}
	public VCalendar(String name){
		this.name = name;
	}


	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getprodid(){
		return prodid;
	}

	public void store(){

	}

	/**
	 * 
	 * @param newVal
	 */
	public void setprodid(String newVal){
		prodid = newVal;
	}

	public String getversion(){
		return version;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setversion(String newVal){
		version = newVal;
	}

	public String getcalscale(){
		return calscale;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setcalscale(String newVal){
		calscale = newVal;
	}

	public String getmethod(){
		return method;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setmethod(String newVal){
		method = newVal;
	}

	public String getname(){
		return name;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setname(String newVal){
		name = newVal;
	}

}