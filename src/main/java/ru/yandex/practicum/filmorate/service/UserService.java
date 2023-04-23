package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserDao userRepository;

    public Collection<User> getAllUsers() {
        log.info("Get all users");
        return userRepository.findAll();
    }

    public User createUser(User user) {
        log.info("Create new user {}", user);

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        User createdUser = userRepository.save(user);
        log.info("Created user {}", createdUser);
        return createdUser;
    }


    public User getUserById(Long id) {
        log.info("Get user by id='{}'", id);
        if (id < 0) {
            throw new ValidationException("Invalid id");
        }
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            log.error("Invalid id='{}' of updatable user", user.getLogin());
            throw new javax.validation.ValidationException("Невалидный id='" + user.getId() + "' обновляемого пользователя");
        }

        return userRepository.update(user);
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
