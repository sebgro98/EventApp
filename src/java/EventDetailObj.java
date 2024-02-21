/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Sushil's pc
 */
public class EventDetailObj {
    private int eventId; 
    private String eventName; 
    private String eventDate; 
    private String eventDescription; 
    private int eventNumOfPlaces; 
    private int eventNumOfPlacesLeft;      
    private int eventAdminID; 
     private BookingDetailObj booking;

    public EventDetailObj(int eventId, String eventName, String eventDate, String eventDescription, int eventNumOfPlaces, int eventNumOfPlacesLeft, int eventAdminID) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventNumOfPlaces = eventNumOfPlaces;
        this.eventNumOfPlacesLeft = eventNumOfPlacesLeft;
        this.eventAdminID = eventAdminID;
    }
    
      public BookingDetailObj getBooking() {
        return booking;
    }
      
        public void setBooking(BookingDetailObj booking) {
        this.booking = booking;
    }
        
        
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public int getEventNumOfPlaces() {
        return eventNumOfPlaces;
    }

    public void setEventNumOfPlaces(int eventNumOfPlaces) {
        this.eventNumOfPlaces = eventNumOfPlaces;
    }

    public int getEventNumOfPlacesLeft() {
        return eventNumOfPlacesLeft;
    }

    public void setEventNumOfPlacesLeft(int eventNumOfPlacesLeft) {
        this.eventNumOfPlacesLeft = eventNumOfPlacesLeft;
    }

    public int getEventAdminID() {
        return eventAdminID;
    }

    public void setEventAdminID(int eventAdminID) {
        this.eventAdminID = eventAdminID;
    }
    
    
}
