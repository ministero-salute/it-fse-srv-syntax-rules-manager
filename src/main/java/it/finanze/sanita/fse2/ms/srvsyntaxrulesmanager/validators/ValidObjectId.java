package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.impl.ObjectIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ObjectIdValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidObjectId {
    String message() default "ObjectId not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
