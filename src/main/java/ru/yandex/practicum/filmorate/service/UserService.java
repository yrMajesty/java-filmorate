package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("Create new user {}", user);

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        userRepository.save(user);
        log.info("Successful create new user {}", user);
        return user;
    }

    public User updateUser(User user) {
        log.info("Update user {}", user);
        if (user.getId() == null || user.getId() <= 0) {
            log.error("Invalid id='{}' of updatable user", user.getLogin());
            throw new ValidationException("Невалидный id='" + user.getId() + "' обновляемого пользователя");
        }

        userRepository.update(user);

        log.info("Successful update user {}", user);
        return user;
    }

    public List<User> getAllUsers() {
        log.info("Get all users");
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        log.info("Get user by id='{}'", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id='" + id + "' не найден"));
    }

    public void addFriend(Long id, Long friendId) {
        userRepository.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        userRepository.deleteFriend(id, friendId);
    }

    public List<User> getFriendsByUserId(Long id) {
        return userRepository.getFriendsByUserId(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userRepository.getCommonFriends(id, otherId);
    }
}
