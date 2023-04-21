package ru.yandex.practicum.filmorate.repository.impl;

import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@RequiredArgsConstructor
@Repository
public class FilmRepository implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    public static final String SELECT_FILM_BY_FILM_ID =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID FROM FILMS f WHERE ID = ?";
    private static final String SELECT_FIND_ALL_FILMS = "SELECT * FROM FILMS LEFT JOIN MPA M on M.ID = FILMS.MPA_ID";
    private static final String SELECT_COUNT_FILM_BY_ID = "SELECT COUNT(*) FROM FILMS WHERE ID = ?";
    private static final String INSERT_NEW_FILM = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_FILM_BY_UNIQUE_FIELD = "SELECT COUNT(*) FROM FILMS WHERE NAME = ? AND DESCRIPTION = ? AND RELEASE_DATE = ? AND DURATION = ?";
    private static final String UPDATE_FILM = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, MPA_ID = ? WHERE ID = ?";
    public static final String SELECT_MPA_BY_FILM_ID = "SELECT m.ID, m.NAME FROM MPA m JOIN FILMS f ON f.MPA_ID = m.ID WHERE f.ID = ?";
    public static final String SELECT_GENRES_BY_FILM_ID = "SELECT g.ID, g.NAME FROM GENRES g JOIN FILMS_GENRES fg ON g.ID = fg.GENRE_ID WHERE fg.FILM_ID = ?";
    public static final String DELETE_FILM_BY_ID = "DELETE FROM FILMS WHERE ID = ?";
    public static final String SELECT_FILMS_USERS_BY_FILM_ID = "SELECT l.USER_ID FROM FILMS f JOIN FILMS_USERS l ON f.ID = l.FILM_ID WHERE f.ID = ?";
    public static final String DELETE_FILMS_USERS_BY_FILM_ID = "DELETE FROM FILMS_USERS WHERE FILM_ID = ?";
    public static final String DELETE_FILMS_GENRES_BY_FILM_ID = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    public static final String INSERT_FILMS_GENRES = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    public static final String INSERT_FILMS_USERS = "INSERT INTO FILMS_USERS (USER_ID, FILM_ID) VALUES (?, ?)";
    public static final String DELETE_FILMS_USERS_BY_FILM_ID_AND_USER_ID = "DELETE FROM FILMS_USERS WHERE FILM_ID = ? AND USER_ID = ?";
    public static final String SELECT_POPULAR_FILMS_LIMIT = "SELECT f.*, COUNT(USER_ID) as count_like FROM FILMS_USERS fu LEFT JOIN FILMS f ON fu.FILM_ID = f.ID GROUP BY fu.FILM_ID  ORDER BY count_like DESC LIMIT ?";
    public static final String SELECT_FILMS_LIMIT = "SELECT * FROM FILMS LIMIT ?";

    @Override
    public Film save(Film film) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(INSERT_NEW_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, kh);

        final Long filmId = kh.getKeyAs(Long.class);
        film.setId(filmId);

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(INSERT_FILMS_GENRES, filmId, genre.getId());
            }
            film.setGenres(getGenresById(filmId));
        }

        Set<Long> likes = film.getUserLikes();
        if (likes != null) {
            for (Long userId : likes) {
                jdbcTemplate.update(INSERT_FILMS_USERS, userId, filmId);
            }
            film.setUserLikes(getLikesByFilmId(filmId));
        }

        if (film.getMpa() != null) {
            film.setMpa(getMpaById(film.getId()));
        }
        return film;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(SELECT_FIND_ALL_FILMS, filmMapper);

        for (Film film : films) {
            film.setMpa(getMpaById(film.getId()));
            film.setGenres(getGenresById(film.getId()));
            film.setUserLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Film findById(Long id) {
        try {
            Optional<Film> film = Optional.ofNullable(jdbcTemplate
                    .queryForObject(SELECT_FILM_BY_FILM_ID, filmMapper, id));
            film.ifPresent(value -> value.setMpa(getMpaById(id)));
            film.ifPresent(value -> value.setGenres(getGenresById(id)));
            film.ifPresent(value -> value.setUserLikes(getLikesByFilmId(id)));
            return film.get();
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundElementException("Film with id='" + id + "' not found");
        }
    }

    @Override
    public Film update(Film film) {
        if (!isExistFilmById(film.getId())) {
            log.error("User with id='{}' already exist", film.getId());
            throw new NotFoundElementException("User with id='" + film.getId() + "' not found");
        }

        int update = jdbcTemplate.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        if (update == 0) {
            return null;
        }

        if (film.getGenres() != null) {
            updateGenres(film);
        }

        if (film.getUserLikes() != null) {
            updateLikes(film);
        }

        film.setMpa(getMpaById(film.getId()));
        film.setGenres(getGenresById(film.getId()));
        return film;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_FILM_BY_ID, id);
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        if (Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT COUNT(USER_ID) <= 0 FROM FILMS_USERS",
                Boolean.class))) {
            List<Film> films = jdbcTemplate
                    .query(SELECT_FILMS_LIMIT, filmMapper, count);
            for (Film film : films) {
                film.setMpa(getMpaById(film.getId()));
                film.setGenres(getGenresById(film.getId()));
                film.setUserLikes(getLikesByFilmId(film.getId()));
            }
            return films;
        }
        List<Film> films = jdbcTemplate
                .query(SELECT_POPULAR_FILMS_LIMIT, filmMapper, count);
        for (Film film : films) {
            film.setMpa(getMpaById(film.getId()));
            film.setGenres(getGenresById(film.getId()));
            film.setUserLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {

        if (filmId <= 0) {
            throw new NotFoundElementException("Film with id='" + filmId + "' not found");
        }
        if (userId <= 0) {
            throw new NotFoundElementException("User with id='" + userId + "' not found");
        }

        jdbcTemplate.update(INSERT_FILMS_USERS, userId, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        if (filmId <= 0) {
            throw new NotFoundElementException("Film with id='" + filmId + "' not found");
        }
        if (userId <= 0) {
            throw new NotFoundElementException("User with id='" + userId + "' not found");
        }

        jdbcTemplate.update(DELETE_FILMS_USERS_BY_FILM_ID_AND_USER_ID, filmId, userId);
    }


    private boolean isExistFilmById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT COUNT(id) > 0  FROM FILMS WHERE ID = ?",
                Boolean.class, id));
    }

    private Mpa getMpaById(Long filmId) {
        return jdbcTemplate.queryForObject(SELECT_MPA_BY_FILM_ID, new BeanPropertyRowMapper<>(Mpa.class), filmId);
    }

    private Set<Genre> getGenresById(Long filmId) {
        SqlRowSet genresRowSet = jdbcTemplate.queryForRowSet(SELECT_GENRES_BY_FILM_ID, filmId);
        Set<Genre> filmGenres = new HashSet<>();
        while (genresRowSet.next()) {
            Genre genre = new Genre(genresRowSet.getInt("id"),
                    genresRowSet.getString("name"));
            filmGenres.add(genre);
        }
        return filmGenres;
    }

    private Set<Long> getLikesByFilmId(Long filmId) {
        SqlRowSet likesRowSet = jdbcTemplate.queryForRowSet(SELECT_FILMS_USERS_BY_FILM_ID, filmId);
        Set<Long> likes = new HashSet<>();

        while (likesRowSet.next()) {
            likes.add(likesRowSet.getLong("user_id"));
        }
        return likes;
    }

    private void updateLikes(Film film) {
        jdbcTemplate.update(DELETE_FILMS_USERS_BY_FILM_ID, film.getId());
        Set<Long> newLikes = film.getUserLikes();
        newLikes.forEach(userId ->
                jdbcTemplate.update(INSERT_FILMS_USERS, userId, film.getId()));
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update(DELETE_FILMS_GENRES_BY_FILM_ID, film.getId());
        List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        filmGenreIds.forEach(filmGenreId ->
                jdbcTemplate.update(INSERT_FILMS_GENRES, film.getId(), filmGenreId));
    }
}
