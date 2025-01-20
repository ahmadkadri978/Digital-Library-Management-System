package kadri.Digital.Library.Management.System.service;

import kadri.Digital.Library.Management.System.module.NotificationMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import static java.lang.System.out;

@Service
public class NotificationProducer {
    private final JmsTemplate jmsTemplate;
    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);
    @Autowired
    public NotificationProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sentNotification(NotificationMessage notificationMessage) {
        logger.info("sending notification: {}", notificationMessage.getMessage());
        jmsTemplate.convertAndSend("notification.queue", notificationMessage);
        logger.info("Notification sent successfully");
    }
}
