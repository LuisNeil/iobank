package com.ltjeda.web.app.iobank.service;

import com.ltjeda.web.app.iobank.dto.UserDto;
import com.ltjeda.web.app.iobank.entity.User;
import com.ltjeda.web.app.iobank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User registerUser(UserDto userDto){
        User user = mapToUser(userDto);
        return userRepository.save(user);
    }

    public Map<String, Object> authenticateUser(UserDto userDto){
        Map<String, Object> authObject = new HashMap<>();
        User user = (User) userDetailsService.loadUserByUsername(userDto.getUsername());
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password");
        }
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        authObject.put("token", "Bearer  " + jwtService.generateToken(userDto.getUsername()));
        authObject.put("user", user);
        return authObject;
    }


    private User mapToUser(UserDto userDto){
        return User.builder()
                .lastName(userDto.getLastName())
                .firstName(userDto.getFirstName())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .telephone(userDto.getTelephone())
                .birthDate(userDto.getBirthDate())
                .roles(List.of("USER"))
                .tag("io " + userDto.getUsername())
                .build();
    }
}
