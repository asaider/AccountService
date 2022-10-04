package account.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface PasswordChecker {
    String message() default "The password is in the hacker's database!";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
