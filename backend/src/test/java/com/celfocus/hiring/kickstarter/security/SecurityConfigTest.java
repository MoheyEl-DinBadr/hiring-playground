package com.celfocus.hiring.kickstarter.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import(TestOAuth2Config.class)
@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_USERNAME = "user1";
    private static final String CART_BASE_URL = "/api/v1/carts";

    @Test
    void cartEndpoint_UnauthenticatedAccess_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(CART_BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cartEndpoint_ValidJwtToken_Success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(CART_BASE_URL)
                        .with(oidcLogin()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .idToken(builder -> builder
                                        .claim("preferred_username", TEST_USERNAME)
                                        .issuedAt(Instant.now())
                                        .expiresAt(Instant.now().plusSeconds(3600)))
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void cartEndpoint_MissingRole_Forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(CART_BASE_URL)
                        .with(oidcLogin()
                                .idToken(builder -> builder
                                        .claim("preferred_username", TEST_USERNAME)
                                        .issuedAt(Instant.now())
                                        .expiresAt(Instant.now().plusSeconds(3600)))
                        )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void cartEndpoint_TokenWithoutPreferredUsername_Forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(CART_BASE_URL)
                        .with(oidcLogin()
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                .idToken(builder -> builder
                                        .issuedAt(Instant.now())
                                        .expiresAt(Instant.now().plusSeconds(3600)))
                        )
                )
                .andExpect(status().isForbidden());
    }

}
