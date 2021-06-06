package io.bluman.javacourse.databaseaccessdemo.jdbc;

import io.bluman.javacourse.databaseaccessdemo.table.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StudentJdbcDao {
    private final JdbcTemplate jdbcTemplate;

    public List<Student> selectList() {
        String sql = "SELECT id, stu_no, stu_name FROM t_student";
        List<Map<String, Object>> resMapList = jdbcTemplate.queryForList(sql);
        return resMapList.stream()
                .map(stringObjectMap -> {
                    String id = String.valueOf(stringObjectMap.get("id"));
                    String name = (String) stringObjectMap.get("stu_name");
                    String stuNo = (String) stringObjectMap.get("stu_no");
                    return new Student(id, stuNo, name);
                })
                .collect(Collectors.toList());
    }

    public int insert(Student student) {
        String sql = "INSERT INTO t_student(stu_no, stu_name)  VALUES ('"+student.getStuNo() + "', '" + student.getName() +"');";
        return jdbcTemplate.update(sql);
    }

    public int update(Student student) {
        String sql = "UPDATE t_student SET stu_no='"+student.getStuNo()+"', stu_name='"+student.getName()+"' where id="+student.getId()+";";
        return jdbcTemplate.update(sql);
    }

    public int delete(String id) {
        String sql = "DELETE FROM t_student where id=" + id + ";";
        return jdbcTemplate.update(sql);
    }
}
