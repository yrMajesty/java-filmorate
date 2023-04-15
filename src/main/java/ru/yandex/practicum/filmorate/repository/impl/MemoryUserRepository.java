package ru.yandex.practicum.filmorate.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;

@Slf4j
@Repository
public class MemoryUserRepository implements UserRepository {

    private long idUser = 1;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> save(User user) {
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
        users.put(user.getId(), user);
        return Optional.of(user);
    }

}
