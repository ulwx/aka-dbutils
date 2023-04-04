package com.github.ulwx.aka.dbutils.database.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AkaColumn {
    boolean isAutoincrement()  default  false;
    boolean isNullable() default true;

}
