package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.impl.MemoryUserRepository;
import ru.yandex.practicum.filmorate.service.UserService;


import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;
    private static Validator validator;
    private User user;

    @BeforeAll
    static void beforeAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void init() {
        user = User
                .builder()
                .email("user@mail.ru")
                .login("userLogin")
                .name("userName")
                .birthday(LocalDate.now())
                .build();
        UserService userService = new UserService(new MemoryUserRepository());
        userController = new UserController(userService);
    }

    @Test
    void createUser_rejectEmail_emailIsNull() {
        user.setEmail(null);
        assertEquals(1, validator.validate(user).size(),
                "Нет ошибки валидации при создании пользователя с email = null");

    }

    @Test
    void createUser_rejectEmail_emailIsEmpty() {
        user.setEmail("");
        assertEquals(1, validator.validate(user).size(),
                "Нет ошибки валидации при создании пользователя с пустым email");

    }

    @Test
    void createUser_acceptEmail_emailIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(user).isEmpty(),
                "Валидатор отклонил создание пользователя с валидным email");
    }

    @Test
    void createUser_rejectLogin_loginIsNull() {
        user.setLogin(null);
        assertEquals(1, validator.validate(user).size(),
                "Валидатор не отклонил создание пользователя с логином = null");

    }

    @Test
    void createUser_rejectLogin_loginIsEmpty() {
        user.setLogin("");
        assertEquals(2, validator.validate(user).size(),
                "Валидатор не отклонил создание пользователя с пустым логином");
    }

    @Test
    void createUser_acceptLogin_loginIsNotEmptyAndNotNull() {
        assertTrue(validator.validate(user).isEmpty(),
                "Валидатор отклонил создание пользователя с валидным логином");
    }

    @Test
    void createUser_rejectBirthday_birthdayIsFuture() {
        user.setBirthday(LocalDate.now().plusDays(10));
        assertEquals(1, validator.validate(user).size(),
                "Валидатор не отклонил создание пользователя с датой рождения больше текущей");
    }

    @Test
    void updateUser_validationException_userWithIdIsNull() {
        userController.createUser(user);
        user.setId(null);
        assertThrows(ValidationException.class, () -> userController.updateUser(user),
                "Нет ошибки валидации при обновлении пользователя id которого null");
    }

    @Test
    void updateUser_validationException_userWithIdIsIncorrect() {
        userController.createUser(user);
        user.setId(-5L);
        assertThrows(ValidationException.class, () -> userController.updateUser(user),
                "Нет ошибки валидации при обновлении пользователя c некорректным id");
    }

    @Test
    void createUser_validationException_userWithLoginIsNotUnique() {
        userController.createUser(user);
        User newUser = new User(null, "user1@mail.ru", "userLogin", "userName", LocalDate.now());
        assertThrows(ValidationException.class, () -> userController.updateUser(newUser),
                "Нет ошибки валидации при создании пользователя с логином, уже имеющимся в базе данных");
    }

    @Test
    void updateUser_validationException_userIdIsNotExist() {
        userController.createUser(user);
        User newUser = new User(10L, "user@mail.ru", "userLogin1", "userName", LocalDate.now());
        assertThrows(NoSuchElementException.class, () -> userController.updateUser(newUser),
                "Нет ошибки валидации при обновлении пользователя с id которого нет в базе");
    }
}