package cz.cvut.felk.timejuggler.core.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Jerry!
 * 
 */
public class DateInterval implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Date startDate;
	
	private Date endDate;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
