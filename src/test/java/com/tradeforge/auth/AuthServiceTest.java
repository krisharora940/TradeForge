package com.tradeforge.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tradeforge.exceptions.ConflictException;
import com.tradeforge.security.JwtService;
import com.tradeforge.security.TradeForgeUserDetailsService;
import com.tradeforge.users.UserEntity;
import com.tradeforge.users.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TradeForgeUserDetailsService userDetailsService;

    @Test
    void registerHashesPasswordAndReturnsBearerToken() {
        UserDetails userDetails = userDetails("test@example.com");
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("hashed-password");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        AuthService authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                userDetailsService
        );

        AuthResponse response = authService.register(new RegisterRequest(" Test@Example.com ", "Password123!"));

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("test@example.com");
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hashed-password");
        assertThat(response).isEqualTo(new AuthResponse("jwt-token", "Bearer"));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);
        AuthService authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                userDetailsService
        );

        assertThatThrownBy(() -> authService.register(new RegisterRequest("test@example.com", "Password123!")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void loginAuthenticatesThenReturnsBearerToken() {
        UserDetails userDetails = userDetails("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        AuthService authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                userDetailsService
        );

        AuthResponse response = authService.login(new LoginRequest("test@example.com", "Password123!"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response).isEqualTo(new AuthResponse("jwt-token", "Bearer"));
    }

    private UserDetails userDetails(String email) {
        return User.withUsername(email)
                .password("hash")
                .authorities("ROLE_USER")
                .build();
    }
}
