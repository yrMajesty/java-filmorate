package ru.yandex.practicum.filmorate.repository.impl.memory;

import ru.yandex.practicum.filmorate.exception.ExistElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.repository.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemoryFilmRepository implements FilmDao {
    private final UserDao memoryUserRepository;
    private long idFilm = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film save(Film film) {

        if (films.values()
                .stream()
                .anyMatch(filmSaved -> (filmSaved.equals(film)))) {
            log.error("Film same as {} already exists", film);
            throw new ExistElementException("Фильм с такими параметрами уже существует");
        }

        film.setId(++idFilm);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.get(film.getId()) == null) {
            throw new NoSuchElementException("Film with id='" + film.getId() + "' not found");
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteById(Long id) {
        films.remove(id);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long id) {
        Film foundedFilm = films.get(id);
        if (foundedFilm == null) {
            throw new NoSuchElementException("Film with id='" + id + "' not found");
        }
        return foundedFilm;
    }

    public List<Film> findPopularFilms(Integer count) {
        return films.values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUserLikes().size(), o1.getUserLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = memoryUserRepository.findById(userId);

        film.getUserLikes().add(user.getId());
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = memoryUserRepository.findById(userId);

        film.getUserLikes().remove(user.getId());
    }

}
