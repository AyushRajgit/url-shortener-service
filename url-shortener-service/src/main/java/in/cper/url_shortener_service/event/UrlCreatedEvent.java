package in.cper.url_shortener_service.event;

public record UrlCreatedEvent(
   String shortCode,
   String eventType
) {}
