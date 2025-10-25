package com.CwY.WebAssignment.Controller;

import com.CwY.WebAssignment.dto.RegisterRequest;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Create/Register user
    @PostMapping("/register")
    public User registerUser(@RequestBody RegisterRequest request) {
        return userService.registerNewUser(request);
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
