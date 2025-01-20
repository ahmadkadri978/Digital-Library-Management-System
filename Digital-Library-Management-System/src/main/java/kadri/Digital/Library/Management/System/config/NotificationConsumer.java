package kadri.Digital.Library.Management.System.config;

import kadri.Digital.Library.Management.System.module.NotificationMessage;
import kadri.Digital.Library.Management.System.service.NotificationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);
    @JmsListener(destination = "notification.queue")
    public void receiveNotification(NotificationMessage notificationMessage) {
        logger.debug("Received message: {}", notificationMessage.getMessage());


        System.out.println("Admin Notification: " + notificationMessage.getTitle() + " - " + notificationMessage.getMessage());

    }
}
