package ru.kolobkevic.tasktracker.scheduler.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import ru.kolobkevic.tasktracker.scheduler.dto.EmailSendingDto;
import ru.kolobkevic.tasktracker.scheduler.model.Task;
import ru.kolobkevic.tasktracker.scheduler.model.TaskStatus;
import ru.kolobkevic.tasktracker.scheduler.model.User;
import ru.kolobkevic.tasktracker.scheduler.repository.TaskRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTemplate<String, EmailSendingDto> kafkaTemplate;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("testuser@example.com");

        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.DONE);
        task1.setUser(user);
        task1.setDoneAt(new Date());

        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.IN_WORK);
        task2.setUser(user);
        task2.setDoneAt(new Date());
    }

    @Test
    void testGetAllUserTasks() {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAllByDoneAtBetweenOrInWorkStatus(any(Date.class), any(Date.class))).thenReturn(tasks);

        var result = taskService.getAllUserTasks(new Date(), new Date());

        assertNotNull(result);
        assertTrue(result.containsKey(user));
        assertEquals(2, result.get(user).size());
    }

    @Test
    void testSendReports() {
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAllByDoneAtBetweenOrInWorkStatus(any(Date.class), any(Date.class))).thenReturn(tasks);

        taskService.sendReports();

        verify(kafkaTemplate, times(1)).send(eq("EMAIL_SENDING_TASKS"), any(EmailSendingDto.class));
    }

    @Test
    void testCountFinishedTasksByDay() {
        List<Task> tasks = Arrays.asList(task1, task2);
        Instant now = Instant.now();
        Instant yesterday = now.minus(1, ChronoUnit.DAYS);
        long count = taskService.countFinishedTasksByDay(tasks, Date.from(yesterday));

        assertEquals(1, count);
    }
}
