# springboot-vue-shiro 基于Shiro前后端分离的认证与授权
## 一、认证

### 1. 开始之前
#### 1.1 技术选型
选用`SpringBoot+Shiro+JWT`实现登录认证，结合`Redis`服务实现`token`的续签，前端选用`Vue`动态构造路由及更细粒度的操作权限控制。
- 前后端分离项目中，我们一般采用的是无状态登录：服务端不保存任何客户端请求者信息，客户端需要自己携带着信息去访问服务端，并且携带的信息可以被服务端辨认。
- 而`Shiro`默认的拦截跳转都是跳转`url`页面，拦截校验机制恰恰使用的`session`；而前后端分离后，后端并无权干涉页面跳转。
- 因此前后端分离项目中使用`Shiro`就需要对其进行改造，我们可以在整合`Shiro`的基础上自定义登录校验，继续整合`JWT`(或者oauth2.0等)，使其成为支持服务端无状态登录，即`token`登录。
- 在`Vue`项目中，只需要根据登录用户的权限信息动态的加载路由列表就可以动态的构造出访问菜单。<!-- more -->

#### 1.2 整体流程
- 首次通过`post`请求将用户名与密码到`login`进行登入，登录成功后返回`token`；
- 每次请求，客户端需通过`header`将`token`带回服务器做`JWT Token`的校验；
- 服务端负责`token`生命周期的刷新，用户权限的校验；

