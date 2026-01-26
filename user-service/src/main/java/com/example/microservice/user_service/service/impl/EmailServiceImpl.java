package com.example.microservice.user_service.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendActivationEmail(String toEmail, String orgName, String activationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Activate Your Investment System Account");

            String activationLink = frontendUrl + "/api/auth/activate?token=" + activationToken;
            
            String htmlContent = buildActivationEmailContent(orgName, activationLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Activation email sent successfully to: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("Failed to send activation email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send activation email", e);
        }
    }

    private String buildActivationEmailContent(String orgName, String activationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .button { 
                            display: inline-block; 
                            padding: 12px 30px; 
                            background-color: #4CAF50; 
                            color: white; 
                            text-decoration: none; 
                            border-radius: 5px;
                            margin: 20px 0;
                        }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to Investment System!</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <p>Thank you for registering with Investment System. To complete your registration and activate your account, please click the button below:</p>
                            <div style="text-align: center;">
                                <a href="%s" class="button">Activate Account</a>
                            </div>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all; color: #4CAF50;">%s</p>
                            <p><strong>Important:</strong> This activation link will expire in 24 hours.</p>
                            <p>If you didn't register for an account, please ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Investment System. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(orgName, activationLink, activationLink);
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password");

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            
            String htmlContent = buildPasswordResetEmailContent(resetLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetEmailContent(String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #FF5722; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .button { 
                            display: inline-block; 
                            padding: 12px 30px; 
                            background-color: #FF5722; 
                            color: white; 
                            text-decoration: none; 
                            border-radius: 5px;
                            margin: 20px 0;
                        }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Password Reset Request</h1>
                        </div>
                        <div class="content">
                            <h2>Reset Your Password</h2>
                            <p>We received a request to reset your password. Click the button below to create a new password:</p>
                            <div style="text-align: center;">
                                <a href="%s" class="button">Reset Password</a>
                            </div>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all; color: #FF5722;">%s</p>
                            <p><strong>Important:</strong> This link will expire in 1 hour.</p>
                            <p>If you didn't request a password reset, please ignore this email and your password will remain unchanged.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Investment System. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetLink, resetLink);
    }

    @Async
    public void sendAccountCredentialsEmail(String toEmail, String orgName, String temporaryPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your Investment System Account Has Been Created");

            String htmlContent = buildAccountCredentialsEmailContent(orgName, toEmail, temporaryPassword);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Account credentials email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send account credentials email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send account credentials email", e);
        }
    }

    private String buildAccountCredentialsEmailContent(String orgName, String email, String temporaryPassword) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; }
                        .credentials { 
                            background-color: white; 
                            border-left: 4px solid #2196F3; 
                            padding: 15px; 
                            margin: 20px 0;
                        }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                        .warning { 
                            background-color: #fff3cd; 
                            border-left: 4px solid #ffc107; 
                            padding: 10px; 
                            margin: 20px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to Investment System!</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <p>An administrator has created an account for you on the Investment System. Below are your login credentials:</p>
                            <div class="credentials">
                                <p><strong>Email:</strong> %s</p>
                                <p><strong>Temporary Password:</strong> <code style="background-color: #f0f0f0; padding: 2px 6px; border-radius: 3px;">%s</code></p>
                            </div>
                            <div class="warning">
                                <p><strong>⚠️ Important Security Notice:</strong></p>
                                <ul>
                                    <li>This is a temporary password</li>
                                    <li>Please change your password after your first login</li>
                                    <li>Do not share your password with anyone</li>
                                </ul>
                            </div>
                            <p>You can now log in to your account using these credentials.</p>
                            <p>If you have any questions or did not expect to receive this email, please contact your administrator.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Investment System. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(orgName, email, temporaryPassword);
    }
}
