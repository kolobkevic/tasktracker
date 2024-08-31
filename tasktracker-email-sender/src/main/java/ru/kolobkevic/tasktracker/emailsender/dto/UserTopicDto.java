package ru.kolobkevic.tasktracker.emailsender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTopicDto {
    private String username;
    private String email;
    private String firstname;
    private String lastname;
}
