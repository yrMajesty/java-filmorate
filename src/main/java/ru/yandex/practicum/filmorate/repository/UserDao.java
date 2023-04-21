package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao extends Repository<User, Long> {

    User findByLogin(String login);

    boolean isExistUserWithLogin(String login);

    boolean isExistUserById(Long id);

     void addFriend(Long id, Long friendId);

     void deleteFriend(Long id, Long friendId);

     List<User> getFriendsByUserId(Long id);

     List<User> getCommonFriends(Long id, Long otherId);

}