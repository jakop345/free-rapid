package cz.cvut.felk.timejuggler.db;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:40:48
 * Hotovo
 */
public class Category extends DbElement{
	//TODO : Logging
	private String name;
	
	private int componentId;
	
	public Category(){

	}
	
	public Category(String name){
		this.name = name;
	}
	
    public void store() {

    }
	 
	 /**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
		if (getId() > 0) {
			Object params[] = { name, componentId, getId() };
			String updateQuery = "UPDATE Category SET name=?,calComponentID=? WHERE categoryID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
	        Object params[] = { name, componentId  };
	        String insertQuery = "INSERT INTO Category (name,calComponentID) VALUES (?,?) ";
	        template.executeUpdate(insertQuery, params);
	        setId(template.getGeneratedId());
		}
	}
	 /**
     * Method delete
     * @param template
     */
	public void delete(TimeJugglerJDBCTemplate template) {
		if (getId() > 0) {
			Object params[] = {	getId() };		
			String deleteQuery = "DELETE FROM Category WHERE categoryID = ? ";
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}

	public String getName(){
		return name;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setName(String newVal){
		name = newVal;
	}

	
	public void setComponentId(int componentId) {
		this.componentId = componentId; 
	}

	public int getComponentId() {
		return (this.componentId); 
	}

}