package ru.yandex.practicum.filmorate.repository.impl.memory;

import ru.yandex.practicum.filmorate.exception.ExistElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemoryUserRepository implements UserDao {

    private long idUser = 0;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        user.setId(++idUser);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(Long id) {
        User foundedUser = users.get(id);
        if (foundedUser == null) {
            throw new NoSuchElementException("User with id='" + id + "' not found");
        }
        return foundedUser;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }


    @Override
    public boolean isExistUserWithLogin(String login) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getLogin().equals(login));
    }

    @Override
    public boolean isExistUserById(Long id) {
        return users.get(id) != null;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("User with id='{}' add friend with id='{}'", userId, friendId);
        User foundUser = findById(userId);
        User foundFriend = findById(friendId);

        foundUser.getFriends().add(friendId);
        foundFriend.getFriends().add(userId);
        log.info("Successful add friend id='{}' in friends user with id='{}'", friendId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("User with id='{}' delete friend with id='{}'", userId, friendId);

        User foundUser = findById(userId);
        User foundFriend = findById(friendId);

        foundUser.getFriends().remove(friendId);
        foundFriend.getFriends().remove(userId);
        log.info("Successful delete friend id='{}' from friends user with id='{}'", friendId, userId);
    }

    @Override
    public List<User> getFriendsByUserId(Long userId) {
        log.info("Get friends user with id='{}'", userId);
        User user = findById(userId);
        return findUsersByIds(user.getFriends());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        log.info("Request get common friends users id='{}' and id='{}'", id, otherId);
        User user = findById(id);
        User otherUser = findById(otherId);

        Set<Long> userFriendsIds = user.getFriends();
        Set<Long> otherUserFriendsIds = otherUser.getFriends();

        Set<Long> commonUsers = new HashSet<>(userFriendsIds);
        commonUsers.retainAll(otherUserFriendsIds);

        return findUsersByIds(commonUsers);
    }

    @Override
    public User findByLogin(String login) {
        return users.values()
                .stream()
                .filter(user -> user.getLogin().equals(login))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("User with login='" + login + "' not found"));
    }

    @Override
    public User update(User user) {
        checkNotExistUserById(user.getId());
        checkExistUserByLogin(user.getLogin());

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    private List<User> findUsersByIds(Set<Long> usersIds) {
        List<User> foundUsers = new ArrayList<>();

        for (Long id : usersIds) {
            foundUsers.add(users.get(id));
        }
        return foundUsers;
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
