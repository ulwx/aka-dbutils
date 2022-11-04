package com.github.ulwx.aka.dbutils.H2.dao.db_student;

import com.github.ulwx.aka.dbutils.H2.domain.cus.One2ManyStudent;
import com.github.ulwx.aka.dbutils.H2.domain.cus.One2OneStudent;
import com.github.ulwx.aka.dbutils.database.AkaMapper;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2ManyMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.One2OneMapNestOptions;
import com.github.ulwx.aka.dbutils.database.MDMethods.PageOptions;

import java.util.List;
import java.util.Map;

public abstract class StudentMapper extends AkaMapper {
    public abstract List<One2OneStudent> getListOne2One(String[] names, One2OneMapNestOptions nestOptions);

    public abstract List<One2OneStudent> getListOne2OnePage(Map<String, Object> args, One2OneMapNestOptions nestOptions,
                                                            PageOptions pageOptions);

    public abstract List<One2ManyStudent> getListOne2Many(String[] names, One2ManyMapNestOptions nestOptions);

    public abstract List<One2ManyStudent> getListOne2ManyPage(Map<String, Object> args,
                                                              One2ManyMapNestOptions nestOptions);

    public abstract List<Integer> getPageIdList(Map<String, Object> args, PageOptions pageOptions);
}
