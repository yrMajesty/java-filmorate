package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRepositoryTest {

    private final MpaRepository underTest;

    @Test
    void findAll_correctListResult() {
        assertThat(underTest.findAll()).hasSize(5);
    }

    @Test
    void findById_correctMpaElement_idCorrect() {
        Mpa result = underTest.findById(5L);

        assertThat(result.getId()).isEqualTo(5);
        assertThat(result.getName()).isEqualTo("NC-17");
    }

    @Test
    void findById_noSuchElementException_idIsIncorrect() {
        assertThrows(NotFoundElementException.class, () -> underTest.findById(-5L));
    }
}