package database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationSimulator {

    // Storage for notifications: String[] {Timestamp, Target Contact, Message Text}
    private static final List<String[]> notificationQueue = new ArrayList<>();

    public static void sendNotification(String targetContact, String messageText) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = df.format(new Date());

        synchronized (notificationQueue) {
            notificationQueue.add(0, new String[]{timeStr, targetContact, messageText}); // newest first
        }
        
        System.out.println("Notification Alert Simulated: [" + targetContact + "] - " + messageText);
    }

    public static List<String[]> getNotificationHistory() {
        synchronized (notificationQueue) {
            return new ArrayList<>(notificationQueue);
        }
    }

    public static void clearHistory() {
        synchronized (notificationQueue) {
            notificationQueue.clear();
        }
    }
}
