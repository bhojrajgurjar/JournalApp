package com.bhojrajCreation.journalApp.SchedullerTesting;

import com.bhojrajCreation.journalApp.Scheduller.UserScheduller;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class UserSchedullerTest {
    @Autowired
    private UserScheduller userScheduller;
    @Test
    public void fetchUserSAMailTest(){
        userScheduller.fetchUserSAMail();
    }
}
