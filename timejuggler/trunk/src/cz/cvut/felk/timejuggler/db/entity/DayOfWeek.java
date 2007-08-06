package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.*;

import java.util.logging.Logger;
/**
 * @version 0.1
 * @created 12-V-2007 18:42:03
 */
public class DayOfWeek extends DbElement{

	private int day;
	private int dayInterval;
//	public RepetitionRule m_RepetitionRule;
//	public ByDayOfWeek m_ByDayOfWeek;

	public DayOfWeek(){
		//TODO DayOfWeek
	}

	public void store (){
	}

	public int getDayInterval(){
		return dayInterval;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setDayInterval(int newVal){
		dayInterval = newVal;
	}

	public int getDay(){
		return day;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setDay(int newVal){
		day = newVal;
	}

}