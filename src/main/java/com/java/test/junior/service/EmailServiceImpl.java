package com.java.test.junior.service;

import com.java.test.junior.model.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;


@Service
@Log
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender mailSender;

    @Override
    public ResponseEntity<Response> send(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Reset password email sent", null));
    }
}
