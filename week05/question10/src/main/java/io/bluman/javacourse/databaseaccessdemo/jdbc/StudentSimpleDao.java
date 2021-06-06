package io.bluman.javacourse.databaseaccessdemo.jdbc;

import io.bluman.javacourse.databaseaccessdemo.table.Student;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentSimpleDao implements StudentDao {
    @Resource(name = "simpleConnection")
    private Connection conn;


    @Override
    public List<Student> selectList() {
        try (Statement statement = conn.createStatement()) {
          try (ResultSet rs = statement.executeQuery(SQL_SELECT_LIST)) {
              List<Student> stuList = new ArrayList<>();
              while (rs.next()) {
                  String id = String.valueOf(rs.getInt("id"));
                  String stuNo = rs.getString("stu_no");
                  String stuName = rs.getString("stu_name");
                  Student resStu = new Student(id, stuNo, stuName);
                  stuList.add(resStu);
              }
              return stuList;
          }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public int insert(Student student) {
        try (Statement statement = conn.createStatement()) {
            String sql = String.format(SQL_INSERT, student.getStuNo(), student.getName());
            return statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public int update(Student student) {
        try (Statement statement = conn.createStatement()) {
            String sql = String.format(SQL_UPDATE, student.getStuNo(), student.getName(), student.getId());
            return statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public int delete(String id) {
        try (Statement statement = conn.createStatement()) {
            String sql = String.format(SQL_DELETE, id);
            return statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public int prepareInsert(Student student) {
        try (PreparedStatement statement = conn.prepareStatement(PREPARE_SQL_INSERT)) {
            statement.setString(1, student.getStuNo());
            statement.setString(2, student.getName());
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public int prepareUpdate(Student student) {
        try (PreparedStatement statement = conn.prepareStatement(PREPARE_SQL_UPDATE)) {
            statement.setString(1, student.getStuNo());
            statement.setString(2, student.getName());
            statement.setString(3, student.getId());
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public int prepareDelete(String id) {
        try (PreparedStatement statement = conn.prepareStatement(PREPARE_SQL_DELETE)) {
            statement.setString(1, id);
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }
}
