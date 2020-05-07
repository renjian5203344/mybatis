package com.yizhan.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented


public @interface YzInsert {
    String value()default "";
}
