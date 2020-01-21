package top.itdn.server;

import top.itdn.server.dao.UserDao;
import top.itdn.server.entity.User;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TestDao {

	@Resource
	private UserDao userDao;

	@Test
	public void test1() {
		System.out.println("--------test1----------");
	}



	@Test
	public void test3() {
		User user = userDao.selectByPrimaryKey(1);
		System.out.println(user.getAccount());
		//Assert.assertNotNull(user);//Assert已过时
		TestCase.assertNotNull(user);
	}




}
