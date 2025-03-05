package com.bhojrajCreation.journalApp.Service;

import com.bhojrajCreation.journalApp.Repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Disabled
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Test
    public void testFindByUsername(){

        assertEquals(4,2+2);
        assertNotNull(userRepository.findByUsername("Arjun"));
    }
}
