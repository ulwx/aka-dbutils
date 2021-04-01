package com.github.ulwx.aka.dbutils.tool.support.reflect;

import java.io.Serializable;

@FunctionalInterface
public interface CGetFun<T, R> extends Serializable {
    R apply(T t);
}