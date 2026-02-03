package com.example.eco.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class DifficultyValidator implements ConstraintValidator<ValidDifficulty, String> {

    private static final List<String> VALID_DIFFICULTIES = 
        Arrays.asList("débutant", "intermédiaire", "avancé");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return VALID_DIFFICULTIES.contains(value.toLowerCase());
    }
}