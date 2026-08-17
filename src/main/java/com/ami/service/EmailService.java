package com.ami.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    } 

    public void sendResetPasswordEmail(
            String toEmail,
            String firstName,
            String resetLink
    ) { 

        String subject = "AMI System - Password Reset Request";

        String body =
                "Hello " + firstName + ",\n\n"
                +
                "We received a request to reset your password for your AMI account.\n\n"
                +
                "Click the link below to reset your password:\n\n"
                +
                resetLink
                +
                "\n\n"
                +
                "This link will expire in 30 minutes.\n\n"
                +
                "If you did not request a password reset, please ignore this email.\n\n"
                +
                "Regards,\n"
                +
                "AMI Support Team";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(subject);

        message.setText(body);

        mailSender.send(message);
    }
}