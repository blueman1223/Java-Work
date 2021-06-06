package io.bluman.javacourse.databaseaccessdemo.jdbc;

import io.bluman.javacourse.databaseaccessdemo.table.Student;

import java.sql.SQLException;
import java.util.List;

public interface StudentDao {
    List<Student> selectList();

    int insert(Student student);

    int update(Student student);

    int delete(String id);

    int prepareInsert(Student student);

    int prepareUpdate(Student student);

    int prepareDelete(String id);

    String SQL_SELECT_LIST = "SELECT id, stu_no, stu_name FROM t_student";
    String SQL_INSERT = "INSERT INTO t_student(stu_no, stu_name)  VALUES ('%s', '%s');";
    String SQL_UPDATE = "UPDATE t_student SET stu_no='%s', stu_name='%s' where id=%s;";
    String SQL_DELETE = "DELETE FROM t_student where id=%s;";
    String PREPARE_SQL_INSERT = "INSERT INTO t_student(stu_no, stu_name)  VALUES (?, ?);";
    String PREPARE_SQL_UPDATE = "UPDATE t_student SET stu_no=?, stu_name=? where id=?;";
    String PREPARE_SQL_DELETE = "DELETE FROM t_student where id=?;";




}
