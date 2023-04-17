package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends Repository<User, Long> {

    Optional<User> findByLogin(String login);

    List<User> findUsersByIds(Set<Long> ids);

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    List<User> getFriendsByUserId(Long id);

    List<User> getCommonFriends(Long id, Long otherId);
}