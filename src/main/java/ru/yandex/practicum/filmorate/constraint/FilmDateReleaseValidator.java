package ru.yandex.practicum.filmorate.constraint;

import ru.yandex.practicum.filmorate.constraint.FilmDateRelease;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmDateReleaseValidator implements ConstraintValidator<FilmDateRelease, LocalDate> {

    private int year;
    private int month;
    private int day;

    @Override
    public void initialize(FilmDateRelease constraintAnnotation) {
        this.day = constraintAnnotation.day();
        this.month = constraintAnnotation.month();
        this.year = constraintAnnotation.year();
    }

    @Override
    public boolean isValid(LocalDate valueDate, ConstraintValidatorContext constraintValidatorContext) {
        return valueDate.isAfter(LocalDate.of(year, month, day));
    }
}
