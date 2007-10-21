USE DATABASE TimeJuggler
;





--  Create Tables 
CREATE TABLE Attachment ( 
	attachmentID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	attach VARCHAR(32672)  ,
	calComponentID Integer  ,
	isBinary SMALLINT  
) 
;

CREATE TABLE Attendee ( 
	address VARCHAR(1500)  ,
	attendeeID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	calComponentID Integer  
) 
;

CREATE TABLE CalComponent ( 
	calComponentID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	dateTimeID Integer  ,
	uid VARCHAR(255)  ,
	vCalendarID Integer  ,
	url VARCHAR(1500)  ,
	clazz VARCHAR(255)  ,
	description VARCHAR(32672)  ,
	organizer VARCHAR(255)  ,
	sequence Integer ,
	status VARCHAR(255)  ,
	summary VARCHAR(32672)  ,
	recurrenceid TIMESTAMP  ,
	dtstamp TIMESTAMP
) 
;

CREATE TABLE Categories ( 
	categoriesID Integer NOT NULL GENERATED ALWAYS AS IDENTITY,
	categoryID Integer,
	calComponentID Integer
) 
;

CREATE TABLE Category ( 
	categoryID Integer NOT NULL GENERATED ALWAYS AS IDENTITY,
	--calComponentID Integer,
	name VARCHAR(255),
	color Integer	/* pridano */
) 
;

CREATE TABLE Comment ( 
	commentID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	calComponentID Integer,
	comment VARCHAR(32672)
) 
;

CREATE TABLE Contact ( 
	contactID Integer NOT NULL GENERATED ALWAYS AS IDENTITY,
	calComponentID Integer,
	contact VARCHAR(255)
) 
;

CREATE TABLE DateTime ( 
	dateTimeID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	distinctDatesID Integer  ,
	 startDate TIMESTAMP,	/* ! */
	 endDate TIMESTAMP,	/* !, nebo delka trvani udalosti - duration */
	durationID Integer,	/* nebo endDate/endTime */
	
	periodsID Integer  ,
	created TIMESTAMP  ,
	lastmodified TIMESTAMP  
) 
;

CREATE TABLE DayOfWeek ( 
	day Integer ,
	dayOfWeekID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	repetitionRuleID Integer  ,
	dayInterval Integer 
) 
;

CREATE TABLE DistinctDate ( 
	Date Date  ,
	distinctDateID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	distinctDatesID Integer  
) 
;

CREATE TABLE DistinctDates ( 
	distinctDatesID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY
) 
;

CREATE TABLE Duration ( 
	durationID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	negative SMALLINT  ,
	days Integer ,
	weeks Integer ,
	hours Integer ,
	minutes Integer ,
	seconds Integer 
) 
;


CREATE TABLE Period ( 
	periodID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	distinctDatesID Integer  ,
	durationID Integer,
	startDate TIMESTAMP, /* zmena na timestamp*/
	endDate TIMESTAMP, /* zmena na timestamp*/
	/*startTime Time, zmena! */
	/*endTime Time, zmena ! */
	periodsID Integer  ,
	/*timeID Integer  ,*/
	rrule Integer ,
	exrule Integer 
) 
;

CREATE TABLE Periods ( 
	periodsID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY
) 
;

CREATE TABLE RelatedTo ( 
	relatedToID Integer NOT NULL GENERATED ALWAYS AS IDENTITY,
	calComponentID Integer,
	relatedto VARCHAR(255)
) 
;

CREATE TABLE RepetitionRule ( 
	repetitionRuleID Integer NOT NULL GENERATED ALWAYS AS IDENTITY,
	repetitionRulesID Integer,
	frequency Integer,
	interval Integer,
	repeat Integer,
	weekStart Integer,
/*
	byHour Integer ,
	byWeekNo Integer ,
	byYearDay Integer ,
	bySetPosition Integer ,
	byMonth Integer ,
	byMinute  Integer ,
	byMonthDay Integer 
*/
	byHour VARCHAR(400) ,
	byWeekNo VARCHAR(400) ,
	byYearDay VARCHAR(400) ,
	bySetPosition VARCHAR(400),
	byMonth VARCHAR(400),
	byMinute  VARCHAR(400),
	byMonthDay VARCHAR(400)

) 
;

