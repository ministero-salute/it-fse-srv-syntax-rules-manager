/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.impl.UniqueMultipartValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueMultipartValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueMultipart {
    String message() default "One or more files duplicated";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
