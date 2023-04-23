package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.impl.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public Genre getGenreById(Long id) {
        log.info("Get genre with id='{}'", id);
        return genreRepository.findById(id);
    }

    public List<Genre> getAllGenres() {
        log.info("Get all genres");
        return genreRepository.findAll();
    }
}
