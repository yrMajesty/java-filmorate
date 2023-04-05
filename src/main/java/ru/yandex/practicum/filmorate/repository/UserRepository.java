package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class UserRepository {
    private long idUser = 0;
    private final Map<Long, User> users = new HashMap<>();

    public void createUser(User user) {
        if (users.values()
                .stream()
                .anyMatch(userSaved -> userSaved.getLogin().equals(user.getLogin()))) {
            log.error("User with login {} already exist", user.getLogin());
            throw new ValidationException("Пользователь с таким логином уже существует");
        }
        user.setId(++idUser);

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
    }


    public void updateUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            log.error("Id updatable user must not be null or less than 1");
            throw new ValidationException("Id обновляемого пользователя не должен быть null или меньше 1");

        }

        if (!users.containsKey(user.getId())) {
            log.error("User with id='{}' is not exist", user.getId());
            throw new NoSuchElementException("Пользователь с id='" + user.getId() + "' не существует");
        }

        users.put(user.getId(), user);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
