package ru.kolobkevic.tasktracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kolobkevic.tasktracker.dto.SignUpRequest;
import ru.kolobkevic.tasktracker.model.User;
import ru.kolobkevic.tasktracker.service.AuthService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private static final String REGISTRATION_PAGE = "auth/registration";
    private static final String LOGIN_PAGE = "auth/login";

    @GetMapping("/login")
    public String showLoginPage() {
        return LOGIN_PAGE;
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return REGISTRATION_PAGE;
    }

    @PostMapping("/registration")
    public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }

        SignUpRequest request = new SignUpRequest(
                user.getUsername(), user.getEmail(),
                user.getPassword(), user.getFirstname(), user.getLastname());

        authService.signUp(request);
        return LOGIN_PAGE;
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("user", new User());
        return LOGIN_PAGE;
    }
}
