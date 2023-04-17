package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class MemoryFilmRepository implements FilmRepository {

    private final UserRepository memoryUserRepository;

    private long idFilm = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> save(Film film) {
        film.setId(++idFilm);
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> update(Film film) {
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        return films.values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUserLikes().size(), o1.getUserLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film foundedFilm = films.get(filmId);
        if (foundedFilm == null) {
            throw new NoSuchElementException("Film with id='" + filmId + "' not found");
        }
        Optional<User> optionalUser = memoryUserRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id='" + userId + "' not found");
        }

        foundedFilm.getUserLikes().add(optionalUser.get().getId());
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film foundedFilm = films.get(filmId);
        if (foundedFilm == null) {
            throw new NoSuchElementException("Film with id='" + filmId + "' not found");
        }
        Optional<User> optionalUser = memoryUserRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id='" + userId + "' not found");
        }

        foundedFilm.getUserLikes().remove(optionalUser.get().getId());
    }

    public boolean existsByUniqueFields(Film film) {
        return films.values()
                .stream()
                .anyMatch(filmSaved -> (filmSaved.equals(film)));
    }
}
