package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistUserException;
import ru.yandex.practicum.filmorate.exception.FilmorateException;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("Create new user {}", user);
        checkExistUserByLogin(user.getLogin());

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        Optional<User> userOptional = userRepository.save(user);

        if (userOptional.isEmpty()) {
            throw new FilmorateException("Ошибка сохранения");
        }
        log.info("Successful create new user {}", user);
        return userOptional.get();
    }

    public User updateUser(User user) {
        log.info("Update user {}", user);
        if (user.getId() == null || user.getId() <= 0) {
            log.error("Invalid id='{}' of updatable user", user.getLogin());
            throw new ValidationException("Невалидный id='" + user.getId() + "' обновляемого пользователя");
        }

        checkNotExistUserById(user.getId());
        checkExistUserByLogin(user.getLogin());

        Optional<User> userOptional = userRepository.update(user);

        if (userOptional.isEmpty()) {
            throw new FilmorateException("Ошибка обновления");
        }
        log.info("Successful update user {}", user);
        return userOptional.get();
    }

    public List<User> getAllUsers() {
        log.info("Get all users");
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        log.info("Get user by id='{}'", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("Пользователь с id='" + id + "' не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("User with id='{}' add friend with id='{}'", userId, friendId);
        checkNotExistUserById(userId);
        checkNotExistUserById(friendId);

        User foundUser = getUserById(userId);
        User foundFriend = getUserById(friendId);

        foundUser.getFriends().add(friendId);
        foundFriend.getFriends().add(userId);
        log.info("Successful add friend id='{}' in friends user with id='{}'", friendId, userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("User with id='{}' delete friend with id='{}'", userId, friendId);
        checkNotExistUserById(userId);
        checkNotExistUserById(friendId);

        User foundUser = getUserById(userId);
        User foundFriend = getUserById(friendId);

        foundUser.getFriends().remove(friendId);
        foundFriend.getFriends().remove(userId);
        log.info("Successful delete friend id='{}' from friends user with id='{}'", friendId, userId);
    }

    public List<User> getFriendsByUserId(Long userId) {
        log.info("Get friends user with id='{}'", userId);
        User user = getUserById(userId);
        return userRepository.findUsersByIds(user.getFriends());
    }

    public List<User> getCommonFriends(Long id, Long otherUserId) {
        log.info("Request get common friends users id='{}' and id='{}'", id, otherUserId);
        User user = getUserById(id);
        User otherUser = getUserById(otherUserId);

        Set<Long> userFriendsIds = user.getFriends();
        Set<Long> otherUserFriendsIds = otherUser.getFriends();

        Set<Long> commonUsers = new HashSet<>(userFriendsIds);
        commonUsers.retainAll(otherUserFriendsIds);

        return userRepository.findUsersByIds(commonUsers);
    }

    private void checkExistUserByLogin(String login) {
        Optional<User> userOptional = userRepository.findByLogin(login);
        if (userOptional.isPresent()) {
            log.error("User with login='{}' already exist", login);
            throw new ExistUserException("Пользователь с логином '" + login + "' уже существует");
        }
    }

    private void checkNotExistUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            log.error("User with id='{}' not found", id);
            throw new NoSuchUserException("Пользователь с id='" + id + "' не найден");
        }
    }
}
