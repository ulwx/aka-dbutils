package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.tool.support.IOUtils;
import com.github.ulwx.aka.dbutils.tool.support.Path;
import com.github.ulwx.aka.dbutils.tool.support.path.Resource;
import com.github.ulwx.aka.dbutils.tool.support.reflect.ReflectionUtil;

import javax.sql.DataSource;

public class ShardingJDBCDBPoolImpl implements DBPool{

    public static ShardingJDBCDBPoolImpl instance=new ShardingJDBCDBPoolImpl();
    final static String className="org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory";
    final static String FILE_CONFIG_ATTR="config-file" ;
    @Override
    public DataSource getNewDataSource(DBPoolAttr dbPoolAttr) throws Exception {
        String configLocation=dbPoolAttr.getAttributes().get(FILE_CONFIG_ATTR);
        Resource[] resources=Path.getResources(configLocation);
        Path.checkResource(resources,configLocation);
        Resource resource=resources[0];

        byte[] bs=IOUtils.toByteArray(resource.getInputStream(),true);

        DataSource dataSource=(DataSource) ReflectionUtil.invoke(
                Class.forName(className),
        "createDataSource",
                byte[].class,bs);
        return dataSource;
    }

    @Override
    public void close(DataSource dataSource) throws Exception {
        ReflectionUtil.invoke(dataSource, "close");
    }

    @Override
    public String getType() {
        return PoolType.ShardingJDBC;
    }
}
