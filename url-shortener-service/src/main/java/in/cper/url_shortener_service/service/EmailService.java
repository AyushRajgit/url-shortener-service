package in.cper.url_shortener_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendUrlEmail(String email, String shortUrl, String originalUrl) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Your Shortened URL is Ready!");
        mailMessage.setText(
                "Hello!\n\n" +
                        "Your URL has been successfully shortened.\n\n" +
                        "Original URL: " + originalUrl + "\n" +
                        "Short URL: " + shortUrl + "\n\n" +
                        "Thank you for using our URL Shortener!"
        );

        mailSender.send(mailMessage);
    }
}
