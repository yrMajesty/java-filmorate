package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.ExistElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.repository.UserDao;
import ru.yandex.practicum.filmorate.repository.impl.memory.MemoryFilmRepository;
import ru.yandex.practicum.filmorate.repository.impl.memory.MemoryUserRepository;
import ru.yandex.practicum.filmorate.service.FilmService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class FilmControllerTest {

    private FilmController filmController;
    private Film film;
    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void init() {
        UserDao userRepository = new MemoryUserRepository();
        FilmDao filmRepository = new MemoryFilmRepository(userRepository);
        film = Film.builder()
                .name("Название фильма")
                .description("Описание фильма")
                .releaseDate(LocalDate.now())
                .duration(60)
                .build();
        filmController = new FilmController(new FilmService(filmRepository));
    }

    @Test
    void createFilm_rejectName_nameIsEmpty() {
        film.setName("");
        assertEquals(1, validator.validate(film).size(),
                "Нет ошибки валидации, что поле с названием пустое");
    }

    @Test
    void createFilm_rejectName_nameIsNull() {
        film.setName(null);
        assertEquals(1, validator.validate(film).size(),
                "Нет ошибки валидации, что поле с названием null");
    }

    @Test
    void createFilm_acceptName_nameIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(film).isEmpty(), "Фильм в валидным именем не прошел валидацию");
    }

    @Test
    void createFilm_acceptDescription_descriptionIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(film).isEmpty(), "Фильм в валидным описанием не прошел валидацию");
    }

    @Test
    void createFilm_rejectDescription_descriptionIsNull() {
        film.setDescription(null);
        assertEquals(1, validator.validate(film).size(),
                "Нет ошибки валидации, что поле с описанием null");
    }

    @Test
    void createFilm_rejectDescription_descriptionIsEmpty() {
        film.setDescription("");
        assertEquals(1, validator.validate(film).size(),
                "Нет ошибки валидации, что поле с описанием пустое");
    }

    @Test
    void createFilm_rejectDescription_descriptionMinSizeIsLess() {
        film.setDescription("Неполное");
        assertEquals(1, validator.validate(film).size(),
                "Нет ошибки валидации, что поле с описанием пустое");
    }

    @Test
    void createFilm_rejectReleaseDate_releaseDateIsIncorrect() {
        film.setReleaseDate(LocalDate.of(1000, 1, 1));
        assertEquals(1, validator.validate(film).size(),
                "Нет ошибки валидации, что дата релиза меньше установленной");
    }

    @Test
    void createFilm_acceptReleaseDate_releaseDateIsCorrect() {
        assertTrue(validator.validate(film).isEmpty(), "Фильм в валидной датой релиза не прошел валидацию");
    }

    @Test
    void createFilm_acceptDuration_durationIsCorrect() {
        assertTrue(validator.validate(film).isEmpty(), "Фильм в валидной продолжительностью не прошел валидацию");
    }

    @Test
    void addFilm_existFilmException_filmAlreadyExist() {
        filmController.createFilm(film);
        Film newFilm = Film.builder()
                .name("Название фильма")
                .description("Описание фильма")
                .releaseDate(LocalDate.now())
                .duration(60)
                .build();
        assertThrows(ExistElementException.class, () -> filmController.createFilm(newFilm),
                "Нет ошибки при создании фильма, который уже есть в базе");
    }

    @Test
    void updateFilm_validationException_filmIdIsNull() {
        filmController.createFilm(film);
        Film newFilm = Film.builder()
                .id(null)
                .name("Название фильма")
                .description("Описание фильма")
                .releaseDate(LocalDate.now())
                .duration(60)
                .build();
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm),
                "Нет ошибки при обновлении фильма, id которого null");
    }

    @Test
    void updateFilm_validationException_filmIdIsIncorrect() {
        filmController.createFilm(film);
        Film newFilm = Film.builder()
                .id(-5L)
                .name("Title film")
                .description("Description film")
                .releaseDate(LocalDate.now())
                .duration(60)
                .build();
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm),
                "Нет ошибки при обновлении фильма с некорректным id");
    }

    @Test
    void updateFilm_validationException_filmIdIsNotExist() {
        filmController.createFilm(film);
        Film newFilm = Film.builder()
                .name("Название фильма")
                .description("Описание фильма")
                .releaseDate(LocalDate.now())
                .duration(60)
                .build();
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm),
                "Нет ошибки при обновлении фильма, id которого не найден в базе");
    }

}
