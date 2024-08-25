package ru.kolobkevic.tasktracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private Long id;
    private String title = "";
    private String content = "";
    private String status = "";
}
