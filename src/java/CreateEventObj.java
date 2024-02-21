
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Sushil's pc
 */
@ManagedBean(name="createEventObj")
@SessionScoped
public class CreateEventObj {
    DerbyManager databaseData = new DerbyManager(); 
    private int eventId;
    private String eventName;
    private String eventDate;
    private String eventDescription;
    private int eventNumOfPlaces;
    private int eventNumOfPlacesLeft;
    private int eventAdminID;
    private String usernameParam;

    
    public CreateEventObj(){
    
    }
    
    public String createEvent() throws SQLException{
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Integer userID = (Integer) externalContext.getSessionMap().get("userID");
        System.out.println(eventName + " " + eventDate + " " + eventDescription + " " +  eventNumOfPlaces+ " " + eventNumOfPlacesLeft + " " +  userID + "username: " + usernameParam + " ");
        databaseData.addEventInDb(eventName, eventDate, eventDescription, eventNumOfPlaces, eventNumOfPlacesLeft, userID);
        
        return "sucessRegister"; 
    }
    
    public String getUsernameParam() {
        return usernameParam;
    }

    public void setUsernameParam(String usernameParam) {
        this.usernameParam = usernameParam;
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
