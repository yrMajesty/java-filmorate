package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ExistElementException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
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
            throw new ExistElementException("Фильм с такими параметрами уже существует");
        }
        filmRepository.save(film);

        log.info("Successful added new film {}", film);
        return film;
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
            throw new NoSuchElementException("Фильм с id='" + film.getId() + "' не найден");
        }
        log.info("Successful update film {}", film);
        return film;
    }

    public Film getFilmById(Long id) {
        log.info("Request get film by id='{}'", id);
        return filmRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Film with  id='{}' not found", id);
                    throw new NoSuchElementException("Film with id='" + id + "' not found");
                });
    }

    public List<Film> getAllFilms() {
        log.info("Request get all films");
        return filmRepository.findAll();
    }

    public void addLikeByFilmId(Long filmId, Long userId) {
        log.info("Request add like user with id='{}' to film by id='{}'", userId, filmId);
        filmRepository.addLike(filmId, userId);
    }

    public void deleteLikeByFilmId(Long filmId, Long userId) {
        log.info("Request delete like user with id='{}' to film by id='{}'", userId, filmId);
        filmRepository.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmRepository.findPopularFilms(count);
    }

}
