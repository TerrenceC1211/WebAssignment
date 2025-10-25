package com.CwY.WebAssignment.service;

import com.CwY.WebAssignment.dto.RegisterRequest;
import com.CwY.WebAssignment.model.Role;
import com.CwY.WebAssignment.model.User;
import com.CwY.WebAssignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User createUser(User user){
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = Optional.ofNullable(user.getRole()).orElse(Role.STUDENT);

        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                authorities
        );
    }

}
