package com.lxc.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Frank_lin
 * @date 2022/6/26
 */

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {ListValueConstraintValidatorForInteger.class
                , ListValueConstraintValidatorForDouble.class}
)
public @interface ListValue {

    String message() default "{com.lxc.common.valid.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    int[] intValues() default {};

    double[] doubleValue() default {};

}