CREATE TABLE RepetitionRules ( 
	repetitionRulesID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY
) 
;

CREATE TABLE RequestStatus ( 
	calComponentID Integer  ,
	requestStatusID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	rstatus VARCHAR(255)  
) 
;

CREATE TABLE Resource ( 
	calComponentID Integer  ,
	resource VARCHAR(255)  ,
	resourceID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY
) 
;

CREATE TABLE VAlarm ( 
	calComponentID Integer  ,
	description VARCHAR(32672)  ,
	durationID Integer  ,
	vAlarmID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	summary VARCHAR(32672)  ,
	action VARCHAR(255)  ,
	repeat Integer ,
	trigg Integer  ,
	attach VARCHAR(32672)  ,
	attendee VARCHAR(255)
) 
;

CREATE TABLE VCalendar ( 
	prodid VARCHAR(255)  ,
	vCalendarID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	version VARCHAR(255)  ,
	calscale VARCHAR(255)  ,
	method VARCHAR(255)  ,
	name VARCHAR(255) ,
	active Integer	/*pridano*/
) 
;

CREATE TABLE VEvent ( 
	calComponentID Integer  ,
	geo VARCHAR(255)  ,
	vEventID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	location VARCHAR(255)  ,
	priority Integer ,
	transp VARCHAR(255)  
) 
;

CREATE TABLE VFreeBusy ( 
	calComponentID Integer  ,
	periodsID Integer  ,
	vFreeBusyID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY
) 
;

CREATE TABLE VJournal ( 
	calComponentID Integer  ,
	vJournalID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY
) 
;

CREATE TABLE VTimeZone ( 
	dateTimeID Integer  ,
	tzid VARCHAR(255)  ,
	vTimeZoneID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	tzname VARCHAR(255)  ,
	tzoffsetfrom VARCHAR(5)  ,
	tzoffsetto VARCHAR(5)  ,
	comment VARCHAR(255)  ,
	tzurl VARCHAR(255)  ,
	standardc Integer  ,
	daylight Integer
) 
;

CREATE TABLE VToDo ( 
	calComponentID Integer  ,
	due TIMESTAMP  , /* zrusit */
	vToDoID Integer   NOT NULL GENERATED ALWAYS AS IDENTITY,
	geo VARCHAR(255)  ,
	location VARCHAR(255)  ,
	priority Integer ,
	percentcomplete Integer ,
	completed TIMESTAMP  
) 
;


--  Create Primary Key Constraints 
ALTER TABLE Attachment ADD CONSTRAINT PK_Attachment 
PRIMARY KEY (attachmentID) 
;

ALTER TABLE Attendee ADD CONSTRAINT PK_Attendee 
PRIMARY KEY (attendeeID) 
;

ALTER TABLE CalComponent ADD CONSTRAINT PK_CalComponent 
PRIMARY KEY (calComponentID) 
;

ALTER TABLE Category ADD CONSTRAINT PK_Category 
PRIMARY KEY (categoryID) 
;

ALTER TABLE Categories ADD CONSTRAINT Categories 
PRIMARY KEY (categoriesID) 
;

ALTER TABLE Comment ADD CONSTRAINT PK_Comment 
PRIMARY KEY (commentID) 
;

ALTER TABLE Contact ADD CONSTRAINT PK_Contact 
PRIMARY KEY (contactID) 
;

ALTER TABLE DateTime ADD CONSTRAINT PK_DateTime 
PRIMARY KEY (dateTimeID) 
;

ALTER TABLE DayOfWeek ADD CONSTRAINT PK_DayOfWeek 
PRIMARY KEY (dayOfWeekID) 
;

ALTER TABLE DistinctDate ADD CONSTRAINT PK_DistinctDate 
PRIMARY KEY (distinctDateID) 
;

ALTER TABLE DistinctDates ADD CONSTRAINT PK_DistinctDates 
PRIMARY KEY (distinctDatesID) 
;

ALTER TABLE Duration ADD CONSTRAINT PK_Duration 
PRIMARY KEY (durationID) 
;
/*
ALTER TABLE Number ADD CONSTRAINT PK_Number 
PRIMARY KEY (numberID) 
;

ALTER TABLE NumberList ADD CONSTRAINT PK_NumberList 
PRIMARY KEY (numberListID) 
;
*/
ALTER TABLE Period ADD CONSTRAINT PK_Period 
PRIMARY KEY (periodID) 
;

