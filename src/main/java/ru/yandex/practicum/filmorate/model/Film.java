package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.constraint.FilmDateRelease;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @Null
    private Long id;

    @NotBlank(message = "Названием фильма не может быть пустым или null")
    private String name;

    @Size(min = 10, max = 200, message = "Длина описание фильма должна быть от 10 до 200 символов")
    @NotNull(message = "Поле с описанием фильма не должно быть null")
    private String description;

    @FilmDateRelease(day = 28, month = 12, year = 1895, message = "Дата релиза должна быть позднее 28 Декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должно быть положительным значением")
    private Integer duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(name, film.name)
                && Objects.equals(description, film.description)
                && Objects.equals(releaseDate, film.releaseDate)
                && Objects.equals(duration, film.duration);
    }
}
