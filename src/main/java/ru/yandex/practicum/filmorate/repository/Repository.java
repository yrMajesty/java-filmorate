package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    Optional<T> save(T element);

    List<T> findAll();

    Optional<T> findById(ID id);

    Optional<T> update(T element);
}
