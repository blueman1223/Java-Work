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
public class StudentHikariDao implements StudentDao {
    private final JdbcTemplate jdbcTemplate;    //默认使用hikari datasource

    @Override
    public List<Student> selectList() {
        List<Map<String, Object>> resMapList = jdbcTemplate.queryForList(SQL_SELECT_LIST);
        return resMapList.stream()
                .map(stringObjectMap -> {
                    String id = String.valueOf(stringObjectMap.get("id"));
                    String name = (String) stringObjectMap.get("stu_name");
                    String stuNo = (String) stringObjectMap.get("stu_no");
                    return new Student(id, stuNo, name);
                })
                .collect(Collectors.toList());
    }

    @Override
    public int insert(Student student) {
        String sql = String.format(SQL_INSERT, student.getStuNo(), student.getName());
        return jdbcTemplate.update(sql);
    }

    @Override
    public int update(Student student) {
        String sql = String.format(SQL_UPDATE, student.getStuNo(), student.getName(), student.getId());
        return jdbcTemplate.update(sql);
    }

    @Override
    public int delete(String id) {
        String sql = String.format(SQL_DELETE, id);
        return jdbcTemplate.update(sql);
    }

    @Override
    public int prepareInsert(Student student) {

        return jdbcTemplate.update(PREPARE_SQL_INSERT, student.getStuNo(), student.getName());
    }

    @Override
    public int prepareUpdate(Student student) {
        return jdbcTemplate.update(PREPARE_SQL_UPDATE, student.getStuNo(), student.getName(), student.getId());
    }

    @Override
    public int prepareDelete(String id) {
        return jdbcTemplate.update(PREPARE_SQL_DELETE, id);
    }
}
