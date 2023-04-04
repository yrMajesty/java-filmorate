package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Null
    private Long id;

    @Email
    @NotBlank(message = "Email не может быть пустым или равным null")
    private String email;

    @NotBlank(message = "Логин не может быть пустым или равным null")
    @Pattern(regexp = "[A-Za-z0-9._]{5,15}")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения должна быть в прошедшем времени, не в будущем")
    private LocalDate birthday;

}
