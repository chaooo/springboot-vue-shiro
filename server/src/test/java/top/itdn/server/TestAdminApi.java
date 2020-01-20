package top.itdn.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import top.itdn.server.controller.AdminApi;
import top.itdn.server.utils.ResponseVo;

import java.util.Date;

/**
 * @author : Charles
 * @description : Description
 * @date : 2020/1/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ServerApplication.class)
public class TestAdminApi {

	@Autowired
	private AdminApi controller;
/**
	 * 测试SpringMVC处理流程 主要逻辑
	 * @param uri
	 * @param params
	 * @param eCode
	 * @throws Exception
	 */
	private void testHttp(String type, String uri, MultiValueMap<String, String> params, int eCode, String token) throws Exception {
		//创建MockMvc对象，可以发送HTTP请求，接收响应结果
		MockMvc mock = MockMvcBuilders.standaloneSetup(controller).build();
		//使用mock对象发送POST请求
		RequestBuilder request = null;
		if("post".equals(type)) {
			if(params==null) request = MockMvcRequestBuilders.post(uri).header("X-Token", token!=null?token:"test");
			else request = MockMvcRequestBuilders.post(uri).params(params).header("X-Token", token!=null?token:"test");
		}
		if("get".equals(type)) {
			if(params==null) request = MockMvcRequestBuilders.get(uri).header("X-Token", token!=null?token:"test");
			else request = MockMvcRequestBuilders.get(uri).params(params).header("X-Token", token!=null?token:"test");
		}
		MvcResult result = mock.perform(request).andReturn();
		//获取返回的结果
		String jsonStr = result.getResponse().getContentAsString();
		//将返回jsonStr结果转成ResultJson对象
		ObjectMapper mapper = new ObjectMapper();
		ResponseVo resultJson = mapper.readValue(jsonStr, ResponseVo.class);

		System.out.println(resultJson);
		//使用断言比对结果和预期
		TestCase.assertEquals(eCode, resultJson.getCode());
	}

	//测试注册
	@Test
	public void testRegister() throws Exception {
		//配置参数
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.set("account", "user2");
		params.set("password", "123456");
		//测试
		testHttp("post", "/user/regist", params, 0, null);
	}

	//测试登录
	@Test
	public void testLogin() throws Exception {
		//配置参数
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.set("account", "user1");
		params.set("password", "123456");
		//测试
		testHttp("post", "/user/login", params, 0, null);
	}
}
