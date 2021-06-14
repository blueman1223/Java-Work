package io.bluman.javacourse.datasourceconfig;

import io.bluman.javacourse.datasourceconfig.dao.UserDao;
import io.bluman.javacourse.datasourceconfig.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = DynamicDataSourceConfigApplicationTests.class)
@SpringBootApplication
class DynamicDataSourceConfigApplicationTests {
	@Autowired
	private UserDao userDao;
	@Autowired
	private ApplicationContext context;

	@BeforeEach
	public void showBean() {
		String[] beanNames = context.getBeanDefinitionNames();
		Arrays.stream(beanNames).forEach(System.out::println);
	}


	@Test
	public void insertTest() {
		User user = new User(String.valueOf(System.currentTimeMillis()), "blueman");
		assert 1 == userDao.insert(user);
	}

	@Test
	public void selectAllTest() {
		List<User> userList = userDao.selectAll();
		System.out.println(userList);
		assert 1 == userList.size();
	}

	@Test
	public void updateTest() {
		User user =  userDao.selectAll().get(0);
		user.setUserName("blueman-update");
		assert 1 == userDao.update(user);
	}

	@Test
	public void deleteTest() {
		User user =  userDao.selectAll().get(0);
		user.setUserName("blueman-update");
		assert 1 == userDao.delete(user.getId());
	}


}
