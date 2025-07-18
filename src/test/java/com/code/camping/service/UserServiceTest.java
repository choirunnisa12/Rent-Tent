package com.code.camping.service;

import com.code.camping.entity.User;
import com.code.camping.exception.ResourceNotFoundException;
import com.code.camping.repository.UserRepository;
import com.code.camping.security.JwtUtils;
import com.code.camping.service.impl.UserServiceImpl;
import com.code.camping.utils.dto.request.LoginUserRequest;
import com.code.camping.utils.dto.request.RegisterUserRequest;
import com.code.camping.utils.dto.response.LoginUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private RegisterUserRequest registerRequest;
    private LoginUserRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword")
                .build();

        registerRequest = new RegisterUserRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginUserRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void createUser_Success() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.create(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void createUser_UserAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(testUser);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.create(registerRequest));
        verify(userRepository).findByEmail(registerRequest.getEmail());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void loginUser_Success() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateAccessToken(any(User.class))).thenReturn("jwtToken");

        // When
        LoginUserResponse result = userService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwtToken", result.getAccessToken());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtUtils).generateAccessToken(testUser);
    }

    @Test
    void loginUser_InvalidCredentials_ReturnsEmptyToken() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When
        LoginUserResponse result = userService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("", result.getAccessToken());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtUtils, never()).generateAccessToken(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getById(testUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getById("non-existent-id"));
        verify(userRepository).findById("non-existent-id");
    }

    @Test
    void getAllUsers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);
        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        // When
        Page<User> result = userService.getAll(pageable, registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUser, result.getContent().get(0));
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void updateUser_Success() {
        // Given
        registerRequest.setId(testUser.getId());
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.update(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findById(testUser.getId());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Given
        registerRequest.setId("non-existent-id");
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.update(registerRequest));
        verify(userRepository).findById("non-existent-id");
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(anyString());

        // When
        userService.delete(testUser.getId());

        // Then
        verify(userRepository).findById(testUser.getId());
        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.delete("non-existent-id"));
        verify(userRepository).findById("non-existent-id");
        verify(userRepository, never()).deleteById(anyString());
    }
} 