package ru.kolobkevic.tasktracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolobkevic.tasktracker.dto.TaskRequest;
import ru.kolobkevic.tasktracker.dto.TaskResponse;
import ru.kolobkevic.tasktracker.model.Task;
import ru.kolobkevic.tasktracker.model.User;
import ru.kolobkevic.tasktracker.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public void create(User user, TaskRequest taskRequest) {
        Task task = new Task();
        task.setContent(taskRequest.getContent());
        task.setHead(taskRequest.getHead());
        task.setStatus(taskRequest.getStatus());
        task.setOwner(user);

        taskRepository.save(task);
    }

    public void update(TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskRequest.getId()).orElseThrow();
        task.setContent(taskRequest.getContent());
        task.setHead(taskRequest.getHead());
        task.setStatus(taskRequest.getStatus());

        taskRepository.save(task);
    }

    public void delete(TaskRequest taskRequest) {
        Task task = taskRepository.findById(taskRequest.getId()).orElseThrow();
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
}
