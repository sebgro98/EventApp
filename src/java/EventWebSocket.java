import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/event-updates")
public class EventWebSocket {

    private static Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Handle incoming messages if needed
    }

    // Method to send a message to all connected clients
    public static void sendMessage(String message) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to notify clients about a canceled event
    public static void notifyEventCancelled(int eventId) {
        sendMessage("EventCancelled:" + eventId);
    }

    // Method to notify clients about a booked event
    public static void notifyEventBooked(int eventId) {
        sendMessage("EventBooked:" + eventId);
    }

    // Method to notify clients about an unbooked event
    public static void notifyEventUnbooked(int eventId) {
        sendMessage("EventUnbooked:" + eventId);
    }

    // Method to notify clients about a created event
    public static void notifyEventCreated(int eventId) {
        sendMessage("EventCreated:" + eventId);
    }
}