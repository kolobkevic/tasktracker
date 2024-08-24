package ru.kolobkevic.tasktracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.kolobkevic.tasktracker.dto.TaskRequest;
import ru.kolobkevic.tasktracker.dto.TaskResponse;
import ru.kolobkevic.tasktracker.model.Task;
import ru.kolobkevic.tasktracker.model.User;
import ru.kolobkevic.tasktracker.repository.TaskRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskResponse create(UserDetails userDetails, TaskRequest taskRequest) {
        Task task = new Task();
        String head = taskRequest.getHead().isBlank()
                ? "Untitled"
                : taskRequest.getHead();
        User user = userService.getUserByUsername(userDetails.getUsername());

        task.setHead(head);
        task.setContent(taskRequest.getContent());
        task.setStatus(taskRequest.getStatus());
        task.setOwner(user);
        task.setCreatedAt(Date.from(Instant.now()));

        return convertTaskToResponse(taskRepository.save(task));
    }

    public TaskResponse update(TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskRequest.getId()).orElseThrow();
        task.setContent(taskRequest.getContent());
        task.setHead(taskRequest.getHead());
        task.setStatus(taskRequest.getStatus());

        return convertTaskToResponse(taskRepository.save(task));
    }

    public void delete(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
    }

    public List<TaskResponse> findAll() {
        List<TaskResponse> taskResponses = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        taskRepository.findAll().forEach(tasks::add);
        for (Task task : tasks) {
            TaskResponse taskResponse = new TaskResponse();
            taskResponse.setId(task.getId());
            taskResponse.setHead(task.getHead());
            taskResponse.setContent(task.getContent());
            taskResponse.setStatus(task.getStatus());
            taskResponse.setCreatedAt(task.getCreatedAt());
            taskResponse.setDoneAt(task.getDoneAt());
            taskResponses.add(taskResponse);
        }
        return taskResponses;
    }

    private TaskResponse convertTaskToResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setHead(task.getHead());
        taskResponse.setContent(task.getContent());
        taskResponse.setStatus(task.getStatus());
        return taskResponse;
    }
}
