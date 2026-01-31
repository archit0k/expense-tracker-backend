package com.expensetracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean emailEnabled;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.email.enabled:false}") boolean emailEnabled) {
        this.mailSender = mailSender;
        this.emailEnabled = emailEnabled;
    }

    public void sendExpenseNotification(String toEmail, Double amount) {
        String subject = "Expense Recorded - $" + String.format("%.2f", amount);
        String body = String.format(
                "Hello,\n\n" +
                        "A new expense of $%.2f has been recorded in your Expense Tracker account.\n\n" +
                        "If you did not make this entry, please contact support immediately.\n\n" +
                        "Best regards,\n" +
                        "Expense Tracker Team",
                amount);

        sendEmail(toEmail, subject, body);
    }

    public void sendWelcomeEmail(String toEmail, String userName) {
        String subject = "Welcome to Expense Tracker!";
        String body = String.format(
                "Hello %s,\n\n" +
                        "Welcome to Expense Tracker! Your account has been created successfully.\n\n" +
                        "You can now start tracking your expenses and gain insights into your spending habits.\n\n" +
                        "Best regards,\n" +
                        "Expense Tracker Team",
                userName);

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (emailEnabled) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("noreply@expensetracker.com");

                mailSender.send(message);
                logger.info("Email sent successfully to: {}", to);
            } catch (Exception e) {
                logger.error("Failed to send email to {}: {}", to, e.getMessage());
            }
        } else {
            // Development mode - log email instead of sending
            logger.info("=== EMAIL (Dev Mode) ===");
            logger.info("To: {}", to);
            logger.info("Subject: {}", subject);
            logger.info("Body: {}", body);
            logger.info("========================");
        }
    }
}
