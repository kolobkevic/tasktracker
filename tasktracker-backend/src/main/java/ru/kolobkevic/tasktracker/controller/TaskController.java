package ru.kolobkevic.tasktracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kolobkevic.tasktracker.dto.TaskRequest;
import ru.kolobkevic.tasktracker.dto.TaskResponse;
import ru.kolobkevic.tasktracker.service.TaskService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<TaskResponse> allTasks = taskService.findAll();
        return ResponseEntity.ok(allTasks);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody TaskRequest taskRequest) {
        TaskResponse response = taskService.create(userDetails, taskRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> edit(@RequestBody TaskRequest taskRequest) {
        TaskResponse response = taskService.update(taskRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
