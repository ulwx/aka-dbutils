package com.github.ulwx.aka.dbutils.database.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AkaParam {
    String value() default "";
}
