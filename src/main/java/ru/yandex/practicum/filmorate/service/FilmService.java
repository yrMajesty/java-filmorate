package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmDao filmRepository;

    public List<Film> getAllFilms() {
        log.info("Request get all films");
        return filmRepository.findAll();
    }

    public Film getFilmById(Long id) {
        log.info("Request get film by id='{}'", id);
        return filmRepository.findById(id);
    }

    public Film createFilm(Film film) {
        log.info("Request create new Film {}", film);
        return filmRepository.save(film);
    }

    public Film updateFilm(Film film) {
        log.info("Request update film");
        if (film.getId() == null || film.getId() <= 0) {
            log.error("Invalid id='{}' of updatable user", film.getId());
            throw new javax.validation.ValidationException("Невалидный id='" + film.getId() + "' обновляемого пользователя");
        }
        return filmRepository.update(film);
    }

    public void addLikeByFilmId(Long filmId, Long userId) {
        log.info("Request add like user with id='{}' to film by id='{}'", userId, filmId);
        filmRepository.addLike(filmId, userId);
    }

    public void deleteLikeByFilmId(Long filmId, Long userId) {
        log.info("Request delete like user with id='{}' to film by id='{}'", userId, filmId);
        filmRepository.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Request get popular films");
        return filmRepository.findPopularFilms(count);
    }
}
