package com.CwY.WebAssignment.service;

import com.CwY.WebAssignment.model.Role;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.CwY.WebAssignment.dto.RegisterRequest;


import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user){
        // Additional logic like hashing password can be added here
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String userName) {
        return userRepository.findByUserName(userName);
    }

    public boolean existsByUsername(String userName) {
        return userRepository.findByUserName(userName).isPresent();
    }

    //Register new User
    public User registerNewUser(RegisterRequest request){
        if (existsByUsername(request.getUserName() )){
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(Role.STUDENT);

        return userRepository.save(user);
    }

}
