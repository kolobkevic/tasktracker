package ru.kolobkevic.tasktracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kolobkevic.tasktracker.dto.JwtAuthenticationResponse;
import ru.kolobkevic.tasktracker.dto.SignInRequest;
import ru.kolobkevic.tasktracker.dto.SignUpRequest;
import ru.kolobkevic.tasktracker.dto.UserTopicDto;
import ru.kolobkevic.tasktracker.model.User;

import static ru.kolobkevic.tasktracker.config.KafkaTopicConfig.USER_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final KafkaTemplate<String, UserTopicDto> kafkaTemplate;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        userService.createUser(user);

        UserTopicDto userTopicDto = new UserTopicDto(
                user.getUsername(), user.getEmail(), user.getFirstname(), user.getLastname());
        log.info("UserTopicDto: {}", userTopicDto);

        kafkaTemplate.send(USER_TOPIC, userTopicDto);

        log.info("Created user username: {}, password: {}", user.getUsername(), user.getPassword());
        return new JwtAuthenticationResponse(jwtService.generateToken(user));
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userService.getUserByUsername(request.getUsername());
        return new JwtAuthenticationResponse(jwtService.generateToken(user));
    }
}
