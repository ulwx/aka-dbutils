package com.github.ulwx.aka.dbutils.tool.support.reflect;

import java.io.Serializable;

@FunctionalInterface
public interface GetFun<R> extends Serializable {
    R get();
}