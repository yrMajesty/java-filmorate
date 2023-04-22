package ru.yandex.practicum.filmorate.repository.impl;

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
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Primary
@RequiredArgsConstructor
@Repository
public class FilmRepository implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public Film save(Film film) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
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
                jdbcTemplate.update("INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)",
                        filmId, genre.getId());
            }
            film.setGenres(getGenresById(filmId));
        }

        Set<Long> likes = film.getUserLikes();
        if (likes != null) {
            for (Long userId : likes) {
                jdbcTemplate.update("INSERT INTO FILMS_USERS (USER_ID, FILM_ID) VALUES (?, ?)", userId, filmId);
            }
            film.setUserLikes(getLikesByFilmId(filmId));
        }

        if (film.getMpa() != null) {
            Mpa mpa = jdbcTemplate.queryForObject(
                    "SELECT m.ID, m.NAME FROM MPA m JOIN FILMS f ON f.MPA_ID = m.ID WHERE f.ID = ?",
                    new BeanPropertyRowMapper<>(Mpa.class), film.getId());
            film.setMpa(mpa);
        }
        return film;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(
                "SELECT F.*, M.ID AS m_id, M.NAME as m_name FROM FILMS F LEFT JOIN MPA M on M.ID = F.MPA_ID",
                filmMapper);

        for (Film film : films) {
            film.setGenres(getGenresById(film.getId()));
            film.setUserLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public Film findById(Long id) {
        try {
            Optional<Film> film = Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT F.*, M.ID AS m_id, M.NAME as m_name FROM FILMS F LEFT JOIN MPA M on M.ID = F.MPA_ID WHERE F.ID = ?",
                    filmMapper, id));
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

        int update = jdbcTemplate.update(
                "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, MPA_ID = ? WHERE ID = ?",
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

        Mpa mpa = jdbcTemplate.queryForObject(
                "SELECT m.ID, m.NAME FROM MPA m JOIN FILMS f ON f.MPA_ID = m.ID WHERE f.ID = ?",
                new BeanPropertyRowMapper<>(Mpa.class), film.getId());
        film.setMpa(mpa);
        film.setGenres(getGenresById(film.getId()));
        return film;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(
                "DELETE FROM FILMS WHERE ID = ?",
                id);
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        if (Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT COUNT(USER_ID) <= 0 FROM FILMS_USERS",
                Boolean.class))) {
            List<Film> films = jdbcTemplate
                    .query("SELECT F.*, M.ID AS m_id, M.NAME as m_name " +
                                    "FROM FILMS F " +
                                    "LEFT JOIN MPA M on M.ID = F.MPA_ID " +
                                    "ORDER BY F.ID DESC " +
                                    "LIMIT ?",
                            filmMapper, count);
            for (Film film : films) {
                film.setGenres(getGenresById(film.getId()));
                film.setUserLikes(getLikesByFilmId(film.getId()));
            }
            return films;
        }
        List<Film> films = jdbcTemplate
                .query(
                        "SELECT f.*, COUNT(USER_ID) as count_like, m.ID as m_id, m.NAME as m_name " +
                                "FROM FILMS_USERS fu " +
                                "LEFT JOIN FILMS f ON fu.FILM_ID = f.ID " +
                                "LEFT JOIN MPA m on M.ID = F.MPA_ID " +
                                "GROUP BY fu.FILM_ID " +
                                "ORDER BY count_like DESC " +
                                "LIMIT ?",
                        filmMapper, count);
        for (Film film : films) {
            film.setGenres(getGenresById(film.getId()));
            film.setUserLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update("INSERT INTO FILMS_USERS (USER_ID, FILM_ID) VALUES (?, ?)", userId, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        jdbcTemplate.update("DELETE FROM FILMS_USERS WHERE FILM_ID = ? AND USER_ID = ?", filmId, userId);
    }


    private boolean isExistFilmById(Long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT COUNT(id) > 0 FROM FILMS WHERE ID = ?",
                Boolean.class, id));
    }

    private Set<Genre> getGenresById(Long filmId) {
        SqlRowSet genresRowSet = jdbcTemplate.queryForRowSet(
                "SELECT g.ID, g.NAME FROM GENRES g JOIN FILMS_GENRES fg ON g.ID = fg.GENRE_ID WHERE fg.FILM_ID = ?",
                filmId);
        Set<Genre> filmGenres = new HashSet<>();
        while (genresRowSet.next()) {
            Genre genre = new Genre(genresRowSet.getInt("id"),
                    genresRowSet.getString("name"));
            filmGenres.add(genre);
        }
        return filmGenres;
    }

    private Set<Long> getLikesByFilmId(Long filmId) {
        SqlRowSet likesRowSet = jdbcTemplate.queryForRowSet(
                "SELECT l.USER_ID FROM FILMS f JOIN FILMS_USERS l ON f.ID = l.FILM_ID WHERE f.ID = ?",
                filmId);
        Set<Long> likes = new HashSet<>();

        while (likesRowSet.next()) {
            likes.add(likesRowSet.getLong("user_id"));
        }
        return likes;
    }

    private void updateLikes(Film film) {
        jdbcTemplate.update("DELETE FROM FILMS_USERS WHERE FILM_ID = ?",
                film.getId());
        Set<Long> newLikes = film.getUserLikes();
        newLikes.forEach(userId ->
                jdbcTemplate.update("INSERT INTO FILMS_USERS (USER_ID, FILM_ID) VALUES (?, ?)",
                        userId, film.getId()));
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update("DELETE FROM FILMS_GENRES WHERE FILM_ID = ?",
                film.getId());
        List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        filmGenreIds.forEach(filmGenreId ->
                jdbcTemplate.update("INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)",
                        film.getId(), filmGenreId));
    }
}
