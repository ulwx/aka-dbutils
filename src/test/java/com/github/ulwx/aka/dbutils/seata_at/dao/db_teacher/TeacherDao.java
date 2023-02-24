package com.github.ulwx.aka.dbutils.seata_at.dao.db_teacher;

import com.github.ulwx.aka.dbutils.seata_at.domain.db.db_teacher.Teacher;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/**
 * 测试aka-dbutils的数据库主从的支持
 */
public class TeacherDao {
    public static String DbPoolName = "seata_at/dbpool.xml#db_teacher";



    public void testUpdateInManager(String name) {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setName(name);
        int ret = MDbUtils.updateBy(DbPoolName, teacher, MD.of("id"));

    }

  


  


}
