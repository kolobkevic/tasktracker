package ru.kolobkevic.tasktracker.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kolobkevic.tasktracker.model.Task;
import ru.kolobkevic.tasktracker.model.User;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findAllByUser(User user);
    Optional<Task> findByIdAndUser(Long id, User user);
}
