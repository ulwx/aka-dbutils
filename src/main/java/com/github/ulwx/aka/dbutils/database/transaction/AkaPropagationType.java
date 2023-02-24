package com.github.ulwx.aka.dbutils.database.transaction;

/**
 * 事务传播级别，目前只支持REQUIRED，REQUIRES_NEW，NESTED。语义与Spring的一样。
 */
public enum AkaPropagationType {
    REQUIRED, REQUIRES_NEW, NESTED
}
