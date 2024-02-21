import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "viewParicepentsObj")
@SessionScoped
public class EventParticepentsList implements Serializable {
    //@ManagedProperty(value = "#{sessionScope.eventParticipentsID}")
    private Integer eventID;

    private List<String> participants;

    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        eventID = (Integer) externalContext.getSessionMap().get("eventParticipentsID");
        System.out.print("EventID###: " + eventID);
        fetchParticipants();
    }

    public void fetchParticipants() {
        try {
            DerbyManager database = new DerbyManager(); // Replace YourClassName with the actual class name
            participants = database.getParticipentsForEvent(eventID);
        } catch (SQLException e) {
            // Handle the exception
            e.printStackTrace();
        }
    }

    // Add getters and setters for participants if needed
    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }
    
    public void removeParticipant(String name) {
        System.out.print(name);
        int userid2; 
        try {
            DerbyManager database = new DerbyManager(); // Replace YourClassName with the actual class name
            userid2 = database.getUserId(name);
             database.removeUserFromEvent(userid2, eventID);
        } catch (SQLException e) {
            // Handle the exception
            e.printStackTrace();
        }
    }
    

    
}
