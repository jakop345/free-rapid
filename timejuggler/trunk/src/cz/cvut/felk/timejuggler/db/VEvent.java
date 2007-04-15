package cz.cvut.felk.timejuggler.db;

/**
 * @version 0.1
 * @created 14-IV-2007 17:18:35
 */
public class VEvent extends CalComponent {

	private String geo="";
	private String location="";
	private int priority=0;
	private String transp="";
	//public Categories m_Categories;
	//public Alarms m_Alarms;
	//public ComponentDetails m_ComponentDetails;
	//public CalComponent m_CalComponent;

	public VEvent(){

	}

	public void finalize() throws Throwable {
		super.finalize();
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

	public String gettransp(){
		return transp;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void settransp(String newVal){
		transp = newVal;
	}

}