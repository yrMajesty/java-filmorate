package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

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

    @JsonIgnore
    private final Set<Long> friends = new HashSet<>();

}
