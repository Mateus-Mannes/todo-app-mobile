package com.todo.todoapi.controller.identity;


import com.todo.todoapi.controller.ApiResponse;
import com.todo.todoapi.domain.identity.JwtService;
import com.todo.todoapi.domain.identity.User;
import com.todo.todoapi.domain.identity.UserService;
import com.todo.todoapi.infrastructure.identity.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IdentityController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public IdentityController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/identity/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        var usuarioExistente = userRepository.findByEmail(user.getEmail());
        if (usuarioExistente.isPresent()) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email já cadastrado");
        }
        userService.saveUser(user);
        return ResponseEntity.ok(new ApiResponse("Usuário registrado com sucesso!", true));
    }

    @PostMapping("/identity/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        user = userRepository.findByEmail(user.getEmail()).orElse(null);
        assert user != null;
        String token = jwtService.generateToken(user);
        Map<String, String> response = new HashMap<>();
        response.put("token", "Bearer " + token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/identity/check")
    public ResponseEntity<?> checkUser() {
        return ResponseEntity.ok(new ApiResponse("Token válido", true));
    }

}
