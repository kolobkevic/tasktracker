package ru.kolobkevic.tasktracker.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TaskTrackerFrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerFrontApplication.class, args);
    }

}
