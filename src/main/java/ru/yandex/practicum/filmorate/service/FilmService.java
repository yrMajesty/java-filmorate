package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserService userService;

    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Add new film {}", film);

        if (filmRepository.existsByUniqueFields(film)) {
            log.error("Film same as {} already exists", film);
            throw new ExistFilmException("Фильм с такими параметрами уже существует");
        }

        Optional<Film> optionalFilm = filmRepository.save(film);

        if (optionalFilm.isEmpty()) {
            throw new FilmorateException("Ошибка сохранения");
        }

        log.info("Successful added new film {}", film);
        return optionalFilm.get();
    }

    public Film updateFilm(@RequestBody Film film) {
        log.info("Request update film {}", film);

        if (film.getId() == null || film.getId() <= 0) {
            log.error("Invalid id='{}' of updatable film", film);
            throw new ValidationException("Невалидный id='" + film.getId() + "' обновляемого фильма. " +
                    "Id не может быть меньше 1 или null");
        }

        Optional<Film> filmOptional = filmRepository.findById(film.getId());

        if (filmOptional.isEmpty()) {
            log.error("Film with  id='{}' not found", film);
            throw new NoSuchFilmException("Фильм с id='" + film.getId() + "' не найден");
        }

        Optional<Film> optionalFilm = filmRepository.update(film);

        if (optionalFilm.isEmpty()) {
            throw new FilmorateException("Ошибка сохранения");
        }

        log.info("Successful update film {}", film);
        return optionalFilm.get();
    }

    public Film getFilmById(Long id) {
        log.info("Request get film by id='{}'", id);
        return filmRepository.findById(id).or(
                () -> {
                    log.error("Film with  id='{}' not found", id);
                    throw new NoSuchFilmException("Film with id='" + id + "' not found");
                }).get();
    }

    public List<Film> getAllFilms() {
        log.info("Request get all films");
        return filmRepository.findAll();
    }

    public void addLikeByFilmId(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        film.addLike(userId);
    }

    public void deleteLikeByFilmId(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        film.deleteLike(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmRepository.findPopularFilms(count);
    }

}
