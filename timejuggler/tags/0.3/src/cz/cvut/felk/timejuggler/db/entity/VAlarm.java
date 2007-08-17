package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

import java.util.List;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 28-IV-2007 19:19:36
 */
public class VAlarm extends DbElement {

    private String description;
    private String summary;
    private String action;
    private int repeat;
    private int componentId;
    //private DurationDateTime trigger;
    private String attachment;
    private List<String> attendee;

    private Duration duration;

    public VAlarm() {

    }

    public void store() {
    }

    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        //TODO : Update
        if (duration != null) {
            duration.saveOrUpdate(template);
        }

        Object params[] = {componentId, description, duration.getId(), summary, action, repeat, null, attachment, null};
        String insertQuery = "INSERT INTO VAlarm (calComponentID,description,durationID,summary,action,repeat,trigg,attach,attendee) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            template.executeUpdate(insertQuery, params);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        setId(template.getGeneratedId());
    }

    public void delete(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            Object params[] = {getId()};
            String deleteQuery = "DELETE FROM VAlarm WHERE vAlarmID=?";
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }

    public String getDescription() {
        return description;
    }

    /**
     * @param newVal
     */
    public void setDescription(String newVal) {
        description = newVal;
    }

    public String getSummary() {
        return summary;
    }

    /**
     * @param newVal
     */
    public void setSummary(String newVal) {
        summary = newVal;
    }

    public String getAction() {
        return action;
    }

    /**
     * @param newVal
     */
    public void setAction(String newVal) {
        action = newVal;
    }

    public int getRepeat() {
        return repeat;
    }

    /**
     * @param newVal
     */
    public void setRepeat(int newVal) {
        repeat = newVal;
    }

    public List<String> getAttendee() {
        return attendee;
    }

    /**
     * @param newVal
     */
    public void setAttendee(List<String> newVal) {
        attendee = newVal;
    }

    public String getAttachment() {
        return attachment;
    }

    /**
     * @param newVal
     */
    public void setAttachment(String newVal) {
        attachment = newVal;
    }

    /*
        public DurationDateTime getTrigger(){
            return trigger;
        }

        public void setTrigger(DurationDateTime newVal){
            trigger = newVal;
        }

    */
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public int getComponentId() {
		return (this.componentId); 
	}

}