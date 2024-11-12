package ru.kolobkevic.tasktracker.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kolobkevic.tasktracker.scheduler.dto.EmailSendingDto;
import ru.kolobkevic.tasktracker.scheduler.model.Task;
import ru.kolobkevic.tasktracker.scheduler.model.TaskStatus;
import ru.kolobkevic.tasktracker.scheduler.model.User;
import ru.kolobkevic.tasktracker.scheduler.repository.TaskRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.kolobkevic.tasktracker.scheduler.config.KafkaTopicConfig.EMAIL_SENDING_TASKS;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    @Value("${tasksLimitForReport}")
    private int tasksLimitForReport;
    private Date today;
    private Date yesterday;

    private final TaskRepository taskRepository;
    private final KafkaTemplate<String, EmailSendingDto> kafkaTemplate;

    private List<Task> getAllTasksByDoneAt(Date start, Date end) {
        return taskRepository.findAllByDoneAtBetweenOrInWorkStatus(start, end);
    }

    Map<User, List<Task>> getAllUserTasks(Date start, Date end) {
        List<Task> tasks = getAllTasksByDoneAt(start, end);
        if (tasks.isEmpty()) {
            return new HashMap<>();
        }
        User user = tasks.get(0).getUser();
        List<Task> userTasks = new ArrayList<>();
        for (Task task : tasks) {
            log.info("Task {}, status {}", task.toString(), task.getStatus());
        }

        Map<User, List<Task>> result = new HashMap<>();
        for (Task task : tasks) {
            if (user == task.getUser()) {
                userTasks.add(task);
            } else {
                user = task.getUser();
                userTasks.clear();
                userTasks.add(task);
            }
            result.put(task.getUser(), userTasks);
        }
        return result;
    }

    @Scheduled(cron = "@daily")
//    @Scheduled(fixedRate = 10000)
    public void sendReports() {
        today = Date.from(Instant.now());
        yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        Map<User, List<Task>> userTasks = getAllUserTasks(yesterday, today);

        if (!userTasks.isEmpty()) {
            var entries = userTasks.entrySet();
            entries.forEach(entry ->
                    kafkaTemplate.send(EMAIL_SENDING_TASKS, getEmailSendingDto(entry.getValue())));
        }
    }

    private long countNotFinishedTasks(List<Task> tasks) {
        return tasks.stream().filter(task -> task.getStatus().equals(TaskStatus.IN_WORK)).count();
    }

    long countFinishedTasksByDay(List<Task> tasks, Date date) {
        return tasks.stream().filter(
                task -> task.getStatus().equals(TaskStatus.DONE) &&
                        task.getDoneAt().after(date) && task.getDoneAt().before(Date.from(Instant.now()))).count();
    }

    EmailSendingDto getEmailSendingDto(List<Task> tasks) {
        long finishedTasks;
        long unFinishedTasks;
        StringBuilder title = new StringBuilder();
        StringBuilder unDoneSubtitle = new StringBuilder();
        StringBuilder doneSubtitle = new StringBuilder();
        StringBuilder content = new StringBuilder("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Email</title>
                </head>
                <body>
                """);

        today = Date.from(Instant.now());
        yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        if (countNotFinishedTasks(tasks) > 0) {
            unFinishedTasks = countNotFinishedTasks(tasks);
            unDoneSubtitle.append("У вас осталось ").append(unFinishedTasks).append(" несделанных задач");
            title.append(unDoneSubtitle).append(".").append(System.lineSeparator());
            content.append("<h3>").append(unDoneSubtitle).append(":</h3><h5>");
            tasks.stream().filter(
                            task -> task.getStatus().equals(TaskStatus.IN_WORK))
                    .limit(tasksLimitForReport)
                    .forEach(task -> content.append(task.getTitle()).append(" "));
            content.append("</h5><p></p>");
        }

        if (countFinishedTasksByDay(tasks, yesterday) > 0) {
            finishedTasks = countFinishedTasksByDay(tasks, yesterday);
            doneSubtitle.append("За сегодня вы выполнили ").append(finishedTasks).append(" задач");
            title.append(doneSubtitle).append(".").append(System.lineSeparator());
            content.append("<h3>").append(doneSubtitle).append(":</h3><h5>");
            tasks.stream().filter(
                            task -> task.getStatus().equals(TaskStatus.DONE) &&
                                    task.getDoneAt().after(yesterday) && task.getDoneAt().before(today))
                    .limit(tasksLimitForReport)
                    .forEach(task -> content.append(task.getTitle())
                            .append(" "));
            content.append("</h5>");
        }

        content.append("""
                </body>
                </html>""");
        EmailSendingDto emailSendingDto = new EmailSendingDto();
        emailSendingDto.setEmail(tasks.get(0).getUser().getEmail());
        emailSendingDto.setTitle(title.toString());
        emailSendingDto.setContent(content.toString());
        return emailSendingDto;
    }
}
