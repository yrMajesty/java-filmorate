package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
        film = new Film(null, "Название фильма", "Описание фильма", LocalDate.now(), 60);
        filmController = new FilmController(new FilmRepository());
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
    void addFilm_validationException_filmAlreadyExist() {
        filmController.createFilm(film);
        Film newFilm = new Film(null, "Название фильма", "Описание фильма", LocalDate.now(), 60);
        assertThrows(ValidationException.class, () -> filmController.createFilm(newFilm),
                "Нет ошибки при создании фильма, который уже есть в базе");
    }

    @Test
    void updateFilm_validationException_filmIdIsNull() {
        filmController.createFilm(film);
        Film newFilm = new Film(null, "Название фильма", "Описание фильма", LocalDate.now(), 60);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm),
                "Нет ошибки при обновлении фильма, id которого null");
    }

    @Test
    void updateFilm_validationException_filmIdIsIncorrect() {
        filmController.createFilm(film);
        Film newFilm = new Film(-5L, "Title film", "Description film", LocalDate.now(), 60);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm),
                "Нет ошибки при обновлении фильма с некорректным id");
    }

    @Test
    void updateFilm_validationException_filmIdIsNotExist() {
        filmController.createFilm(film);
        Film newFilm = new Film(null, "Название фильма", "Описание фильма", LocalDate.now(), 60);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(newFilm),
                "Нет ошибки при обновлении фильма, id которого не найден в базе");
    }
}
