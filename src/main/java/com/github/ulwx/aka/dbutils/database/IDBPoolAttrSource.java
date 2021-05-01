package com.github.ulwx.aka.dbutils.database;

import java.util.Map;

public interface IDBPoolAttrSource {
       void configProperties(Map<String, String> masterProperties,Map<String, Map<String, String>> slaveServerProperties);
}
