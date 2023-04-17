package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepository extends Repository<Film, Long> {

    List<Film> findPopularFilms(Integer count);

    boolean existsByUniqueFields(Film film);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

}