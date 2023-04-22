package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GenreRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM GENRES",
                new BeanPropertyRowMapper<>(Genre.class));
    }

    public Genre findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRES WHERE ID = ?",
                    new BeanPropertyRowMapper<>(Genre.class), id);
        } catch (DataAccessException ex) {
            log.error("Genre with id='{}' not found", id);
            throw new NotFoundElementException("Genre with id='" + id + "' not found");
        }
    }
}
