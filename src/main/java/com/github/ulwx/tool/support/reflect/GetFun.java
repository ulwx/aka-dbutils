package com.github.ulwx.tool.support.reflect;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface GetFun<T, R> extends Supplier<R>, Serializable {

}