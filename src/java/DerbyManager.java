
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DerbyManager implements Serializable {

    public DerbyManager() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // No need to initialize the connection here
    }

    public Connection getConnection() throws SQLException {
        // Create a new connection each time this method is called
        return DriverManager.getConnection("jdbc:derby://localhost:1527/eventAppDb;create=true;");
    }

    public void removeUserFromEvent(int userID, int eventId) {
        String query = "DELETE FROM Bookings WHERE UserID = ? AND EventID = ?";
        System.out.println("removeUserFromEvent");
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userID);
            pstmt.setInt(2, eventId);

            int rowsAffected = pstmt.executeUpdate(); // Use executeUpdate for DELETE

            if (rowsAffected > 0) {
                updateNumOfPlaces(eventId);
                System.out.println("User removed from the event successfully.");
            } else {
                System.out.println("User was not booked for the specified event.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Log more details about the exception
            System.err.println("Error in removeUserFromEvent: " + e.getMessage());
        }
    }

    public void updateNumOfPlaces(int eventId) {
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement("UPDATE Events SET EventNumOfPlacesLeft = EventNumOfPlacesLeft + 1 WHERE EventID = ?")) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    public ArrayList<BookingDetailObj> getAllBookings() {
        ArrayList<BookingDetailObj> bookingDetails = new ArrayList<>();
        String query = "SELECT * FROM Bookings";

        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query);  ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int bookingID = rs.getInt("BookingID");
                int userID = rs.getInt("UserID");
                int eventID = rs.getInt("EventID");
                LocalDateTime bookingDate = rs.getTimestamp("BookingDate").toLocalDateTime();
                boolean isCancelled = rs.getBoolean("IsCancelled");

                // Create BookingDetailObj and add to the list
                BookingDetailObj bookingDetail = new BookingDetailObj(bookingID, userID, eventID, bookingDate, isCancelled);

                // Check if the list already contains a booking with the same details
                if (!bookingDetails.contains(bookingDetail)) {
                    bookingDetails.add(bookingDetail);
                } else {
                    // Object with similar properties already exists
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Log more details about the exception
            System.err.println("Error in getAllBookings: " + e.getMessage());
        }

        return bookingDetails;
    }

    public boolean checkUserInDB(String userName, String password) {
        boolean isValidUser = false;
        String query = "SELECT * FROM users WHERE username=? AND passwordhash=?";
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userName);
            pstmt.setString(2, password); // Assuming passwords are stored as plain text

            try ( ResultSet rs = pstmt.executeQuery()) {
                isValidUser = rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValidUser;
    }

    public int getUserId(String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username=?";
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            try ( ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    public ArrayList<EventDetailObj> getEventDetails() throws SQLException {
        ArrayList<EventDetailObj> evnDetObj = new ArrayList<>();
        String query = "SELECT * FROM events";
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query);  ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int eventID = rs.getInt("eventid");
                String eventName = rs.getString("eventname");
                String eventDate = rs.getString("eventdate");
                String eventDescription = rs.getString("eventdescription");
                int eventNumOfPlaces = rs.getInt("eventnumofplaces");
                int eventNumOfPlacesLeft = rs.getInt("eventnumofplacesleft");
                int eventAdminId = rs.getInt("adminid");

                EventDetailObj tempEvenDetObj = new EventDetailObj(eventID, eventName, eventDate, eventDescription, eventNumOfPlaces, eventNumOfPlacesLeft, eventAdminId);

                if (!evnDetObj.contains(tempEvenDetObj)) {
                    evnDetObj.add(tempEvenDetObj);
                } else {
                    // Object with similar properties already exists
                }
            }
        }
        return evnDetObj;
    }

    public ArrayList<BookingDetailObj> getUserBookings(int userID) throws SQLException {
        ArrayList<BookingDetailObj> userBookings = new ArrayList<>();

        // Query the database to get bookings for the specified user
        String query = "SELECT * FROM Bookings WHERE UserID = ?";

        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userID);
            System.out.println("Executing query: " + pstmt.toString()); // Debug print

            try ( ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int bookingID = rs.getInt("BookingID");
                    int eventID = rs.getInt("EventID");
                    LocalDateTime bookingDate = rs.getTimestamp("BookingDate").toLocalDateTime();
                    boolean cancelled = rs.getBoolean("IsCancelled");

                    BookingDetailObj bookingDetail = new BookingDetailObj(bookingID, userID, eventID, bookingDate, cancelled);
                    userBookings.add(bookingDetail);

                    // Debug print for each booking
                    System.out.println("Booking retrieved: " + bookingDetail);
                }
            }
        }

        System.out.println("Number of bookings retrieved: " + userBookings.size()); // Debug print
        return userBookings;
    }

    public ArrayList<BookingDetailObj> getEventBookings(int eventId) throws SQLException {
        ArrayList<BookingDetailObj> eventBookings = new ArrayList<>();

        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Bookings WHERE EventID = ?")) {

            pstmt.setInt(1, eventId);

            try ( ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookingDetailObj booking = new BookingDetailObj(
                            rs.getInt("BookingID"),
                            rs.getInt("UserID"),
                            rs.getInt("EventID"),
                            rs.getTimestamp("BookingDate").toLocalDateTime(),
                            rs.getBoolean("IsCancelled")
                    );

                    eventBookings.add(booking);
                }
            }
        }

        return eventBookings;
    }

    public boolean addEventInDb(String eventName, String eventDate, String eventDescription, int eventNumOfPlaces, int eventNumOfPlacesLeft, int eventAdminID) throws SQLException {
        String query = "INSERT INTO events (eventname, eventdate, eventdescription, eventnumofplaces, eventnumofplacesleft, adminid) " + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try ( PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, eventName);
            pstmt.setString(2, eventDate);
            pstmt.setString(3, eventDescription);
            pstmt.setInt(4, eventNumOfPlaces);
            pstmt.setInt(5, eventNumOfPlacesLeft);
            pstmt.setInt(6, eventAdminID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean addUserInDb(String username, String password, boolean admin) throws SQLException {
        String query = "INSERT INTO users (username, passwordhash, admin) " + "VALUES (?, ?, ?)";
        Connection conn = getConnection();
        try ( PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setBoolean(3, admin);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean isUserAdmin(String username) throws SQLException {
        String query = "SELECT admin FROM users WHERE username = ?";
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);

            try ( ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("admin");
                }
            }
        }
        return false; // Return false if the user is not found or if there is an error
    }

    public List<String> getParticipentsForEvent(int eventID) throws SQLException {
        List<String> participants = new ArrayList<>();
        String query = "SELECT userid FROM bookings WHERE eventid = ?";
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, eventID);

            try ( ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int userID = rs.getInt("userid");
                    String userName = getUserNameByID(userID); // Assuming a method to get username by ID
                    participants.add(userName);
                }
            }
        }
        return participants;
    }

    private String getUserNameByID(int userID) throws SQLException {
        String query = "SELECT username FROM users WHERE id = ?";

        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userID);

            try ( ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null; // Handle the case where username is not found
    }

}
