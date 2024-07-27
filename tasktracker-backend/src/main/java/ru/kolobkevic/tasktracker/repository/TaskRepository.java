package ru.kolobkevic.tasktracker.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kolobkevic.tasktracker.model.Task;
import ru.kolobkevic.tasktracker.model.User;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findAllByOwner(User owner);
}
