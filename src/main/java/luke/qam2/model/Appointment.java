package luke.qam2.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Appointment {

    public Appointment(int inAppointmentId, String inTitle, String inDescription, String inLocation, String inType, LocalDateTime inStart, LocalDateTime inEnd, int inCustomerId, int inUserId, int inContactId){
        appointmentId = inAppointmentId;
        title = inTitle;
        description = inDescription;
        location = inLocation;
        type = inType;
        start = inStart;
        end = inEnd;
        customerId = inCustomerId;
        userId = inUserId;
        contactId = inContactId;
    }

    public int appointmentId;
    public String title;
    public String description;
    public String location;
    public String type;
    public LocalDateTime start;
    public LocalDateTime end;
    public int customerId;
    public int userId;
    public int contactId;
}
