package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.*;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 23:40:35
 * Hotovo
 */
public class Comment extends DbElement{
	//TODO : Logging
	private String comment = "";
	
	private int componentId;

	public Comment(){

	}
	
	public Comment(String comment){
		this.comment = comment;
	}
	
    public void store() {

    }
	 
	 /**
     * Method saveOrUpdate
     * @param template
     */
	public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
		if (getId() > 0) {
			Object params[] = { comment, componentId, getId() };
			String updateQuery = "UPDATE Comment SET comment=?,calComponentID=? WHERE commentID = ? ";
			template.executeUpdate(updateQuery, params);
		}else{
		    Object params[] = { comment, componentId };
		    String insertQuery = "INSERT INTO Comment (comment,calComponentID) VALUES (?,?)";
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
			String deleteQuery = "DELETE FROM Comment WHERE commentID = ?";
			template.executeUpdate(deleteQuery, params);
			setId(-1);
		}
	}


	public String getComment(){
		return comment;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setComment(String newVal){
		comment = newVal;
	}

	
	public void setComponentId(int componentId) {
		this.componentId = componentId; 
	}

	public int getComponentId() {
		return (this.componentId); 
	}

}