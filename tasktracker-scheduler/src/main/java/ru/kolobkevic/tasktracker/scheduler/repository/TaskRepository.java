package ru.kolobkevic.tasktracker.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kolobkevic.tasktracker.scheduler.model.Task;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.doneAt BETWEEN :start AND :end OR t.status = 'IN_WORK' ")
    List<Task> findAllByDoneAtBetweenOrInWorkStatus(Date start, Date end);
}
