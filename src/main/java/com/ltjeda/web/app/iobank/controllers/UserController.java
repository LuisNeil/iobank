package com.ltjeda.web.app.iobank.controllers;

import com.ltjeda.web.app.iobank.dto.UserDto;
import com.ltjeda.web.app.iobank.entity.User;
import com.ltjeda.web.app.iobank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.registerUser(userDto));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
       var authObject =  userService.authenticateUser(userDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,(String) authObject.get("token"))
                .body(authObject.get("user"));
    }


}
