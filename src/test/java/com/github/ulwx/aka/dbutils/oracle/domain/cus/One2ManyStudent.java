package com.github.ulwx.aka.dbutils.oracle.domain.cus;


import com.github.ulwx.aka.dbutils.oracle.domain.db.db_student.Course;
import com.github.ulwx.aka.dbutils.oracle.domain.db.db_student.Student;

import java.util.List;

public class One2ManyStudent extends Student {
    private List<Course> courseList;

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
}



