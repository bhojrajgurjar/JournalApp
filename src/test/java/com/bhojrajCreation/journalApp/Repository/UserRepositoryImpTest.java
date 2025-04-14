package com.bhojrajCreation.journalApp.Repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class UserRepositoryImpTest {
    @Autowired
    private UserRepositoryImp userRepositoryImp;

    @Test
    public void toSaveUser(){
        userRepositoryImp.getUserForSA();
    }
}
