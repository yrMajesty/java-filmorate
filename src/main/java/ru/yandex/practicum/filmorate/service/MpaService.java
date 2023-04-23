package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.impl.MpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {
    private final MpaRepository mpaRepository;

    public Mpa getMpaById(Long id) {
        log.info("Get mpa with id='{}'", id);
        return mpaRepository.findById(id);
    }

    public List<Mpa> getAllMpa() {
        log.info("Get all mpa");
        return mpaRepository.findAll();
    }
}
