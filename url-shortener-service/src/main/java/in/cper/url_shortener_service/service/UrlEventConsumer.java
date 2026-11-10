package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.event.UrlCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UrlEventConsumer {

    private EmailService emailService;

    public UrlEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "url-events", groupId = "url-event-group")
    public void consume(UrlCreatedEvent event) {
        System.out.println("Event received from Kafka!");

        emailService.sendUrlEmail(event.email(), event.shortUrl(), event.originalUrl());

        System.out.println("Email sent successfully to: " + event.email());
    }
}
