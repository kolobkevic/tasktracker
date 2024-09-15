package ru.kolobkevic.tasktracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kolobkevic.tasktracker.model.User;
import ru.kolobkevic.tasktracker.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@EnableCaching
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);

        return getUserDetails(user);
    }

    @Cacheable("users")
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unable to find user with username: " + username));
    }

    public UserDetails getUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
