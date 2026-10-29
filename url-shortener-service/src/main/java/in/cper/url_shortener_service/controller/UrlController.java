package in.cper.url_shortener_service.controller;

import in.cper.url_shortener_service.dto.UrlRequestDTO;
import in.cper.url_shortener_service.dto.UrlResponseDTO;
import in.cper.url_shortener_service.service.UrlCreationService;
import in.cper.url_shortener_service.service.UrlRedirectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class UrlController {

    private UrlCreationService urlCreationService;
    private UrlRedirectionService urlRedirectionService;

    @Autowired
    public void UrlController(UrlCreationService urlCreationService, UrlRedirectionService urlRedirectionService) {
        this.urlCreationService = urlCreationService;
        this.urlRedirectionService =  urlRedirectionService;
    }

    @PostMapping("/shortenURL")
    public ResponseEntity<UrlResponseDTO> shortenURL(@Valid @RequestBody UrlRequestDTO requestDTO) {
        UrlResponseDTO response = urlCreationService.shortenUrl(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/redirectURL/{shortCode}")
    public ResponseEntity<UrlResponseDTO> getURL(@PathVariable String shortCode) {
        String originalUrl = urlRedirectionService.fetchURL(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }
}
