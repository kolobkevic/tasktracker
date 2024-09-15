package ru.kolobkevic.tasktracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.kolobkevic.tasktracker.converter.TaskConverter;
import ru.kolobkevic.tasktracker.dto.TaskRequest;
import ru.kolobkevic.tasktracker.dto.TaskResponse;
import ru.kolobkevic.tasktracker.exception.ObjectAlreadyExistsException;
import ru.kolobkevic.tasktracker.exception.TaskNotFoundException;
import ru.kolobkevic.tasktracker.model.Task;
import ru.kolobkevic.tasktracker.model.TaskStatus;
import ru.kolobkevic.tasktracker.model.User;
import ru.kolobkevic.tasktracker.repository.TaskRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableCaching
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskConverter taskConverter;

    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponse create(String username, TaskRequest taskRequest) {
        try {
            Task task = new Task();
            String head = taskRequest.getTitle().isBlank()
                    ? "Untitled"
                    : taskRequest.getTitle();
            User user = userService.getUserByUsername(username);

            task.setTitle(head);
            task.setContent(taskRequest.getContent());
            task.setStatus(TaskStatus.valueOf(taskRequest.getStatus().toUpperCase()));
            task.setUser(user);
            task.setCreatedAt(Date.from(Instant.now()));
            if (taskRequest.getStatus().toUpperCase().equals(TaskStatus.DONE.toString())) {
                task.setDoneAt(Date.from(Instant.now()));
            }

            return taskConverter.toResponse(taskRepository.save(task));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new ObjectAlreadyExistsException();
            }
        }
        return new TaskResponse();
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponse update(String username, TaskRequest taskRequest) {
        try {
            Task task = taskRepository.findByIdAndUser(taskRequest.getId(), userService.getUserByUsername(username))
                    .orElseThrow(TaskNotFoundException::new);
            task.setContent(taskRequest.getContent());
            task.setTitle(taskRequest.getTitle());

            if (taskRequest.getStatus().toUpperCase().equals(TaskStatus.DONE.toString()) &&
                    (task.getStatus() == TaskStatus.IN_WORK)) {
                task.setDoneAt(Date.from(Instant.now()));
            } else if (taskRequest.getStatus().toUpperCase().equals(TaskStatus.IN_WORK.toString()) &&
                    task.getStatus().equals(TaskStatus.DONE)) {
                task.setDoneAt(null);
            }
            task.setStatus(TaskStatus.valueOf(taskRequest.getStatus().toUpperCase()));
            return taskConverter.toResponse(taskRepository.save(task));

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new ObjectAlreadyExistsException();
            }
        }
        return new TaskResponse();
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void delete(String username, Long id) {
        Task task = taskRepository.findByIdAndUser(id, userService.getUserByUsername(username))
                .orElseThrow(TaskNotFoundException::new);
        taskRepository.delete(task);
    }

    @Cacheable("tasks")
    public List<TaskResponse> findAll(String username) {
        List<TaskResponse> taskResponses = new ArrayList<>();
        List<Task> tasks = new ArrayList<>(taskRepository.findAllByUser(userService.getUserByUsername(username)));
        for (Task task : tasks) {
            taskResponses.add(taskConverter.toResponse(task));
        }
        return taskResponses;
    }
}
