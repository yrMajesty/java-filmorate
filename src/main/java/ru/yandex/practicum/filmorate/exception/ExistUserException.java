package ru.yandex.practicum.filmorate.exception;

public class ExistUserException extends RuntimeException {
    public ExistUserException(String message) {
        super(message);
    }
}
