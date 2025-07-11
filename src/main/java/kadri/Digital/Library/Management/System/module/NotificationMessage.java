package kadri.Digital.Library.Management.System.module;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String message;
    private LocalDateTime timestamp;

    public NotificationMessage(String title, String message) {
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
