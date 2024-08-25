package ru.kolobkevic.tasktracker.converter;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.kolobkevic.tasktracker.dto.TaskResponse;
import ru.kolobkevic.tasktracker.model.Task;

@Component
@RequiredArgsConstructor
public class TaskConverter {
    private final ModelMapper modelMapper;

    public TaskResponse toResponse(Task task) {
        return modelMapper.map(task, TaskResponse.class);
    }
}
