package com.code.camping.controller;

import com.code.camping.entity.User;
import com.code.camping.service.UserService;
import com.code.camping.utils.dto.request.RegisterUserRequest;
import com.code.camping.utils.dto.response.UserResponse;
import com.code.camping.utils.dto.webResponse.PageResponse;
import com.code.camping.utils.dto.webResponse.WebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by ID")
    @PreAuthorize("hasRole('USER') and #id == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WebResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable String id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse response = UserResponse.fromUser(userService.getById(id));
        
        return ResponseEntity.ok(WebResponse.<UserResponse>builder()
                .data(response)
                .message("User retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination and filtering")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponse<PageResponse<User>>> getAllUsers(
            @Parameter(description = "Pagination and sorting") 
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable page,
            @Parameter(description = "Search criteria") @ModelAttribute RegisterUserRequest searchCriteria) {
        
        log.info("Fetching users with page: {}, size: {}", page.getPageNumber(), page.getPageSize());
        Page<User> userPage = userService.getAll(page, searchCriteria);
        PageResponse<User> response = new PageResponse<>(userPage);
        
        return ResponseEntity.ok(WebResponse.<PageResponse<User>>builder()
                .data(response)
                .message("Users retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    @PreAuthorize("hasRole('USER') and #id == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WebResponse<UserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable String id,
            @Valid @RequestBody RegisterUserRequest request) {
        
        log.info("Updating user with ID: {}", id);
        request.setId(id);
        User updatedUser = userService.update(request);
        UserResponse response = UserResponse.fromUser(updatedUser);
        
        return ResponseEntity.ok(WebResponse.<UserResponse>builder()
                .data(response)
                .message("User updated successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete user by ID")
    @PreAuthorize("hasRole('USER') and #id == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WebResponse<String>> deleteUser(
            @Parameter(description = "User ID") @PathVariable String id) {
        
        log.info("Deleting user with ID: {}", id);
        userService.delete(id);
        
        return ResponseEntity.ok(WebResponse.<String>builder()
                .data("User deleted successfully")
                .message("User deleted successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
}
