package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmRepositoryTest {

    private final FilmRepository underTest;

    static Film firstFilm;

    @BeforeAll
    static void init() {
        firstFilm = Film.builder()
                .id(1L)
                .name("First film")
                .description("Describe first film")
                .releaseDate(LocalDate.now())
                .duration(30)
                .mpa(new Mpa(1, "G"))
                .build();
    }

    @BeforeEach
    void cleanDB() {
        underTest.findAll().forEach(u -> underTest.deleteById(u.getId()));
    }

    @Test
    void findAll_emptyList_notCreatedFilms() {
        List<Film> result = underTest.findAll();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void findAll_listFilmsContainsOneElement_oneFilmWasCreated() {
        underTest.save(firstFilm);

        List<Film> result = underTest.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void update_updateResultIsNull_filmIdNotExistInDB() {
        Film film = Film.builder()
                .id(9999L)
                .name("Test film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(30)
                .mpa(new Mpa(1, "G"))
                .build();
        assertThrows(NotFoundElementException.class, () -> underTest.update(film));
    }

    @Test
    void update_successfulUpdate_filmIdIsCorrected() {
        underTest.save(firstFilm);

        Film film = Film.builder()
                .id(firstFilm.getId())
                .name("Update name film")
                .description("Describe film")
                .releaseDate(LocalDate.now())
                .duration(30)
                .mpa(new Mpa(1, "G"))
                .build();
        Film result = underTest.update(film);

        assertThat(result).hasFieldOrPropertyWithValue("name", film.getName());
    }

    @Test
    void findById_notFoundElementException_filmIdIsNotExistInDB() {
        assertThrows(NotFoundElementException.class, () -> underTest.findById(999L));
    }

    @Test
    void findById_successfulReturnOneFilm_filmIdIsExistInDB() {
        underTest.save(firstFilm);
        Film result = underTest.findById(firstFilm.getId());
        assertThat(result).isEqualTo(firstFilm);
    }


    @Test
    void findPopularFilms_emptyListResult_filmsWereNotCreated() {
        List<Film> result = underTest.findPopularFilms(10);
        assertThat(result).isEmpty();
    }

}

