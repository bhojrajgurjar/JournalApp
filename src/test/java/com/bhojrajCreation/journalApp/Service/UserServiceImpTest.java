package com.bhojrajCreation.journalApp.Service;

import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Repository.UserRepository;
import com.bhojrajCreation.journalApp.Services.UserDetailsServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

    @InjectMocks
    private UserDetailsServiceImp userDetailsServiceImp;

    @Mock
    private UserRepository userRepository;  // Corrected from @MockBean to @Mock

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Ensures mocks are initialized
        Mockito.reset(userRepository);
    }

    @Test
    public void loadUserByUsernameTest() {
        // Mock User entity (assuming it's your custom entity)
        User mockUser = new User();
        mockUser.setUsername("Arjun");
        mockUser.setPassword("anything");
        mockUser.setRoles(Arrays.asList("USER,ADMIN")); // Assuming roles are stored as a string

        when(userRepository.findByUsername("Arjun")).thenReturn(mockUser);

        UserDetails user = userDetailsServiceImp.loadUserByUsername("Arjun");
        assertNotNull(user);
    }
}
