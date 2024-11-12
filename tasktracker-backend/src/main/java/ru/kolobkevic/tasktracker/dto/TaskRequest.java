package ru.kolobkevic.tasktracker.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Заголовок задачи не может быть пустым")
    private String title;
    @NotBlank(message = "Содержимое задачи не может быть пустым")
    private String content;
    @NotBlank(message = "Статус задачи не может быть пустым")
    private String status;
}
