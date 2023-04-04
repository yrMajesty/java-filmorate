package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Request add new user");
        userRepository.createUser(user);
        log.info("Successful added new user {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Request update user {}", user);
        userRepository.updateUser(user);
        log.info("Updatable user {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Request get all users");
        Collection<User> users = userRepository.getAllUsers();
        return new ArrayList<>(users);
    }
}