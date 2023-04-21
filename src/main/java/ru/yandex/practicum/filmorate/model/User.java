package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    @Email(message = "Email должен иметь формат адреса электронной почты")
    @NotBlank(message = "Email не может быть пустым или равным null")
    private String email;

    @NotBlank(message = "Логин не может быть пустым или равным null")
    @Pattern(regexp = "[A-Za-z0-9._]{5,15}",
            message = "Пароль должен содержать от 5 до 15 символов и состоять из заглавных, строчных букв и цифр")
    private String login;

    @PastOrPresent(message = "Дата рождения должна быть в прошедшем времени, не в будущем")
    private LocalDate birthday;

    @JsonIgnore
    private final Set<Long> friends = new HashSet<>();

}
