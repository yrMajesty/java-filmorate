package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao extends Repository<Film, Long> {

    List<Film> findPopularFilms(Integer count);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

}