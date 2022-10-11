package com.github.ulwx.aka.dbutils.database;

import java.util.Map;

/**
 * 获取数据源接口
 */
public interface IDBPoolAttrSource {
    /**
     * 实现此方法提供了一种手段可以不通过xml配置获取数据源信息，而通过实现此方法通过外部源来获取数据源信息，例如可以把数据源信息
     * 配置到数据库里或文件里，再通过实现此方法获取;或者也可以实现数据源信息的加密，例如可以把数据源信息以加密的方式存储在外部，
     * 再通过此方法获取解密。如果masterProperties返回空并且slaveServerProperties返回空，则不执行。
     *
     * @param masterProperties 返回的master的数据源信息
     * @param slaveServerProperties 返回的slave的数据源信息
     */
    void configProperties(Map<String, String> masterProperties, Map<String, Map<String, String>> slaveServerProperties);
}
