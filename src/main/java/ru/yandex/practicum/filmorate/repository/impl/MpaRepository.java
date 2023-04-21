package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MpaRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_MPA = "SELECT * FROM MPA";
    private static final String SELECT_FIND_BY_ID = "SELECT * FROM MPA WHERE ID = ?";

    public List<Mpa> findAll() {
        return jdbcTemplate.query(SELECT_ALL_MPA, new BeanPropertyRowMapper<>(Mpa.class));
    }

    public Mpa findById(Long id) {
        Optional<Mpa> optionalMpa = jdbcTemplate.query(SELECT_FIND_BY_ID, new BeanPropertyRowMapper<>(Mpa.class), id).stream().findFirst();
        if (optionalMpa.isEmpty()) {
            log.error("Mpa with id='{}' not found", id);
            throw new NotFoundElementException("Mpa with id='" + id + "' not found");
        }
        return optionalMpa.get();
    }
}
