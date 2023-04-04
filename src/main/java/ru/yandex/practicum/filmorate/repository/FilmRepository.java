package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilmRepository {
    private long idFilm = 0;
    private final Map<Long, Film> films = new HashMap<>();

    public void createFilm(Film film) {
        if (films.values()
                .stream()
                .anyMatch(filmSaved -> (filmSaved.equals(film)))) {
            log.error("Film already exist");
            throw new ValidationException("Такой фильм уже существует");
        }

        film.setId(++idFilm);
        films.put(film.getId(), film);
    }

    public void updateFilm(Film film) {
        if (film.getId() == null || film.getId() <= 0) {
            log.error("Id updatable film must not be null or less than 1");
            throw new ValidationException("Id обновляемого фильма не может быть меньше 1 или null");
        }

        if (!films.containsKey(film.getId())) {
            log.error("Film with id='{}' is not exist", film.getId());
            throw new NoSuchFilmException("Фильм с id='" + film.getId() + "' не существует");
        }

        films.put(film.getId(), film);
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }
}
