package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreRepositoryTest {

    private final GenreRepository underTest;

    @Test
    void findAll_correctListResult() {
        assertThat(underTest.findAll()).hasSize(6);
    }

    @Test
    void findById_correctMpaElement_idCorrect() {
        Genre result = underTest.findById(1L);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Комедия");
    }

    @Test
    void findById_noSuchElementException_idIsIncorrect() {
        assertThrows(NotFoundElementException.class, () -> underTest.findById(-5L));
    }
}