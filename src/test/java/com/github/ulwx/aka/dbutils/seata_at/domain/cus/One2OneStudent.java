package com.github.ulwx.aka.dbutils.seata_at.domain.cus;

import com.github.ulwx.aka.dbutils.seata_at.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.seata_at.domain.db.db_student.Student;

public class One2OneStudent extends Student {
    private Course course;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
