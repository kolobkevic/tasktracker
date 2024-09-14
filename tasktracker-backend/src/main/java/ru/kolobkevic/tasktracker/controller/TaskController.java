package ru.kolobkevic.tasktracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kolobkevic.tasktracker.dto.TaskRequest;
import ru.kolobkevic.tasktracker.dto.TaskResponse;
import ru.kolobkevic.tasktracker.exception.InvalidArgumentsException;
import ru.kolobkevic.tasktracker.service.TaskService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll() {
        List<TaskResponse> allTasks = taskService.findAll();
        return ResponseEntity.ok(allTasks);
    }

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> create(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody @Valid TaskRequest taskRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidArgumentsException(getAllErrorMessages(bindingResult));
        }
        TaskResponse response = taskService.create(userDetails, taskRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<TaskResponse> edit(@RequestBody @Valid TaskRequest taskRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidArgumentsException(getAllErrorMessages(bindingResult));
        }
        TaskResponse response = taskService.update(taskRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private String getAllErrorMessages(BindingResult bindingResult) {
        StringBuilder result = new StringBuilder();
        bindingResult.getAllErrors().forEach(error -> result.append(
                error.getDefaultMessage()).append(System.lineSeparator()));
        result.delete(result.length() - 1, result.length());
        return result.toString();
    }
}
