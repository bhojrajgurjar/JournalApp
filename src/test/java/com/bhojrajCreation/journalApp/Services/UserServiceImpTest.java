package com.bhojrajCreation.journalApp.Services;

import com.bhojrajCreation.journalApp.Repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

    @InjectMocks
    private UserDetailsServiceImp userDetailsServiceImp;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userRepository);
    }

    @Test
    public void loadUserByUsernameTest() {
        when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn((com.bhojrajCreation.journalApp.Entity.User)
                        User.builder().username("Arjun").password("anything").roles(String.valueOf(new ArrayList<>())).build());

        UserDetails user = userDetailsServiceImp.loadUserByUsername("Arjun");
        Assertions.assertNotNull(user);
    }
}
