package com.yizhan.annotation;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public  @interface YzParam {

    String value() default "";

}
