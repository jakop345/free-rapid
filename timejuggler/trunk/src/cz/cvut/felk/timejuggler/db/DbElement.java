package cz.cvut.felk.timejuggler.db;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 14-IV-2007 15:35:01
 */
public abstract class DbElement {

	private int id = -1;

	public DbElement(){

	}
	public DbElement(int id){
		this.id=id;
	}
	public void finalize() throws Throwable {

	}
	
	public int getid(){
		return id;
	}
	public void setid(int newVal){
		id = newVal;
	}

	public abstract void store();

}