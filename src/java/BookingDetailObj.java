import java.time.LocalDateTime;

public class BookingDetailObj {
    private final int bookingID;
    private final int userID;
    private  final int eventID;
    private final LocalDateTime bookingDate;
    private final boolean cancelled;

    public BookingDetailObj(int bookingID, int userID, int eventID, LocalDateTime bookingDate, boolean isCancelled) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.eventID = eventID;
        this.bookingDate = bookingDate;
        this.cancelled = isCancelled;
    }

    // Getter methods
    public int getBookingID() {
        return bookingID;
    }

    public int getUserID() {
        return userID;
    }

    public int getEventID() {
        return eventID;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public boolean isIsCancelled() {
    return cancelled;
}

}
