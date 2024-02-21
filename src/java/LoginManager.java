import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

// that there will be one instance of this managed bean for each user session. 
@ManagedBean(name = "loginObj")
@SessionScoped
public class LoginManager {

    private String userName;
    private String password;
    private int adminUserCode;
    private int userID;
    private boolean isAdmin;
    ArrayList<EventDetailObj> evnDetObj = new ArrayList<>();
    DerbyManager databaseData = new DerbyManager();

    public String checkCread() throws SQLException {
        // Check user credentials in the database
        if (databaseData.checkUserInDB(userName, password)) {
            userID = databaseData.getUserId(userName);
            isAdmin = databaseData.isUserAdmin(userName);
            // Set userID in the session map
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.getSessionMap().put("userID", userID);
            externalContext.getSessionMap().put("isAdmin", isAdmin);
            // Retrieve event details
            evnDetObj = databaseData.getEventDetails();
            // Call categorizeEvents after retrieving event details
            onLoginSuccess();
            // Print for debugging (you can remove these lines in production)
            System.out.println(password + " " + userName + " " + userID + " " + evnDetObj);
            // Navigate to the home page
            return "homePage";
        } else {
            // If login fails, navigate to the login error page
            return "loginErrorPage";
        }
    }

    public String registerAcc() throws SQLException {
        // Set userID in the session map
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        if (adminUserCode == 1) {
            databaseData.addUserInDb(userName, password, true);
            externalContext.getSessionMap().put("isAdmin", true);
            return "sucessRegister";
        } else if (adminUserCode == 0) {
            databaseData.addUserInDb(userName, password, false);
            externalContext.getSessionMap().put("isAdmin", false);
            return "sucessRegister";
        }
        return "loginErrorPage";
    }

    public void onLoginSuccess() {
        FacesContext context = FacesContext.getCurrentInstance();
        HomePageManager homePageObj = context.getApplication().evaluateExpressionGet(context, "#{homePageObj}", HomePageManager.class);
        homePageObj.categorizeEvents();
    }

    public int getAdminUserCode() {
        return adminUserCode;
    }

    public void setAdminUserCode(int adminUserCode) {
        this.adminUserCode = adminUserCode;
    }

    public ArrayList<EventDetailObj> getEvnDetObj() {
        return evnDetObj;
    }

    public void setEvnDetObj(ArrayList<EventDetailObj> evnDetObj) {
        this.evnDetObj = evnDetObj;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

}
