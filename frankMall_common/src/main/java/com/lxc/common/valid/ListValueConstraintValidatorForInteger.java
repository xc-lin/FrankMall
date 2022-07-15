package com.lxc.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Frank_lin
 * @date 2022/6/26
 */
public class ListValueConstraintValidatorForInteger implements ConstraintValidator<ListValue,Integer> {
    private Set<Integer> set = new HashSet<>();
    // 初始化
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.intValues();
        for (int value : values) {
            set.add(value);
        }
    }

    /**
     *
     * @param value 需要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (set.contains(value)){
            return true;
        }
        return false;
    }
}
