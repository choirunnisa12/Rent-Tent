package com.code.camping.service.impl;

import com.code.camping.entity.User;
import com.code.camping.exception.ResourceNotFoundException;
import com.code.camping.repository.UserRepository;
import com.code.camping.security.JwtUtils;
import com.code.camping.service.UserService;
import com.code.camping.utils.GeneralSpecification;
import com.code.camping.utils.dto.request.LoginUserRequest;
import com.code.camping.utils.dto.request.RegisterUserRequest;
import com.code.camping.utils.dto.response.LoginUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User create(RegisterUserRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }
        
        User newUser = RegisterUserRequest.fromRegisterToUserMapper(request);
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(hashedPassword);
        
        User savedUser = userRepository.saveAndFlush(newUser);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public LoginUserResponse login(LoginUserRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());
        
        LoginUserResponse loginResponse = new LoginUserResponse();
        loginResponse.setAccessToken("");
        
        try {
            User user = userRepository.findByEmail(request.getEmail());
            if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String accessToken = jwtUtils.generateAccessToken(user);
                loginResponse.setAccessToken(accessToken);
                log.info("User logged in successfully: {}", request.getEmail());
            } else {
                log.warn("Login failed for user: {}", request.getEmail());
            }
        } catch (Exception error) {
            log.error("Error during login for user: {}", request.getEmail(), error);
        }
        
        return loginResponse;
    }

    @Override
    public User getById(String id) {
        log.debug("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public Page<User> getAll(Pageable pageable, RegisterUserRequest searchCriteria) {
        log.debug("Fetching users with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Specification<User> specification = GeneralSpecification.getSpecification(searchCriteria);
        return userRepository.findAll(specification, pageable);
    }

    @Override
    public User update(RegisterUserRequest request) {
        log.info("Updating user with ID: {}", request.getId());
        
        User existingUser = userRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getId()));
        
        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            existingUser.setPassword(hashedPassword);
        }

        User updatedUser = userRepository.saveAndFlush(existingUser);
        log.info("User updated successfully: {}", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public void delete(String id) {
        log.info("Deleting user with ID: {}", id);
        User user = getById(id);
        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }
}
