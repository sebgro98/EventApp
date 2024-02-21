
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "homePageObj")
@SessionScoped
public class HomePageManager implements Serializable {

    private DerbyManager derbyManager;
    private ArrayList<BookingDetailObj> allBookings;
    private List<EventDetailObj> myEvents;
    private List<EventDetailObj> otherEvents;
    private boolean isAdmin;

    public HomePageManager() {
        derbyManager = new DerbyManager();
        allBookings = new ArrayList<>();
        myEvents = new ArrayList<>();
        otherEvents = new ArrayList<>();
        refreshAllBookings(); // Call this method in the constructor
    }

    public void categorizeEvents() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Integer userID = (Integer) externalContext.getSessionMap().get("userID");
        isAdmin = (boolean) externalContext.getSessionMap().get("isAdmin");
        myEvents.clear();
        otherEvents.clear();

        if (userID != null) {
            // Retrieve the LoginManager bean from the FacesContext
            LoginManager loginObj = (LoginManager) externalContext.getSessionMap().get("loginObj");

            if (loginObj != null) {
                List<EventDetailObj> events = loginObj.getEvnDetObj();

                System.out.println("Total events: " + events.size());

                for (EventDetailObj event : events) {
                    System.out.println("Event ID: " + event.getEventId() + ", Admin ID: " + event.getEventAdminID());
                    if (event.getEventAdminID() == userID) {
                        myEvents.add(event);
                    } else {
                        otherEvents.add(event);
                    }
                }

                System.out.println("My Events: " + myEvents.size());
                System.out.println("Other Events: " + otherEvents.size());
            } else {
                System.out.println("LoginManager bean is null.");
            }
        } else {
            System.out.println("User ID is null.");
        }
    }

    public List<EventDetailObj> getMyEvents() {
        return myEvents;
    }

    public List<EventDetailObj> getOtherEvents() {
        return otherEvents;
    }

    public boolean isUserAdminForEvent(EventDetailObj event) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Integer userID = (Integer) externalContext.getSessionMap().get("userID");

        return userID != null && event.getEventAdminID() == userID;
    }

    public void bookEvent(EventDetailObj event) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Integer userID = (Integer) externalContext.getSessionMap().get("userID");
        int eventID = event.getEventId();
        if (userID != null) {
            try ( Connection conn = derbyManager.getConnection();  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Bookings (UserID, EventID, BookingDate, IsCancelled) VALUES (?, ?, CURRENT_TIMESTAMP, FALSE)")) {
                pstmt.setInt(1, userID);
                pstmt.setInt(2, event.getEventId());
                pstmt.executeUpdate();
                updatePlacesLeftAfterBooking(event.getEventId(), event.getEventNumOfPlacesLeft() - 1);
                // Update the list of bookings after a new booking is made
                refreshAllBookings();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }

            // Send WebSocket message
            String message = "EventBooked:";
            sendEventCancellationWebSocketMessage(eventID, message);
            //return "booked";
        } else {
            //return "loginErrorPage";
        }
    }

    public boolean renderBookButton(EventDetailObj event) {
        refreshAllBookings();

        int availablePlaces = event.getEventNumOfPlacesLeft();
        boolean shouldRender = availablePlaces > 0 && !hasUserBooked(event) && !isUserAdminForEvent(event);

        if (!shouldRender) {
            System.out.println("Event " + event.getEventId() + " is full or you have already booked the event.");
        }

        return shouldRender;
    }

    public void cancelEvent(EventDetailObj event) {
        System.out.println("Cancel Event called with event: " + event);

        try {
            int eventID = event.getEventId();
            System.out.println("Event ID to cancel: " + eventID);

            // Retrieve the bookings associated with the event
            List<BookingDetailObj> eventBookings = derbyManager.getEventBookings(eventID);

            // Cancel all associated bookings
            for (BookingDetailObj booking : eventBookings) {
                try ( Connection conn = derbyManager.getConnection();  PreparedStatement pstmt = conn.prepareStatement("UPDATE Bookings SET IsCancelled = TRUE WHERE BookingID = ?")) {
                    pstmt.setInt(1, booking.getBookingID());
                    pstmt.executeUpdate();
                }
            }

            // Now, delete the event
            try ( Connection conn = derbyManager.getConnection();  PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Events WHERE EventID = ?")) {
                pstmt.setInt(1, eventID);
                pstmt.executeUpdate();
            }

            // Notify users about event cancellation
            notifyUsersAboutEventCancellation(eventBookings);

            // Send WebSocket message
            String message = "EventCancelled:";
            sendEventCancellationWebSocketMessage(eventID, message);

            //return "eventCancelled"; // or the appropriate outcome
        } catch (Exception e) {
            e.printStackTrace();
            // Log more details about the exception
            System.err.println("Error in cancelEvent: " + e.getMessage());
            //return "loginErrorPage"; // or the appropriate error outcome
        }
    }

    private void sendEventCancellationWebSocketMessage(int eventID, String message) {
        EventWebSocket.sendMessage(message + eventID);
    }

    private void notifyUsersAboutEventCancellation(List<BookingDetailObj> bookings) {

        System.out.println("Im not here fucker!");
        System.out.println("Number of bookings: " + bookings.size());
        SendMail sendMail = new SendMail();
        String subject = "Event Cancellation Notification";
        String body = "The event you booked has been cancelled.";

        for (BookingDetailObj booking : bookings) {
            try {
                int eventID = booking.getEventID();
                int userID = booking.getUserID();
                sendMail.sendEmailToUser(userID, subject, body, eventID);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    public void refreshAllBookings() {
        allBookings = derbyManager.getAllBookings();
    }

    public List<BookingDetailObj> getAllBookings() {
        return allBookings;
    }

    public boolean hasUserBooked(EventDetailObj event) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Integer userID = (Integer) externalContext.getSessionMap().get("userID");

        if (userID != null) {
            for (BookingDetailObj booking : allBookings) {
                System.out.println("Checking booking: " + booking);
                System.out.println("User ID: " + userID);
                System.out.println("Event ID: " + event.getEventId());

                if (booking.getUserID() == userID && booking.getEventID() == event.getEventId()) {
                    System.out.println("User has booked the event");
                    return true; // User has booked the event
                }
            }
        }

        System.out.println("User not logged in or hasn't booked the event");
        return false; // User not logged in or hasn't booked the event
    }

    public void unbookEvent(EventDetailObj event) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Integer userID = (Integer) externalContext.getSessionMap().get("userID");
        int eventID = event.getEventId();
        if (userID != null) {
            try ( Connection conn = derbyManager.getConnection();  PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Bookings WHERE UserID = ? AND EventID = ?")) {
                pstmt.setInt(1, userID);
                pstmt.setInt(2, event.getEventId());
                pstmt.executeUpdate();
                updatePlacesLeftAfterUnbooking(event.getEventId(), event.getEventNumOfPlacesLeft() + 1);
                // Navigation to the unbooked page
                //return "unbooked";
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }

            String message = "EventUnbooked:";
            sendEventCancellationWebSocketMessage(eventID, message);
        }

        //return "loginErrorPage";
    }

    // Update the number of places left after unbooking
    public void updatePlacesLeftAfterUnbooking(int eventId, int newPlacesLeft) {
        try ( Connection conn = derbyManager.getConnection();  PreparedStatement pstmt = conn.prepareStatement("UPDATE Events SET EventNumOfPlacesLeft = ? WHERE EventID = ?")) {
            pstmt.setInt(1, newPlacesLeft);
            pstmt.setInt(2, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    // Update the number of places left after booking
    public void updatePlacesLeftAfterBooking(int eventId, int newPlacesLeft) {
        try ( Connection conn = derbyManager.getConnection();  PreparedStatement pstmt = conn.prepareStatement("UPDATE Events SET EventNumOfPlacesLeft = ? WHERE EventID = ?")) {
            pstmt.setInt(1, newPlacesLeft);
            pstmt.setInt(2, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    public String viewMyBookings() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Integer userID = (Integer) externalContext.getSessionMap().get("userID");

        if (userID != null) {
            try {
                // Retrieve bookings for the current user
                allBookings = derbyManager.getUserBookings(userID);
                System.out.println("User ID: " + userID);
                System.out.println("Number of bookings retrieved: " + allBookings.size());
                for (BookingDetailObj booking : allBookings) {
                    System.out.println("Booking: " + booking);
                }
                return "myBookings"; // Navigation to the myBookings page
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        }

        System.out.println("User not logged in or error retrieving bookings.");
        return "loginErrorPage";
    }

    public String makeNewEvent() {
        return "newEventForm";
    }

    public String viewEventPrati(EventDetailObj event) {
        int eventID = event.getEventId();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.getSessionMap().put("eventParticipentsID", eventID);
        System.out.println("eventID: " + eventID);
        System.out.println("Session Map: " + externalContext.getSessionMap().get("eventParticipentsID"));
        return "eventParticipentsList";
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
