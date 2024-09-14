package ru.kolobkevic.tasktracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kolobkevic.tasktracker.dto.JwtAuthenticationResponse;
import ru.kolobkevic.tasktracker.dto.SignInRequest;
import ru.kolobkevic.tasktracker.dto.SignUpRequest;
import ru.kolobkevic.tasktracker.exception.InvalidArgumentsException;
import ru.kolobkevic.tasktracker.service.AuthService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<JwtAuthenticationResponse> register(@RequestBody @Valid SignUpRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidArgumentsException(getAllErrorMessages(bindingResult));
        }
        JwtAuthenticationResponse response = authService.signUp(request);
        log.info(response.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authentication")
    public ResponseEntity<JwtAuthenticationResponse> authenticate(@RequestBody @Valid SignInRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidArgumentsException(getAllErrorMessages(bindingResult));
        }
        JwtAuthenticationResponse response;
        response = authService.signIn(request);
        log.info(response.getToken());
        return ResponseEntity.ok(response);
    }

    private String getAllErrorMessages(BindingResult bindingResult) {
        StringBuilder result = new StringBuilder();
        bindingResult.getAllErrors().forEach(error -> result.append(
                error.getDefaultMessage()).append(System.lineSeparator()));
        result.delete(result.length() - 1, result.length());
        return result.toString();
    }
}
