package ru.kolobkevic.tasktracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kolobkevic.tasktracker.dto.JwtAuthenticationResponse;
import ru.kolobkevic.tasktracker.dto.SignUpRequest;
import ru.kolobkevic.tasktracker.service.AuthService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        log.info(request.toString());
        JwtAuthenticationResponse response = authService.signUp(request);
        log.info(response.toString());
        return ResponseEntity.ok(response);
    }
}
