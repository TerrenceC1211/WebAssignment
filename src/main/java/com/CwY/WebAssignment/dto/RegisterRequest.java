package com.CwY.WebAssignment.dto;


import com.CwY.WebAssignment.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String password;
    private String email;
}
