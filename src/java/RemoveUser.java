
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Sushil's pc
 */
@ManagedBean(name = "removeUserObj")
@SessionScoped
public class RemoveUser {

    public RemoveUser() {
    }
    
        public String removeParticipant(String name, int eventID) {
        System.out.println(name + eventID);
        int userid2; 
        try {
            DerbyManager database = new DerbyManager(); // Replace YourClassName with the actual class name
            userid2 = database.getUserId(name);
            System.out.println(userid2);
            database.removeUserFromEvent(userid2, eventID);
        } catch (SQLException e) {
            // Handle the exception
            e.printStackTrace();
        }
        
        return "sucessRegister";  
    }
    
}
