package com.github.ulwx.aka.dbutils.database.spring;

import com.github.ulwx.aka.dbutils.database.DataBase;
import com.github.ulwx.aka.dbutils.database.DataBaseImpl;
import com.github.ulwx.aka.dbutils.database.MDataBase;
import com.github.ulwx.aka.dbutils.database.MDataBaseImpl;
import com.github.ulwx.aka.dbutils.database.utils.DbConst;

import javax.sql.DataSource;
import java.sql.Connection;

public class MDataBaseFactory {
    private DataSource dataSource;
    private  String tableNameRule= DbConst.TableNameRules.underline_to_camel;
    private  String tableColumRule=DbConst.TableColumRules.underline_to_camel;

    public MDataBaseFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getTableNameRule() {
        return tableNameRule;
    }

    public void setTableNameRule(String tableNameRule) {
        this.tableNameRule = tableNameRule;
    }

    public String getTableColumRule() {
        return tableColumRule;
    }

    public void setTableColumRule(String tableColumRule) {
        this.tableColumRule = tableColumRule;
    }

    public MDataBase getDatabase(Connection connection, boolean autoCommit, boolean externalControlClose){
        DataBase dataBase=new DataBaseImpl();
        dataBase.connectDb(connection,externalControlClose);
        dataBase.setAutoCommit(autoCommit);
        return new MDataBaseImpl(dataBase);

    }
}
