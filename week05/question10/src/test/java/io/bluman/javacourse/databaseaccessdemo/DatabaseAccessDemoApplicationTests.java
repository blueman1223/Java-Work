package io.bluman.javacourse.databaseaccessdemo;

import io.bluman.javacourse.databaseaccessdemo.jdbc.StudentJdbcDao;
import io.bluman.javacourse.databaseaccessdemo.table.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.List;

@SpringBootTest(classes = DatabaseAccessDemoApplication.class)
class DatabaseAccessDemoApplicationTests {
	@Autowired
	StudentJdbcDao studentJdbcDao;
	@Autowired
	JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void initDatabase() {
		jdbcTemplate.execute("DROP TABLE t_student IF EXISTS;");
		jdbcTemplate.execute("CREATE TABLE t_student(" +
				"id SERIAL, stu_no VARCHAR(255), stu_name VARCHAR(255));");
	}


	@Test
	void studentJdbcDaoTest() {
		Student stu1 = new Student();
		stu1.setStuNo("001");
		stu1.setName("student-1");
		int insertRow = studentJdbcDao.insert(stu1);
		assert insertRow == 1;
		List<Student> studentList = studentJdbcDao.selectList();
		assert studentList.size() == 1;
		Student queryRes = studentList.get(0);
		assert "001".equals(queryRes.getStuNo()) && "student-1".equals(queryRes.getName());
		queryRes.setName("student-001");
		int updateRow = studentJdbcDao.update(queryRes);
		assert updateRow == 1;
		int delRow = studentJdbcDao.delete(queryRes.getId());
		assert delRow == 1;
	}

	@Test
	void studentJdbcDaoPrepareTest() {
		Student stu1 = new Student();
		stu1.setStuNo("001");
		stu1.setName("student-1");
		int insertRow = studentJdbcDao.prepareInsert(stu1);
		assert insertRow == 1;
		List<Student> studentList = studentJdbcDao.selectList();
		assert studentList.size() == 1;
		Student queryRes = studentList.get(0);
		assert "001".equals(queryRes.getStuNo()) && "student-1".equals(queryRes.getName());
		queryRes.setName("student-001");
		int updateRow = studentJdbcDao.prepareUpdate(queryRes);
		assert updateRow == 1;
		int delRow = studentJdbcDao.prepareDelete(queryRes.getId());
		assert delRow == 1;
	}

}
