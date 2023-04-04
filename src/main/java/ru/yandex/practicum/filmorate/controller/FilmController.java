package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmRepository filmRepository;

    @Autowired
    public FilmController(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Request add new film");
        filmRepository.createFilm(film);
        log.info("Successful added new film {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Request update film {}", film);
        filmRepository.updateFilm(film);
        log.info("Updatable film {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Request get all films");
        Collection<Film> films = filmRepository.getAllFilms();
        return new ArrayList<>(films);
    }
}