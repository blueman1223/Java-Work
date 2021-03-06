package io.bluman.javacourse.databaseaccessdemo;

import io.bluman.javacourse.databaseaccessdemo.jdbc.StudentSimpleDao;
import io.bluman.javacourse.databaseaccessdemo.table.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@SpringBootTest(classes = DatabaseAccessDemoApplication.class)
class DatabaseAccessDemoApplicationTests {
	@Autowired
	StudentSimpleDao simpleDao;
	@Resource(name = "simpleConnection")
	Connection conn;

	@BeforeEach
	public void initDatabase() {
		try (Statement statement = conn.createStatement()) {
			statement.execute("DROP TABLE t_student IF EXISTS;");
			statement.execute("CREATE TABLE t_student(" +
					"id SERIAL, stu_no VARCHAR(255), stu_name VARCHAR(255));");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}


	@Test
	void studentJdbcDaoTest() {
		Student stu1 = new Student();
		stu1.setStuNo("001");
		stu1.setName("student-1");
		int insertRow = simpleDao.insert(stu1);
		assert insertRow == 1;
		List<Student> studentList = simpleDao.selectList();
		assert studentList.size() == 1;
		Student queryRes = studentList.get(0);
		assert "001".equals(queryRes.getStuNo()) && "student-1".equals(queryRes.getName());
		queryRes.setName("student-001");
		int updateRow = simpleDao.update(queryRes);
		assert updateRow == 1;
		int delRow = simpleDao.delete(queryRes.getId());
		assert delRow == 1;
	}

	@Test
	void studentJdbcDaoPrepareTest() {
		Student stu1 = new Student();
		stu1.setStuNo("001");
		stu1.setName("student-1");
		int insertRow = simpleDao.prepareInsert(stu1);
		assert insertRow == 1;
		List<Student> studentList = simpleDao.selectList();
		assert studentList.size() == 1;
		Student queryRes = studentList.get(0);
		assert "001".equals(queryRes.getStuNo()) && "student-1".equals(queryRes.getName());
		queryRes.setName("student-001");
		int updateRow = simpleDao.prepareUpdate(queryRes);
		assert updateRow == 1;
		int delRow = simpleDao.prepareDelete(queryRes.getId());
		assert delRow == 1;
	}

}
