package com.struggle.common.core.utils;


import com.struggle.common.core.exception.BusinessException;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 校验对象
 * @Description:
 * @Author: shankui.xu@wisdragon.com
 * @UpdateDate: 2020/07/04 17:37
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ValidatorUtil {

    private static final Validator validator = Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();

    public ValidatorUtil() {}
    /**
     * 返回错误信息-批量导入用
     * @param object
     * @param groups
     * @return
     */
    public static <T> String validBean(T object, Class<?>... groups) {
        StringBuffer _buffer = new StringBuffer();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object, groups);
        if(constraintViolations.size()>0){
            for(ConstraintViolation<?> constraintViolation:constraintViolations){
                _buffer.append("[");
                _buffer.append(constraintViolation.getMessage());
                _buffer.append("]");
            }
        }
        return _buffer.toString();
    }
    /**
     * 抛出错误信息-新增或编辑用
     * @param object
     * @param groups
     * @return
     */
    public static <T> void _validBean(T object, Class<?>... groups) {
        String msg = ValidatorUtil.validBean(object, groups);
        if(StringUtils.hasText(msg)){
            throw new BusinessException("参数错误："+ msg);
        }
    }
}
