package com.bhojrajCreation.journalApp.Service;

import com.bhojrajCreation.journalApp.Services.EmailService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class EmailServiceTest {
    @Autowired
    private EmailService emailService;

    @Test
    public void sendMailTest(){
        emailService.sendMail("bhojrajmca24@gmail.com","Java mail sender","hi there, hope this mail finds you well");
    }
}