ALTER TABLE Periods ADD CONSTRAINT PK_Periods 
PRIMARY KEY (periodsID) 
;

ALTER TABLE RelatedTo ADD CONSTRAINT PK_RelatedTo 
PRIMARY KEY (relatedToID) 
;

ALTER TABLE RepetitionRule ADD CONSTRAINT PK_RepetitionRule 
PRIMARY KEY (repetitionRuleID) 
;

ALTER TABLE RepetitionRules ADD CONSTRAINT PK_RepetitionRules 
PRIMARY KEY (repetitionRulesID) 
;

ALTER TABLE RequestStatus ADD CONSTRAINT PK_RequestStatus 
PRIMARY KEY (requestStatusID) 
;

ALTER TABLE Resource ADD CONSTRAINT PK_Resource 
PRIMARY KEY (resourceID) 
;
/*
ALTER TABLE Time ADD CONSTRAINT PK_Time 
PRIMARY KEY (timeID) 
;*/

ALTER TABLE VAlarm ADD CONSTRAINT PK_VAlarm 
PRIMARY KEY (vAlarmID) 
;

ALTER TABLE VCalendar ADD CONSTRAINT PK_VCalendar 
PRIMARY KEY (vCalendarID) 
;

ALTER TABLE VEvent ADD CONSTRAINT PK_VEvent 
PRIMARY KEY (vEventID) 
;

ALTER TABLE VFreeBusy ADD CONSTRAINT PK_VFreeBusy 
PRIMARY KEY (vFreeBusyID) 
;

ALTER TABLE VJournal ADD CONSTRAINT PK_VJournal 
PRIMARY KEY (vJournalID) 
;

ALTER TABLE VTimeZone ADD CONSTRAINT PK_VTimeZone 
PRIMARY KEY (vTimeZoneID) 
;

ALTER TABLE VToDo ADD CONSTRAINT PK_VToDo 
PRIMARY KEY (vToDoID) 
;




--  Create Foreign Key Constraints 
ALTER TABLE Attachment ADD CONSTRAINT FK_Attachment_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE Attendee ADD CONSTRAINT FK_Attendee_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE CalComponent ADD CONSTRAINT FK_CalComponent_DateTime 
FOREIGN KEY (dateTimeID) REFERENCES DateTime (dateTimeID)
;

ALTER TABLE CalComponent ADD CONSTRAINT FK_CalComponent_VCalendar 
FOREIGN KEY (vCalendarID) REFERENCES VCalendar (vCalendarID)
;

/*ALTER TABLE Category ADD CONSTRAINT FK_Category_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;
*/
ALTER TABLE Categories ADD CONSTRAINT FK_Categories_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE Categories ADD CONSTRAINT FK_Categories_Category 
FOREIGN KEY (categoryID) REFERENCES Category (categoryID)
;


ALTER TABLE Comment ADD CONSTRAINT FK_Comment_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE Contact ADD CONSTRAINT FK_Contact_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE DateTime ADD CONSTRAINT FK_DateTime_DistinctDates 
FOREIGN KEY (distinctDatesID) REFERENCES DistinctDates (distinctDatesID)
;

ALTER TABLE DateTime ADD CONSTRAINT FK_DateTime_Periods 
FOREIGN KEY (periodsID) REFERENCES Periods (periodsID)
;

ALTER TABLE DayOfWeek ADD CONSTRAINT FK_DayOfWeek_RepetitionRule 
FOREIGN KEY (repetitionRuleID) REFERENCES RepetitionRule (repetitionRuleID)
;

ALTER TABLE DistinctDate ADD CONSTRAINT FK_DistinctDate_DistinctDates 
FOREIGN KEY (distinctDatesID) REFERENCES DistinctDates (distinctDatesID)
;
/*
ALTER TABLE Number ADD CONSTRAINT FK_Number_NumberList 
FOREIGN KEY (numberListID) REFERENCES NumberList (numberListID)
;
*/
ALTER TABLE Period ADD CONSTRAINT FK_exrule 
FOREIGN KEY (exrule) REFERENCES RepetitionRules (repetitionRulesID)
;

ALTER TABLE Period ADD CONSTRAINT FK_rrule 
FOREIGN KEY (rrule) REFERENCES RepetitionRules (repetitionRulesID)
;

