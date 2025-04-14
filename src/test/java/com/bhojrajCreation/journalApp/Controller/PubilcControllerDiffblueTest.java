package com.bhojrajCreation.journalApp.Controller;

import com.bhojrajCreation.journalApp.Services.UserDetailsServiceImp;
import com.bhojrajCreation.journalApp.Services.UserService;
import com.bhojrajCreation.journalApp.Utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {PubilcController.class})
@ExtendWith(SpringExtension.class)
class PubilcControllerDiffblueTest {
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private PubilcController pubilcController;

    @MockBean
    private UserDetailsServiceImp userDetailsServiceImp;

    @MockBean
    private UserService userService;

    /**
     * Method under test: {@link PubilcController#welcomePage()}
     */
    @Test
    void testWelcomePage() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/public/welcome");
        MockMvcBuilders.standaloneSetup(pubilcController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("welcome"));
    }

    /**
     * Method under test: {@link PubilcController#welcomePage()}
     */
    @Test
    void testWelcomePage2() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/public/welcome", "Uri Variables");
        MockMvcBuilders.standaloneSetup(pubilcController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("welcome"));
    }
}
