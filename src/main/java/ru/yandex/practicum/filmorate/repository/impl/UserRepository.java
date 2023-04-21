package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.ExistElementException;
import ru.yandex.practicum.filmorate.exception.NotFoundElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Primary
@RequiredArgsConstructor
@Repository("userRep")
public class UserRepository implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipRepository friendshipRepository;

    private static final String SELECT_FIND_ALL_USERS = "SELECT * FROM USERS";
    private static final String SELECT_FIND_USER_BY_ID = "SELECT * FROM USERS WHERE ID = ?";
    private static final String SELECT_FIND_USER_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?";
    private static final String SELECT_COUNT_ID = "SELECT COUNT(id) FROM USERS";
    private static final String INSERT_NEW_USER = "INSERT INTO USERS (NAME, LOGIN, EMAIL, BIRTHDAY) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_BY_ID = "UPDATE USERS SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE ID =?";
    private static final String DELETE_USER_BY_ID = "DELETE FROM USERS WHERE ID = ?";

    @Override
    public User save(User user) {

        if (isExistUserWithLogin(user.getLogin())) {
            log.error("User with login='{}' already exist", user.getLogin());
            throw new ExistElementException("User with login='" + user.getLogin() + "' already exist");
        }

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(INSERT_NEW_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, kh);

        final Long userId = kh.getKeyAs(Long.class);
        user.setId(userId);

        return user;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(SELECT_FIND_ALL_USERS, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FIND_USER_BY_ID, new BeanPropertyRowMapper<>(User.class), id);
        } catch (DataAccessException ex) {
            log.error("User with id='{}' not found", id);
            throw new NotFoundElementException("User with id='" + id + "' not found");
        }
    }

    @Override
    public User update(User user) {
        if (!isExistUserById(user.getId())) {
            log.error("User with id='{}' already exist", user.getId());
            throw new NotFoundElementException("User with id='" + user.getId() + "' not found");
        }

        jdbcTemplate.update(UPDATE_USER_BY_ID,
                user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_USER_BY_ID, id);
    }

    @Override
    public User findByLogin(String login) {
        try {
            return jdbcTemplate.queryForObject(SELECT_FIND_USER_BY_LOGIN, new BeanPropertyRowMapper<>(User.class), login);
        } catch (DataAccessException ex) {
            log.error("User with login='{}' not found", login);
            throw new NotFoundElementException("User with login='" + login + "' not found");
        }
    }

    @Override
    public boolean isExistUserWithLogin(String login) {
        List<User> userList = jdbcTemplate.query(SELECT_FIND_USER_BY_LOGIN, new BeanPropertyRowMapper<>(User.class), login);
        return !userList.isEmpty();
    }

    @Override
    public boolean isExistUserById(Long id) {
        List<User> userList = jdbcTemplate.query(SELECT_FIND_USER_BY_ID, new BeanPropertyRowMapper<>(User.class), id);
        return !userList.isEmpty();
    }

    public void addFriend(Long id, Long friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        friendshipRepository.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        friendshipRepository.deleteFriend(id, friendId);
    }

    public List<User> getFriendsByUserId(Long id) {
        return friendshipRepository.getFriendsByUserId(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return friendshipRepository.getCommonFriends(id, otherId);
    }
}