ALTER TABLE Period ADD CONSTRAINT FK_Period_DistinctDates 
FOREIGN KEY (distinctDatesID) REFERENCES DistinctDates (distinctDatesID)
;

ALTER TABLE Period ADD CONSTRAINT FK_Period_Duration 
FOREIGN KEY (durationID) REFERENCES Duration (durationID)
;

ALTER TABLE Period ADD CONSTRAINT FK_Period_Periods 
FOREIGN KEY (periodsID) REFERENCES Periods (periodsID)
;
/*
ALTER TABLE Period ADD CONSTRAINT FK_Period_Time 
FOREIGN KEY (timeID) REFERENCES Time (timeID)
;
*/
ALTER TABLE RelatedTo ADD CONSTRAINT FK_RelatedTo_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;
/*
ALTER TABLE RepetitionRule ADD CONSTRAINT FK_byHour 
FOREIGN KEY (byHour) REFERENCES NumberList (numberListID)
;

ALTER TABLE RepetitionRule ADD CONSTRAINT FK_byMinute 
FOREIGN KEY (byMinute) REFERENCES NumberList (numberListID)
;

ALTER TABLE RepetitionRule ADD CONSTRAINT FK_byMonth 
FOREIGN KEY (byMonth) REFERENCES NumberList (numberListID)
;

ALTER TABLE RepetitionRule ADD CONSTRAINT FK_byMonthDay 
FOREIGN KEY (byMonthDay) REFERENCES NumberList (numberListID)
;

ALTER TABLE RepetitionRule ADD CONSTRAINT FK_bySetPosition 
FOREIGN KEY (bySetPosition) REFERENCES NumberList (numberListID)
;

ALTER TABLE RepetitionRule ADD CONSTRAINT FK_byWeekNo 
FOREIGN KEY (byWeekNo) REFERENCES NumberList (numberListID)
;

ALTER TABLE RepetitionRule ADD CONSTRAINT FK_byYearDay 
FOREIGN KEY (byYearDay) REFERENCES NumberList (numberListID)
;
*/
ALTER TABLE RepetitionRule ADD CONSTRAINT FK_RepetitionRule_RepetitionRules 
FOREIGN KEY (repetitionRulesID) REFERENCES RepetitionRules (repetitionRulesID)
;

ALTER TABLE RequestStatus ADD CONSTRAINT FK_RequestStatus_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE Resource ADD CONSTRAINT FK_Resource_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE VAlarm ADD CONSTRAINT FK_VAlarm_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE VAlarm ADD CONSTRAINT FK_VAlarm_Duration 
FOREIGN KEY (durationID) REFERENCES Duration (durationID)
;

ALTER TABLE VEvent ADD CONSTRAINT FK_VEvent_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE VFreeBusy ADD CONSTRAINT FK_VFreeBusy_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE VFreeBusy ADD CONSTRAINT FK_VFreeBusy_Periods 
FOREIGN KEY (periodsID) REFERENCES Periods (periodsID)
;

ALTER TABLE VJournal ADD CONSTRAINT FK_VJournal_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

ALTER TABLE VTimeZone ADD CONSTRAINT FK_VTimeZone_DateTime 
FOREIGN KEY (dateTimeID) REFERENCES DateTime (dateTimeID)
;

ALTER TABLE VToDo ADD CONSTRAINT FK_VToDo_CalComponent 
FOREIGN KEY (calComponentID) REFERENCES CalComponent (calComponentID)
;

--DEFAULT DATA

INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES ('-//CVUT //TimeJuggler Calendar 0.1//CZ','2.0','GREGORIAN','PUBLISH','Timejuggler');
INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES ('-//CVUT //TimeJuggler Calendar 0.1//CZ','2.0','GREGORIAN','PUBLISH','Svatky');
INSERT INTO VCalendar (prodid,version,calscale,method,name) VALUES ('-//CVUT //TimeJuggler Calendar 0.1//CZ','2.0','GREGORIAN','PUBLISH','Ostatni');

INSERT INTO Category (name,color) VALUES ('Svatky',null);
INSERT INTO Category (name,color) VALUES ('Vyroci',255);
INSERT INTO Category (name,color) VALUES ('Skola',3999);
INSERT INTO Category (name,color) VALUES ('Osobni',0);




