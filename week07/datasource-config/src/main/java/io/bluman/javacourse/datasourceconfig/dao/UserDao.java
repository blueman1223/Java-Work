package io.bluman.javacourse.datasourceconfig.dao;

import io.bluman.javacourse.datasourceconfig.annotation.DataRead;
import io.bluman.javacourse.datasourceconfig.annotation.DataWrite;
import io.bluman.javacourse.datasourceconfig.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;


    @DataWrite
    public int insert(User user) {
        return jdbcTemplate.update("insert into t_user_info values (?, ?)",
                user.getId(), user.getUserName());
    }

    @DataWrite
    public int delete(String id) {
       return jdbcTemplate.update("delete from t_user_info where id = ?", id);
    }

    @DataRead
    public List<User> selectAll() {
        List<Map<String, Object>> resMapList = jdbcTemplate.queryForList("select * from t_user_info");
        return resMapList.stream()
                .map(stringObjectMap -> {
                    String id = String.valueOf(stringObjectMap.get("id"));
                    String name = (String) stringObjectMap.get("user_name");
                    return new User(id, name);
                })
                .collect(Collectors.toList());
    }

    @DataWrite
    public int update(User user) {
        return jdbcTemplate.update("update t_user_info set user_name = ? where id = ?",
                user.getUserName(), user.getId());
    }
}
