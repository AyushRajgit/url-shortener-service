package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.event.UrlCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UrlEventConsumer {

    @KafkaListener(topics = "url-events", groupId = "url-event-group")
    public void consume(UrlCreatedEvent event) {
        System.out.println("Event received from Kafka!");
        System.out.println("Short Code : " + event.shortCode());
        System.out.println("Event Type : " + event.eventType());
    }
}
