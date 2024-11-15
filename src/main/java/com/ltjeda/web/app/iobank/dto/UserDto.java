package com.ltjeda.web.app.iobank.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private String firstName;
    private String lastName;
    private String username;
    private LocalDate birthDate;
    private long telephone;
    private String password;
    private String gender;


}
