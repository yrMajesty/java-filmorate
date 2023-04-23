package ru.yandex.practicum.filmorate.repository;

import java.util.List;

public interface Repository<T, ID> {

    T save(T t);

    List<T> findAll();

    T findById(ID id);

    T update(T t);

    void deleteById(ID id);
}
