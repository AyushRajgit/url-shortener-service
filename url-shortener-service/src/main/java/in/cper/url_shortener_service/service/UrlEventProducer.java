package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.event.UrlCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UrlEventProducer {

    private KafkaTemplate<String, UrlCreatedEvent> kafkaTemplate;

    public UrlEventProducer(KafkaTemplate<String, UrlCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUrlCreatedEvent(UrlCreatedEvent event) {
        kafkaTemplate.send("url-events", event.shortCode(), event);
    }
}
