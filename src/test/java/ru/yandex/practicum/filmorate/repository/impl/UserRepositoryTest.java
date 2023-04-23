package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {

    private final UserRepository underTest;

    static User firstUser;

    @BeforeAll
    static void init() {
        firstUser = User.builder()
                .id(1L)
                .name("Test name")
                .email("test@mail.ru")
                .login("test login")
                .birthday(LocalDate.now())
                .build();
    }

    @BeforeEach
    void cleanDB() {
        underTest.findAll().forEach(u -> underTest.deleteById(u.getId()));
    }

    @Test
    void findAll_emptyList_notCreatedFilms() {
        List<User> result = underTest.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_listUsersContainsOneElement_oneUserWasCreated() {
        underTest.save(firstUser);

        List<User> result = underTest.findAll();
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0)).hasFieldOrPropertyWithValue("name", firstUser.getName()),
                () -> assertThat(result.get(0)).hasFieldOrPropertyWithValue("email", firstUser.getEmail()),
                () -> assertThat(result.get(0)).hasFieldOrPropertyWithValue("login", firstUser.getLogin())
        );
    }

    @Test
    void update_updateResultIsNull_userIdNotExistInDB() {
        User user = User.builder()
                .id(9999L)
                .name("Update test name")
                .email("test@mail.ru")
                .login("Update test login")
                .birthday(LocalDate.now())
                .build();

        assertThrows(NotFoundElementException.class, () -> underTest.update(user));
    }

    @Test
    void update_successfulUpdate_filmIdIsCorrected() {
        underTest.save(firstUser);

        User user = User.builder()
                .id(firstUser.getId())
                .name("Update test name")
                .email("test@mail.ru")
                .login("Update test login")
                .birthday(LocalDate.now())
                .build();
        User result = underTest.update(user);

        assertThat(result).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(result).hasFieldOrPropertyWithValue("login", user.getLogin());
    }

    @Test
    void findById_notFoundElementException_filmIdIsNotExistInDB() {
        assertThrows(NotFoundElementException.class, () -> underTest.findById(999L));
    }

    @Test
    void findById_successfulReturnOneFilm_filmIdIsExistInDB() {
        firstUser = underTest.save(firstUser);
        User result = underTest.findById(firstUser.getId());
        assertThat(result).isEqualTo(firstUser);
    }
}