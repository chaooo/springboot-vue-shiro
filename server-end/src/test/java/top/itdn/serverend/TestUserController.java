package top.itdn.serverend;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;
import top.itdn.serverend.controller.UserController;
import top.itdn.serverend.entity.vo.ResponseVo;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=ServerEndApplication.class)
public class TestUserController {

    @Autowired
    private UserController controller;
    /**
     * 测试SpringMVC处理流程 主要逻辑
     * @param uri
     * @param params
     * @param status
     * @throws Exception
     */
    private void testHttp(String type, String uri, MultiValueMap<String, String> params,  int status, String token) throws Exception {
        //创建MockMvc对象，可以发送HTTP请求，接收响应结果
        MockMvc mock = MockMvcBuilders.standaloneSetup(controller).build();
        //使用mock对象发送POST请求
        RequestBuilder request = null;
        if("post".equals(type)) {
            if(params==null) request = MockMvcRequestBuilders.post(uri).header("token", token!=null?token:"test");
            else request = MockMvcRequestBuilders.post(uri).params(params).header("token", token!=null?token:"test");
        }
        if("get".equals(type)) {
            if(params==null) request = MockMvcRequestBuilders.get(uri).header("token", token!=null?token:"test");
            else request = MockMvcRequestBuilders.get(uri).params(params).header("token", token!=null?token:"test");
        }
        MvcResult result = mock.perform(request).andReturn();
        //获取返回的结果
        String jsonStr = result.getResponse().getContentAsString();
        //将返回jsonStr结果转成ResultJson对象
        ObjectMapper mapper = new ObjectMapper();
        ResponseVo resultJson = mapper.readValue(jsonStr, ResponseVo.class);

        System.out.println(resultJson);
        //使用断言比对结果和预期
        TestCase.assertEquals(status, resultJson.getCode());
    }

    //测试注册
    @Test
    public void testRegister() throws Exception {
        //配置参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("name", "chao2");
        params.set("password", "123456");
        //测试
        testHttp("post", "/regist", params, 0, null);
    }

    //测试登录
    @Test
    public void testLogin() throws Exception {
        //配置参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("name", "chao2");
        params.set("password", "123456");
        //测试
        testHttp("post", "/login", params, 0, null);
    }


    //测试token
    @Test
    public void testJWT() throws Exception {
        //配置参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJjaGFvIiwidWlkIjoyOSwiZXhwIjoxNTY3OTM2NzgwfQ.6zvimBNs_MCiov4MOkkUodgKmRFBS2dVhmhIb1MV6m4");
        //测试
        testHttp("post", "/token", params, 0, null);
    }

    //测试用户资料更新
    @Test
    public void testUPDATE() throws Exception {
        //配置参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("nick_name", "test");
        params.set("sex", "男");
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJjaGFvIiwidWlkIjoyOSwiZXhwIjoxNTY3OTM2NzgwfQ.6zvimBNs_MCiov4MOkkUodgKmRFBS2dVhmhIb1MV6m4";
        //测试
        testHttp("post", "/update", params, 0, token);
    }

    //测试用户修改密码
    @Test
    public void testPWD() throws Exception {
        //配置参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("password", "12345678");
        params.set("new_password", "123456");
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJjaGFvIiwidWlkIjoyOSwiZXhwIjoxNTY3OTM2NzgwfQ.6zvimBNs_MCiov4MOkkUodgKmRFBS2dVhmhIb1MV6m4";
        //测试
        testHttp("post", "/password", params, 0, token);
    }

    //登陆记录
    @Test
    public void testloadLoginHistory() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJjaGFvIiwidWlkIjoyOSwiZXhwIjoxNTY4MDIwNDkwfQ.5RYMMPbvTxdVdpdm2GO2bX7oCE7VadygUhTTaILnFUY";
        //测试
        testHttp("get", "/history/login", null, 0, token);
    }

}
