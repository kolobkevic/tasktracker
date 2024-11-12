package ru.kolobkevic.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import ru.kolobkevic.tasktracker.converter.TaskConverter;
import ru.kolobkevic.tasktracker.dto.TaskRequest;
import ru.kolobkevic.tasktracker.dto.TaskResponse;
import ru.kolobkevic.tasktracker.exception.ObjectAlreadyExistsException;
import ru.kolobkevic.tasktracker.exception.TaskNotFoundException;
import ru.kolobkevic.tasktracker.model.Task;
import ru.kolobkevic.tasktracker.model.TaskStatus;
import ru.kolobkevic.tasktracker.model.User;
import ru.kolobkevic.tasktracker.repository.TaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskConverter taskConverter;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;
    private TaskRequest taskRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("testUser");

        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setContent("Content of test task");
        testTask.setStatus(TaskStatus.IN_WORK);
        testTask.setUser(testUser);

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Updated Task");
        taskRequest.setContent("Updated content");
        taskRequest.setStatus("DONE");

        taskResponse = new TaskResponse();
    }

    @Test
    void create_ReturnsTaskResponse() {
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskConverter.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.create(testUser.getUsername(), taskRequest);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void create_ThrowsObjectAlreadyExistsException() {
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(taskRepository.save(any(Task.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(ObjectAlreadyExistsException.class, () -> taskService.create(testUser.getUsername(), taskRequest));
    }

    @Test
    void update_ReturnsUpdatedTaskResponse() {
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(taskRepository.findByIdAndUser(taskRequest.getId(), testUser)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskConverter.toResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.update(testUser.getUsername(), taskRequest);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void update_ThrowsTaskNotFoundException() {
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(taskRepository.findByIdAndUser(taskRequest.getId(), testUser)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.update(testUser.getUsername(), taskRequest));
    }

    @Test
    void delete_TaskDeletedSuccessfully() {
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));

        taskService.delete(testUser.getUsername(), 1L);

        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void delete_ThrowsTaskNotFoundException() {
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.delete(testUser.getUsername(), 1L));
    }

    @Test
    void findAll_ReturnsListOfTaskResponses() {
        when(userService.getUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(taskRepository.findAllByUser(testUser)).thenReturn(Collections.singletonList(testTask));
        when(taskConverter.toResponse(testTask)).thenReturn(taskResponse);

        List<TaskResponse> result = taskService.findAll(testUser.getUsername());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findAllByUser(testUser);
    }
}
