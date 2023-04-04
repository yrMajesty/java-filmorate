package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

@Configuration
@ComponentScan("ru.yandex.practicum.filmorate")
public class SpringConfig {

    @Bean
    public FilmRepository filmRepository() {
        return new FilmRepository();
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
}