![](http://cdn.chaooo.top/Java/auth-global.png)

### 2. SpringBoot整合Shiro+JWT
这里贴出主要逻辑，源码请移步文章末尾获取。
1. 数据表

``` sql
/** 系统用户表 */
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user(
    id INT AUTO_INCREMENT COMMENT '用户ID',
    account VARCHAR(30) NOT NULL COMMENT '用户名',
    PASSWORD VARCHAR(50) COMMENT '用户密码',
    salt VARCHAR(8) COMMENT '随机盐',
    nickname VARCHAR(30) COMMENT '用户昵称',
    roleId INT COMMENT '角色ID',
    createTime DATE COMMENT '创建时间',
    updateTime DATE COMMENT '更新时间',
    deleteStatus VARCHAR(2) DEFAULT '1' COMMENT '是否有效：1有效，2无效',
    CONSTRAINT sys_user_id_pk PRIMARY KEY(id),
    CONSTRAINT sys_user_account_uk UNIQUE(account)
);
COMMIT;
```

2. pom.xml

``` xml
<!-- JWT -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.8.3</version>
</dependency>
<!-- shiro -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.4.2</version>
</dependency>
```

3. `shiro`配置类：构建`securityManager`环境，及配置`shiroFilter`并将`jwtFilter`添加进`shiro`的拦截器链中，放行登录注册请求。

``` java
@Configuration
public class ShiroConfig {
    @Bean("securityManager")
    public DefaultWebSecurityManager getManager(MyRealm myRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 使用自己的realm
        securityManager.setRealm(myRealm);
        /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean factory(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        // 拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置不会被拦截的链接 顺序判断，规则：http://shiro.apache.org/web.html#urls-
        filterChainDefinitionMap.put("/register", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/unauthorized", "anon");

        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new HashMap<>(1);
        filterMap.put("jwt", new JwtFilter());
        factoryBean.setFilters(filterMap);

        // 过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        filterChainDefinitionMap.put("/**", "jwt");
        // 未授权返回
        factoryBean.setUnauthorizedUrl("/unauthorized");

        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return factoryBean;
    }
    /**
     * 添加注解支持
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
```

4. 自定义`Realm`：继承`AuthorizingRealm`类，在其中实现登陆验证及权限获取的方法。

``` java
@Slf4j
@Component("MyRealm")
public class MyRealm extends AuthorizingRealm {
    /** 注入SysService */
    private SysService sysService;
    @Autowired
    public void setSysService(SysService sysService) {
        this.sysService = sysService;
    }
    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
    /**
     * 用来进行身份认证，也就是说验证用户输入的账号和密码是否正确，
     * 获取身份验证信息，错误抛出异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        log.info("————————身份认证——————————");
        String token = (String) auth.getCredentials();
        if (null == token || !JwtUtil.isVerify(token)) {
            throw new AuthenticationException("token无效!");
        }
        // 解密获得username，用于和数据库进行对比
        String account = JwtUtil.parseTokenAud(token);
        User user = sysService.selectByAccount(account);
        if (null == user) {
            throw new AuthenticationException("用户不存在!");
        }
        return new SimpleAuthenticationInfo(user, token,"MyRealm");
    }
    /**
     * 获取用户权限信息，包括角色以及权限。
     * 只有当触发检测用户权限时才会调用此方法，例如checkRole,checkPermissionJwtToken
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("————权限认证 [ roles、permissions]————");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        /* 暂不编写，此处编写后，controller中可以使用@RequiresPermissions来对用户权限进行拦截 */
        return simpleAuthorizationInfo;
    }
}

```

5. 鉴权登录过滤器：继承`BasicHttpAuthenticationFilter`类,该拦截器需要拦截所有请求除(除登陆、注册等请求)，用于判断请求是否带有`token`，并获取`token`的值传递给`shiro`的登陆认证方法作为参数，用于获取`token`；

``` java
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            executeLogin(request, response);
            return true;
        } catch (Exception e) {
            unauthorized(response);
            return false;
        }
    }
    /**
     * 认证
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("X-Token");
        JwtToken token = new JwtToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        return true;
    }
    /**
     * 认证失败 跳转到 /unauthorized
     */
    private void unauthorized(ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.sendRedirect("/unauthorized");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
```

6. `JwtToken`

``` java
public class JwtToken implements AuthenticationToken {
    private String token;
    JwtToken(String token) {
        this.token = token;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }
    @Override
    public Object getCredentials() {
        return token;
    }
}
```

7. `JWT`工具类：利用登陆信息生成`token`，根据`token`获取`username`，`token`验证等方法。

``` java
public class JwtUtil {
    /** 设置过期时间: 30分钟 */
    private static final long EXPIRE_TIME = 30 * 60 * 1000;
    /** 服务端的私钥secret,在任何场景都不应该流露出去 */
    private static final String TOKEN_SECRET = "zhengchao";
    /**
     * 生成签名，30分钟过期
     */
    public static String createToken(User user) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("aud", user.getAccount())
                    .withClaim("uid", user.getId())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 检验token是否正确
     */
    public static boolean isVerify(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    /**
     *从token解析出uid信息,用户ID
     */
    public static int parseTokenUid(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("uid").asInt();
    }
    /**
     *从token解析出aud信息,用户名
     */
    public static String parseTokenAud(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("aud").asString();
    }
    /**
     *从token解析出过期时间
     */
    public static Date paraseExpiresTime(String token){
        DecodedJWT jwt = JWT.decode(token);
        return  jwt.getExpiresAt();
    }
}
```

8. MD5加密工具类

``` java
public class Md5Util {
    /**
     * md5加密
     * @param s：待加密字符串
     * @return 加密后16进制字符串
     */
    public static String md5(String s) {
        try {
            //实例化MessageDigest的MD5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //通过digest方法返回哈希计算后的字节数组
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            //将字节数组转换为16进制字符串并返回
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 获取随即盐
     */
    public static String salt(){
        //利用UUID生成随机盐
        UUID uuid = UUID.randomUUID();
        //返回a2c64597-232f-4782-ab2d-9dfeb9d76932
        String[] arr = uuid.toString().split("-");
        return arr[0];
    }
    /**
     * 字节数组转换为16进制字符串
     * @param bytes数组
     * @return 16进制字符串
     */
    private static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }
}
```

### 3. 注册与登录主要逻辑
这里只贴出主要逻辑，`DAO`和`Mapper`映射可查看源码，源码请移步文章末尾获取。
1. 登录`Controller`

``` java
@RestController
public class SysApi {
    /**
     * 注入服务类
     */
    private SysService sysService;
    @Autowired
    public void setSysService(SysService sysService) {
        this.sysService = sysService;
    }
    /**
     * 注册(用户名，密码)
     * @param account
     * @param password
     * @return
     */
    @PostMapping("/register")
    public ResponseVo<String> register(String account, String password) {
        return sysService.register(account, password);
    }
    /**
     * 登录(用户名，密码)
     * @param account
     * @param password
     * @return
     */
    @PostMapping("/login")
    public ResponseVo<String> login(String account, String password) {
        return sysService.login(account, password);
    }
    /**
     * 处理非法请求
     */
    @GetMapping("/unauthorized")
    public ResponseVo unauthorized(HttpServletRequest request) {
        return new ResponseVo(-1, "Token失效请重新登录!");
    }
}
```

2. Service

``` java
public interface SysService {
    /**
     * 注册(用户名，密码)
     */
    ResponseVo<String> register(String account, String password);
    /**
     * 登录(用户名，密码)
     */
    ResponseVo<String> login(String account, String password);
    /**
     * 根据account查找用户，自定义Realm中调用
     */
    User selectByAccount(String account);
}
/**
 * 实现类
 */
@Service
public class SysServiceImpl implements SysService {

    private SysDao sysDao;
    /**
     * 注入DAO
     */
    @Autowired
    public void setSysDao(SysDao sysDao) {
        this.sysDao = sysDao;
    }
    /**
     * 用户注册(用户名，密码)
     *
     * @param account 用户名
     * @param password 密码
     * @return token
     */
    @Override
    public ResponseVo<String> register(String account, String password) {
        //检查用户名是否被占用
        User user = sysDao.selectByAccount(account);
        if(user!=null) {
            return new ResponseVo<>( -1, "用户名被占用");
        }
        //添加用户信息
        user = new User();
        //设置用户名
        user.setAccount(account);
        //密码加密后再保存
        String salt = Md5Util.salt();
        String md5Password = Md5Util.md5(password+salt);
        user.setPassword(md5Password);
        user.setSalt(salt);
        //设置注册时间
        user.setCreatetime(new Date());
        //添加到数据库
        int row = sysDao.insertSelective(user);
        //返回信息
        if(row>0) {
            //生成token给用户
            String token = JwtUtil.createToken(user);
            return new ResponseVo<>(0,"注册成功", token);
        }else {
            return new ResponseVo<>( -1, "注册失败");
        }
    }
    /**
     * 用户登录(用户名，密码)
     *
     * @param account 用户名
     * @param password 密码
     * @return token
     */
    @Override
    public ResponseVo<String> login(String account, String password) {
        //处理比对密码
        User user = sysDao.selectByAccount(account);
        if(user!=null) {
            String  salt = user.getSalt();
            String md5Password = Md5Util.md5(password+salt);
            String dbPassword = user.getPassword();
            if(md5Password.equals(dbPassword)) {
                //生成token给用户
                String token = JwtUtil.createToken(user);
                return new ResponseVo<>(0,"登录成功", token);
            }
        }
        return new ResponseVo<>( -1, "登录失败");
    }
    /**
     * 根据account查找用户，自定义Realm中调用
     *
     * @param account
     * @return User
     */
    @Override
    public User selectByAccount(String account) {
        return sysDao.selectByAccount(account);
    }
}
```

3. 统一接口返回格式

``` java
public class ResponseVo<T> {
    /** 状态码 */
    private int code;
    /** 提示信息 */
    private String msg;
    /** 返回的数据 */
    private T data;
    public ResponseVo() {}
    public ResponseVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ResponseVo(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
```

> 注：这里的登录认证逻辑在`github`源码`tag`的`V1.0`中，后续版本再加入`Token`续签和`shiro`前后端权限管理等。
> 源码地址: [https://github.com/chaooo/springboot-vue-shiro.git](https://github.com/chaooo/springboot-vue-shiro.git)
> 仅下载认证逻辑源码:
> `git clone --branch V1.0 https://github.com/chaooo/springboot-vue-shiro.git`


================================================================================

## 二、权限控制
前面我们整合了`SpringBoot+Shiro+JWT`实现了登录认证，但还没有实现权限控制，这是接下来的工作。

### 1. JWT的Token续签
#### 1.1 续签思路
1. 业务逻辑：
    + 登录成功后，用户在未过期时间内继续操作，续签token。
    + 登录成功后，空闲超过过期时间，返回token已失效，重新登录。
2. 实现逻辑：
    1. 登录成功后将token存储到redis里面(这时候k、v值一样都为token)，并设置过期时间为token过期时间
    2. 当用户请求时token值还未过期，则重新设置redis里token的过期时间。
    3. 当用户请求时token值已过期，但redis中还在，则JWT重新生成token并覆盖v值(这时候k、v值不一样了)，然后设置redis过期时间。
    4. 当用户请求时token值已过期，并且redis中也不存在，则用户空闲超时，返回token已失效，重新登录。

#### 1.2 编码实现
1. `pom.xml`引入`Redis`

``` xml
<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.8.0</version>
</dependency>
```

2. 编写`Redis`工具类

``` java
@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 指定缓存失效时间
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
    /**
     * 普通缓存放入并设置时间
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
```

3. JwtUtil中增加返回过期秒数的方法

``` java
public class JwtUtil {
    /** 设置过期时间: 30分钟 */
    private static final long EXPIRE_TIME = 30 * 60 * 1000;
    //... 其他代码省略
    /**
     * 返回设置的过期秒数
     * @return long 秒数
     */
    public static long getExpireTime(){
        return  EXPIRE_TIME/1000;
    }
}
```

4. 改写登录逻辑，生成`token`后存入`Redis`

``` java
@Service
public class SysServiceImpl implements SysService {
    private String getToken(User user){
        // 生成token
        String token = JwtUtil.createToken(user);
        // 为了过期续签，将token存入redis，并设置超时时间
        redisUtil.set(token, token, JwtUtil.getExpireTime());
        return token;
    }
     * 用户登录(用户名，密码)
     *
     * @param account 用户名
     * @param password 密码
     * @return token
     */
    @Override
    public ResponseVo<String> login(String account, String password) {
        //处理比对密码
        User user = sysDao.selectByAccount(account);
        if(user!=null) {
            String  salt = user.getSalt();
            String md5Password = Md5Util.md5(password+salt);
            String dbPassword = user.getPassword();
            if(md5Password.equals(dbPassword)) {
                //生成token给用户，并存入redis
                String token = getToken(user);
                return new ResponseVo<>(0,"登录成功", token);
            }
        }
        return new ResponseVo<>( -1, "登录失败");
    }
}
```

5. 改写`MyRealm`，加入`token`续签逻辑

``` java
@Slf4j
@Component("MyRealm")
public class MyRealm extends AuthorizingRealm {
    /**
     * JWT Token续签：
     * 业务逻辑：登录成功后，用户在未过期时间内继续操作，续签token。
     *         登录成功后，空闲超过过期时间，返回token已失效，重新登录。
     * 实现逻辑：
     *    1.登录成功后将token存储到redis里面(这时候k、v值一样都为token)，并设置过期时间为token过期时间
     *    2.当用户请求时token值还未过期，则重新设置redis里token的过期时间。
     *    3.当用户请求时token值已过期，但redis中还在，则JWT重新生成token并覆盖v值(这时候k、v值不一样了)，然后设置redis过期时间。
     *    4.当用户请求时token值已过期，并且redis中也不存在，则用户空闲超时，返回token已失效，重新登录。
     */
    public boolean tokenRefresh(String token, User user) {
        String cacheToken = String.valueOf(redisUtil.get(token));
        // 过期后会得到"null"值，所以需判断字符串"null"
        if (cacheToken != null && cacheToken.length() != 0 && !"null".equals(cacheToken)) {
            // 校验token有效性
            if (!JwtUtil.isVerify(cacheToken)) {
                // 生成token
                String newToken = JwtUtil.createToken(user);
                // 将token存入redis,并设置超时时间
                redisUtil.set(token, newToken, JwtUtil.getExpireTime());
            } else {
                // 重新设置超时时间
                redisUtil.expire(token, JwtUtil.getExpireTime());
            }
            log.info("打印存入redis的过期时间："+redisUtil.getExpire(token));
            return true;
        }
        return false;
    }
    /**
     * 重写认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        log.info("————————身份认证——————————");
        String token = (String) auth.getCredentials();
        if (null == token) {
            throw new AuthenticationException("token为空!");
        }
        // 解密获得username，用于和数据库进行对比
        String account = JwtUtil.parseTokenAud(token);
        User user = sysService.selectByAccount(account);
        if (null == user) {
            throw new AuthenticationException("用户不存在!");
        }
        // 校验token是否过期
        if (!tokenRefresh(token, user)) {
            throw new AuthenticationException("Token已过期!");
        }
        return new SimpleAuthenticationInfo(user, token,"MyRealm");
    }
}
```

到此，JWT的Token续签的功能已经全部实现了。


### 2. 权限管理
#### 2.1 首先增加三张数据表
``` sql
/** 角色表 */
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_name` VARCHAR(100) DEFAULT NULL COMMENT '角色名称',
  `description` VARCHAR(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色表';
INSERT  INTO `sys_role`(`id`,`role_name`,`description`) VALUES (1,'admin','管理角色'),(2,'user','用户角色');
/** 权限表 */
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` VARCHAR(32) NOT NULL COMMENT '主键id',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '菜单标题',
  `url` VARCHAR(255) DEFAULT NULL COMMENT '路径',
  `menu_type` INT(11) DEFAULT NULL COMMENT '菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)',
  `perms` VARCHAR(255) DEFAULT NULL COMMENT '菜单权限编码',
  `sort_no` INT(10) DEFAULT NULL COMMENT '菜单排序',
  `del_flag` INT(1) DEFAULT '0' COMMENT '删除状态 0正常 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_prem_sort_no` (`sort_no`) USING BTREE,
  KEY `index_prem_del_flag` (`del_flag`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单权限表';
INSERT  INTO `sys_permission`(`id`,`name`,`url`,`menu_type`,`perms`,`sort_no`,`del_flag`) VALUES ('1','新增用户','/user/add',2,'user:add',1,0),('2','删除用户','/user/delete',2,'user:delete',2,0),('3','修改用户','/user/update',2,'user:update',3,0),('4','查询用户','/user/list',2,'user:list',4,0);
/** 角色与权限关联表 */
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `role_id` INT(11) DEFAULT NULL COMMENT '角色id',
  `permission_id` INT(11) DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_group_role_per_id` (`role_id`,`permission_id`) USING BTREE,
  KEY `index_group_role_id` (`role_id`) USING BTREE,
  KEY `index_group_per_id` (`permission_id`) USING BTREE
) ENGINE=INNODB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色权限表';
INSERT  INTO `sys_role_permission`(`id`,`role_id`,`permission_id`) VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,2,4);
```


#### 2.2 编码实现
1. 补全`MyRealm`中授权验证逻辑

``` java
@Slf4j
@Component("MyRealm")
public class MyRealm extends AuthorizingRealm {
    //...其他代码省略
/**
     * 获取用户权限信息，包括角色以及权限。
     * 只有当触发检测用户权限时才会调用此方法，例如checkRole,checkPermissionJwtToken
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("————权限认证 [ roles、permissions]————");
        User user = null;
        if (principals != null) {
            user = (User) principals.getPrimaryPrincipal();
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        if (user != null) {
            // 用户拥有的角色，比如“admin/user”
            String role = sysService.getRoleByRoleid(user.getRoleid());
            simpleAuthorizationInfo.addRole(role);
            log.info("角色为："+role);
            // 用户拥有的权限集合，比如“role:add,user:add”
            Set<String> permissions = sysService.getPermissionsByRoleid(user.getRoleid());
            simpleAuthorizationInfo.addStringPermissions(permissions);
            log.info("权限有："+permissions.toString());
        }
        return simpleAuthorizationInfo;
    }
}
```

2. `Service`中添加获取角色与权限的方法，DAO与Mapper请移步源码。

``` java
public interface SysService {
	/**
	 * 根据roleid查找用户角色名，自定义Realm中调用
	 * @param roleid
	 * @return roles
	 */
	String getRoleByRoleid(Integer roleid);

	/**
	 * 根据roleid查找用户权限，自定义Realm中调用
	 * @param roleid
	 * @return  Set<permissions>
	 */
	Set<String> getPermissionsByRoleid(Integer roleid);
}
/**
 * 实现类
 */
@Service
public class SysServiceImpl implements SysService {
    @Override
    public String getRoleByRoleid(Integer roleid) {
        return sysDao.getRoleByRoleid(roleid);
    }
    @Override
    public Set<String> getPermissionsByRoleid(Integer roleid) {
        return sysDao.getPermissionsByRoleid(roleid);
    }
}
```

3. `Controller`中使用`@RequiresPermissions`来控制权限

``` java
@RestController
public class UserApi {
	/**
	 * 获取所有用户信息
	 * @return
	 */
	@RequiresPermissions("user:list")
	@GetMapping("/user/list")
	public ResponseVo list() {
		return userService.loadUser();
	}
	/**
	 * 用户更新资料
	 * @param user
	 * @return
	 */
	@RequiresPermissions("user:update")
	@PostMapping("/user/update")
	public ResponseVo update(User user, HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		return userService.modifyUser(token, user);
	}
}
```


> 注：这里的登录认证+授权控制 在`github`源码`tag`的`V2.0`中，后续版本再加入前端动态路由控制等。
> 源码地址: [https://github.com/chaooo/springboot-vue-shiro.git](https://github.com/chaooo/springboot-vue-shiro.git)
> 仅下载后端认证+授权控制源码:
> `git clone --branch V2.0 https://github.com/chaooo/springboot-vue-shiro.git`

