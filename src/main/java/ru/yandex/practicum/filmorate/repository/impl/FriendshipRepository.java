package ru.yandex.practicum.filmorate.repository.impl;

import ru.yandex.practicum.filmorate.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FriendshipRepository {
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(Long id, Long friendId) {
        jdbcTemplate.update(
                "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, APPROVED) VALUES (?, ?, 0)",
                id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        jdbcTemplate.update(
                "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?",
                id, friendId);
    }

    public List<User> getFriendsByUserId(Long id) {
        return jdbcTemplate.query(
                "SELECT ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                        "FROM FRIENDSHIP f " +
                        "LEFT JOIN USERS U ON f.FRIEND_ID = U.ID " +
                        "WHERE USER_ID = ?",
                new BeanPropertyRowMapper<>(User.class), id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return jdbcTemplate.query(
                "SELECT * FROM USERS us\n" +
                        "JOIN FRIENDSHIP AS fr1 ON us.ID = fr1.FRIEND_ID\n" +
                        "JOIN FRIENDSHIP AS fr2 ON us.ID = fr2.FRIEND_ID\n" +
                        "WHERE fr1.USER_ID = ? AND fr2.USER_ID = ?",
                new BeanPropertyRowMapper<>(User.class), id, otherId);
    }
}
