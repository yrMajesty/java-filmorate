package ru.yandex.practicum.filmorate.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;

@Slf4j
@Repository
public class MemoryUserRepository implements UserRepository {

    private long idUser = 0;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> save(User user) {
        checkExistUserByLogin(user.getLogin());
        user.setId(++idUser);
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findUsersByIds(Set<Long> usersIds) {
        List<User> foundUsers = new ArrayList<>();

        for (Long id : usersIds) {
            foundUsers.add(users.get(id));
        }

        return foundUsers;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        for (User user : users.values()) {
            if (user.getLogin().equals(login)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User>  update(User user) {
        checkNotExistUserById(user.getId());
        checkExistUserByLogin(user.getLogin());

        users.put(user.getId(), user);
        return Optional.of(user);

    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("User with id='{}' add friend with id='{}'", userId, friendId);
        User foundUser = users.get(userId);
        if (foundUser == null) {
            throw new NoSuchElementException("User with id='" + userId + "' not found");
        }
        User foundFriend = users.get(friendId);
        if (foundFriend == null) {
            throw new NoSuchElementException("User with id='" + friendId + "' not found");
        }

        foundUser.getFriends().add(friendId);
        foundFriend.getFriends().add(userId);
        log.info("Successful add friend id='{}' in friends user with id='{}'", friendId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("User with id='{}' delete friend with id='{}'", userId, friendId);

        User foundUser = users.get(userId);
        if (foundUser == null) {
            throw new NoSuchElementException("User with id='" + userId + "' not found");
        }
        User foundFriend = users.get(friendId);
        if (foundFriend == null) {
            throw new NoSuchElementException("User with id='" + friendId + "' not found");
        }

        foundUser.getFriends().remove(friendId);
        foundFriend.getFriends().remove(userId);
        log.info("Successful delete friend id='{}' from friends user with id='{}'", friendId, userId);
    }

    @Override
    public List<User> getFriendsByUserId(Long userId) {
        log.info("Get friends user with id='{}'", userId);
        User user = users.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User with id='" + userId + "' not found");
        }
        return findUsersByIds(user.getFriends());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        log.info("Request get common friends users id='{}' and id='{}'", id, otherId);
        User user = users.get(id);
        if (user == null) {
            throw new NoSuchElementException("User with id='" + id + "' not found");
        }
        User otherUser = users.get(otherId);
        if (otherUser == null) {
            throw new NoSuchElementException("User with id='" + otherId + "' not found");
        }

        Set<Long> userFriendsIds = user.getFriends();
        Set<Long> otherUserFriendsIds = otherUser.getFriends();

        Set<Long> commonUsers = new HashSet<>(userFriendsIds);
        commonUsers.retainAll(otherUserFriendsIds);

        return findUsersByIds(commonUsers);
    }


    private void checkExistUserByLogin(String login) {
        Optional<User> userOptional = users.values()
                .stream()
                .filter(user -> user.getLogin().equals(login))
                .findAny();
        if (userOptional.isPresent()) {
            log.error("User with login='{}' already exist", login);
            throw new ExistElementException("Пользователь с логином '" + login + "' уже существует");
        }
    }

    private void checkNotExistUserById(Long id) {
        Optional<User> userOptional = users.values()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findAny();
        if (userOptional.isEmpty()) {
            log.error("User with id='{}' not found", id);
            throw new NoSuchElementException("Пользователь с id='" + id + "' не найден");
        }
    }

}
