package ru.kolobkevic.tasktracker.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kolobkevic.tasktracker.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    void deleteByUsername (String username);
}
