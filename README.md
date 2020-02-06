# springboot-vue-shiro 基于Shiro前后端分离的认证与授权
提供一套基于SpringBoot-Vue-Shiro前后端分离的权限管理思路。
主要目的在于整合主流技术，寻找最佳前后端分离项目的权限管理方案，实现可直接使用的基础开发框架。


## 技术选型
选用`SpringBoot+Shiro+JWT`实现登录认证，结合`Redis`服务实现`token`的续签，前端选用`Vue`动态构造路由及更细粒度的操作权限控制。
- 前后端分离项目中，我们一般采用的是无状态登录：服务端不保存任何客户端请求者信息，客户端需要自己携带着信息去访问服务端，并且携带的信息可以被服务端辨认。
- 而`Shiro`默认的拦截跳转都是跳转`url`页面，拦截校验机制恰恰使用的`session`；而前后端分离后，后端并无权干涉页面跳转。
- 因此前后端分离项目中使用`Shiro`就需要对其进行改造，我们可以在整合`Shiro`的基础上自定义登录校验，继续整合`JWT`(或者oauth2.0等)，使其成为支持服务端无状态登录，即`token`登录。
- 在`Vue`项目中，只需要根据登录用户的权限信息动态的加载路由列表就可以动态的构造出访问菜单。


## 整体流程
- 首次通过`post`请求将用户名与密码到`login`进行登入，登录成功后返回`token`；
- 每次请求，客户端需通过`header`将`token`带回服务器做`JWT Token`的校验；
- 服务端负责`token`生命周期的刷新，用户权限的校验；

![](http://cdn.chaooo.top/Java/auth-global.png)


## 目录说明
1. serve：Java后台服务代码
2. portal：后台管理前端页面代码
3. db：SQL文件目录

写了一个系列的教程配套文章，从零开始构建:
+ [【安全认证】基于Shiro前后端分离的认证与授权(一.认证篇)](https://chaooo.github.io/article/20200118.html)
+ [【安全认证】基于Shiro前后端分离的认证与授权(二.授权篇)](https://chaooo.github.io/article/20200121.html)
+ [【安全认证】基于Shiro前后端分离的认证与授权(三.前端篇)](https://chaooo.github.io/article/20200207.html)


## 快速启动
``` shell
# 克隆项目
git clone https://github.com/chaooo/springboot-vue-shiro.git
```

导入SQL文件到数据库。
`IDEA`打开`springboot-vue-shiro/server`后端项目，`pom.xml`导包完成后直接启动。

启动前端项目
``` shell
# 进入前端项目目录
cd springboot-vue-shiro/portal
# 安装依赖
npm install --registry=https://registry.npm.taobao.org
# 启动服务
npm run dev
```


## 效果演示：
![](http://cdn.chaooo.top/Java/auth-admin.jpg)


## License
[MIT License](LICENSE)