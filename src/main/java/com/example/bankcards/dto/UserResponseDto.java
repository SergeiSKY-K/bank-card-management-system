package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String username;
    private Role role;
}