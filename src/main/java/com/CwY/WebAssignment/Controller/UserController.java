package com.CwY.WebAssignment.Controller;

import com.CwY.WebAssignment.dto.RegisterRequest;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Create/Register user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            User createdUser = userService.registerNewUser(request);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Invalid request");
        return Map.of("message", message);
    }


    // Get user by username
    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String userName) {
            return userService.findByUsername(userName).orElseThrow(() -> new RuntimeException("Username not found"));
    }

    // Check if username exists
    @GetMapping("/exists/{userName}")
    public boolean checkIfUsernameExists(@PathVariable String userName) {
        return userService.existsByUsername(userName);
    }
}
