package cz.cvut.felk.timejuggler.db.entity.interfaces;


public interface DurationEntity extends EntityElement {
	void setDays(int days);
	void setWeeks(int weeks);
	void setHours(int hours);
	void setMinutes(int minutes);
	void setSeconds(int seconds);
	boolean isNegative();
	int getDays();
	int getWeeks();
	int getHours();
	int getMinutes();
	int getSeconds();
}
