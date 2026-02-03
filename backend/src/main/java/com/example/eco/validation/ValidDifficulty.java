package com.example.eco.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DifficultyValidator.class)
@Documented
public @interface ValidDifficulty {
    
    String message() default "La difficulté doit être: débutant, intermédiaire ou avancé";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}