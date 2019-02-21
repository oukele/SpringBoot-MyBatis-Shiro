`Shiro是一个强大且易用的Java安全框架`

1.  Authentication 认证 --用户登录
2.  Authorization 授权   --用户具有哪些权限
3.  Cryptography 安全数据加密
4.  Session Management 会话管理 

#### 分析Shiro的核心API

1. Subject：用户主体（把操作交给SecurityManager）
2. SecurityManager：安全管理器（管理Realm）
3. Realm：Shiro连接数据的桥梁

#### SpringBoot 整合 Shiro

1. shiro与spring整合依赖

2. 编写 shiro 配置类

   1. 创建 ShiroFilterFactoryBean（简称 Subject 把操作 交给 SecurityManager）
   2. 创建DefaultWebSecurityManager （简称 SecurityManager 并且管理 Realm）
   3. 创建 realm （shiro连接数据的桥梁）

3. 自定义Realm类

   1. 继承 AuthorizingRealm 类 
   2. 重写AuthorizingRealm 类中 两个主要方法
      1. doGetAuthorizationInfo （ 执行 授权逻辑 ）
      2. doGetAuthenticationInfo （执行 认证逻辑）

4. 使用Shiro 内置过滤器实现url拦截

   ~~~ java
   /**
    * Shiro的配置类
    * @author OYL
    */
   
   @Configuration
   public class ShiroConfig {
       /**
        *  创建 ShiroFilterFactoryBean
        */
       @Bean
       public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
           ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
   
           //添加shiro内置过滤器
           /**
            * shiro 内置过滤器：可以实现权限相关的拦截器
            *  常用的过滤器：
            *      anon：无需认证（登录）可以访问
            *      authc：必须认证才可以访问
            *      user：如果使用rememberMe的功能直接访问
            *      perms：该资源必须得到资源权限才可以访问
            *      role：该资源必须得到角色权限才可以访问
            */
   
           //过滤链
           Map<String,String> filterMap = new LinkedHashMap<String,String>();
           filterMap.put("/add","authc");
           filterMap.put("/update","authc");
           //指定拦截某些方法
           //默认拦截 所有 /*
           filterMap.put("/admin/*","authc");
           shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
   
           //修改默认登录的访问地址
           shiroFilterFactoryBean.setLoginUrl("/login");
   
           //设置安全管理器
           shiroFilterFactoryBean.setSecurityManager(securityManager);
   
           return shiroFilterFactoryBean;
       }
   
       /**
        * 创建DefaultWebSecurityManager
        * 加入 Bean 注解 将DefaultWebSecurityManager类放入 spring 容器中
        * 便于 主体器（Subject ） 关联 securityManager（管理器）
        */
       @Bean(name="securityManager")
       public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
           DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
           //关联realm
           securityManager.setRealm(userRealm);
           return securityManager;
       }
   
       /**
        * 创建 realm
        * 加入 Bean 注解 将UserRealm类放入 spring 容器中
        * 便于 安全管理器（securityManager） 关联 规则
        */
       @Bean(name = "userRealm")
       public UserRealm getRealm(){
           return new UserRealm();
       }
   
   }
   
   ~~~

5. 实现用户认证

   1. 获取Subject
      1. Subject subject = SecurityUtils.getSubject();
   2. 封张用户数据
      1. UsernamePasswordToken token = new UsernamePasswordToken (name,password)
   3. 执行登录方法
      1. subject.login(token) //try ... catch.. 
         1.  UnknownAccountException 异常 （登录失败，用户名不存在）
         2. IncorrectCredentialsException 异常（密码错误）
         3. 。。。

6. 编写Realm的判断逻辑

   ~~~ java
   /**
    * 自定义 Realm
    * @author OYL
    */
   public class UserRealm extends AuthorizingRealm {
   
       /**
        * 执行授权逻辑
        */
       @Override
       protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
           System.out.println("执行授权逻辑");
           return null;
       }
   
       /**
        *
        * 执行认证逻辑
        */
       @Override
       protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
           System.out.println("执行认证逻辑");
           //模拟数据库数据
           String name = "oukele";
           String password = "oukele";
   
           // 编写 shiro判断逻辑 判断用户名和密码
           //1.判断用户名
           UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
           if( !token.getUsername().equals(name) ){
               //用户名不存在
               return null;//shiro底层会抛出UnknowAccountException
           }
   
           /**
            *  返回数据信息，系统自动比较输入密码
            *  第一个参数 返回login的信息 token.login();
            *  第二个参数 数据库获取的密码
            *  第三个参数 realm的名称
            */
           return new SimpleAuthenticationInfo("",password,"");
       }
   
   }
   
   ~~~

7. 整合 MyBatis 实现登录

   +  导入相关依赖

     ~~~ xml
             <!--druid 连接池-->
             <dependency>
                 <groupId>com.alibaba</groupId>
                 <artifactId>druid</artifactId>
                 <version>1.1.10</version>
             </dependency>
     
             <!--mybatis-->
             <dependency>
                 <groupId>org.mybatis.spring.boot</groupId>
                 <artifactId>mybatis-spring-boot-starter</artifactId>
                 <version>1.3.2</version>
             </dependency>
     
             <!--数据库驱动-->
             <dependency>
                 <groupId>org.mariadb.jdbc</groupId>
                 <artifactId>mariadb-java-client</artifactId>
                 <version>2.3.0</version>
             </dependency>
     ~~~

   + 1

8. 配置application

   ~~~ xml
   # 连接数据库
   spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
   spring.datasource.url=jdbc:mariadb://localhost:3306/shiro
   spring.datasource.username=oukele
   spring.datasource.password=oukele
   spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
   
   #配置mybatis
   mybatis.type-aliases-package=com.example.demo.entity
   mybatis.mapper-locations=classpath:mapper/*.xml
   ~~~

9. 编写实体类

   ~~~ java
   public class User {
   
      private int id;
      private String name;
      private String password;
   
       public int getId() {
           return id;
       }
   
       public void setId(int id) {
           this.id = id;
       }
   
       public String getName() {
           return name;
       }
   
       public void setName(String name) {
           this.name = name;
       }
   
       public String getPassword() {
           return password;
       }
   
       public void setPassword(String password) {
           this.password = password;
       }
   }
   
   ~~~

10. 编写接口

    ~~~ java
    @Repository
    public interface UserMapper {
        public User findByName(String name);
    }
    
    ~~~

11. 创建 mybatis sql xml文件

    ~~~ xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.example.demo.dao.UserMapper">
    
        <select id="findByName" resultType="com.example.demo.entity.User">
            select id,name,password from user where name =#{name}
        </select>
    </mapper>
    ~~~

12. 创建 业务类

     ~~~ java
    public interface UserService {
        User findByName(String name);
    }
    
    /**
     * UserMapper接口的业务逻辑类
     *
     * @author OYL
     * @create 2019-02-21 9:02
     */
    @Service
    public class UserServiceIpml implements UserService {
    
        @Autowired
        private UserMapper userMapper;
    
        @Override
        public User findByName(String name) {
            return userMapper.findByName(name);
        }
    }
    
    
    
     ~~~

13. 在 springboot 启动类 中 加入 @MapperScan 扫描 dao包中的接口

14. 修改 UserRealm 类

    ~~~ java
    /**
     * 自定义 Realm
     * @author OYL
     */
    public class UserRealm extends AuthorizingRealm {
    
        @Autowired
        private UserServiceIpml serviceIpml;
    
        /**
         * 执行授权逻辑
         */
        @Override
        protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
            System.out.println("执行授权逻辑");
            return null;
        }
    
        /**
         *
         * 执行认证逻辑
         */
        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
            System.out.println("执行认证逻辑");
    
            // 编写 shiro判断逻辑 判断用户名和密码
            //1.判断用户名
            UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
            User user = serviceIpml.findByName(token.getUsername());
            if( user == null ){
                //用户名不存在
                return null;//shiro底层会抛出UnknowAccountException
            }
    
            /**
             *  返回数据信息，系统自动比较输入密码
             *  第一个参数 返回login的信息 token.login();
             *  第二个参数 数据库获取的密码
             *  第三个参数 realm的名称
             */
            return new SimpleAuthenticationInfo("",user.getPassword(),"");
        }
    
    }
    
    ~~~

15. 编写 Usercontroller 类

    ~~~ java
    @RestController
    public class UserController {
        /*
        *  测试方法
        * */
        @GetMapping(path = "/hello")
        public String hello(){
            System.out.println("UserController.hello()");
            return "ok";
        }
    
        /**
         *  测试 新增方法
         */
        @GetMapping(path = "/add")
        public String add(){
            return "新增成功";
        }
    
        /**
         *  测试 更新方法
         */
        @GetMapping(path = "/update")
        public String update(){
            return "更新成功";
        }
    
        /**
         *  测试 登录方法
         */
        @PostMapping(path = "/login")
        public String login(@RequestParam("name") String name ,@RequestParam("password") String password){
    
            /**
             * 使用 shiro编写认证操作
             */
            //1.获取Subject
            Subject subject = SecurityUtils.getSubject();
    
            //封张用户数据
            UsernamePasswordToken token = new UsernamePasswordToken(name,password);
    
            //执行登录
            try{
                subject.login(token);
                //登录成功
                return "{msg:登陆成功}";
            }catch (UnknownAccountException e){
                return "{msg:账号不存在}";
            }catch (IncorrectCredentialsException e){
                return "{msg:密码错误}";
            }
        }
    
        /**
         *  管理员方法
         */
        @GetMapping(path = "/admin/add")
        public String adminAdd(){
            return "某位管理员点击了新增方法";
        }
        @GetMapping(path = "/admin/update")
        public String adminUpdate(){
            return "某位管理员点击了更新方法";
        }
    
        /**
         *  根据用户名 查询数据
         * @param name
         * @return User
         */
        @Autowired
        private UserServiceIpml userServiceIpml;
    
        @GetMapping(path = "/get/{name}")
        public User findByName(@PathVariable("name") String name){
            return userServiceIpml.findByName(name);
        }
    
    }
    
    ~~~

#### Spring Boot  与 Shiro 整合 实现用户授权

1. 使用Shiro内置过滤器拦截资源

   ~~~ java
   /**
    * Shiro的配置类
    * @author OYL
    */
   
   @Configuration
   public class ShiroConfig {
       /**
        *  创建 ShiroFilterFactoryBean
        */
       @Bean
       public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
           ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
   
           //添加shiro内置过滤器
           /**
            * shiro 内置过滤器：可以实现权限相关的拦截器
            *  常用的过滤器：
            *      anon：无需认证（登录）可以访问
            *      authc：必须认证才可以访问
            *      user：如果使用rememberMe的功能直接访问
            *      perms：该资源必须得到资源权限才可以访问
            *      role：该资源必须得到角色权限才可以访问
            */
   
           //过滤链
           Map<String,String> filterMap = new LinkedHashMap<String,String>();
   
           //授权过滤器
           //注意：当前授权拦截后 shiro会自动会跳转到未授权页面
           filterMap.put("/add","perms[user:add]");
            shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
   
           //修改默认登录的访问地址
           shiroFilterFactoryBean.setLoginUrl("/login");
           //修改 未授权提示地址
           shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");
   
           //设置安全管理器
           shiroFilterFactoryBean.setSecurityManager(securityManager);
   
           return shiroFilterFactoryBean;
       }
   
       /**
        * 创建DefaultWebSecurityManager
        * 加入 Bean 注解 将DefaultWebSecurityManager类放入 spring 容器中
        * 便于 主体器（Subject ） 关联 securityManager（管理器）
        */
       @Bean(name="securityManager")
       public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
           DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
           //关联realm
           securityManager.setRealm(userRealm);
           return securityManager;
       }
   
       /**
        * 创建 realm
        * 加入 Bean 注解 将UserRealm类放入 spring 容器中
        * 便于 安全管理器（securityManager） 关联 规则
        */
       @Bean(name = "userRealm")
       public UserRealm getRealm(){
           return new UserRealm();
       }
   
   }
   
   ~~~

#### 完成shiro  的资源授权

~~~ java
/**
 * 自定义 Realm
 * @author OYL
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserServiceIpml serviceIpml;

    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");

        // 给 资源进行授权
        SimpleAuthorizationInfo info =new SimpleAuthorizationInfo();
        //添加资源的授权字符串
        //info.addStringPermission("user:add");

        //获取数据库查询当前登录用户的授权字符串
        //获取当前登录用户
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        User byName = serviceIpml.findByName(user.getName());
        info.addStringPermission(byName.getPermit());

        return info;
    }

    /**
     *
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");

        // 编写 shiro判断逻辑 判断用户名和密码
        //1.判断用户名
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        User user = serviceIpml.findByName(token.getUsername());
        if( user == null ){
            //用户名不存在
            return null;//shiro底层会抛出UnknowAccountException
        }

        /**
         *  返回数据信息，系统自动比较输入密码
         *  第一个参数 返回login的信息 token.login();
         *  第二个参数 数据库获取的密码
         *  第三个参数 realm的名称
         */
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }

}

~~~

~~~java
/**
 * Shiro的配置类
 * @author OYL
 */

@Configuration
public class ShiroConfig {
    /**
     *  创建 ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //添加shiro内置过滤器
        /**
         * shiro 内置过滤器：可以实现权限相关的拦截器
         *  常用的过滤器：
         *      anon：无需认证（登录）可以访问
         *      authc：必须认证才可以访问
         *      user：如果使用rememberMe的功能直接访问
         *      perms：该资源必须得到资源权限才可以访问
         *      role：该资源必须得到角色权限才可以访问
         */

        //过滤链
        Map<String,String> filterMap = new LinkedHashMap<String,String>();
//        filterMap.put("/add","authc");
//        filterMap.put("admin/update","authc");

        //授权过滤器
        //注意：当前授权拦截后 shiro会自动会跳转到未授权页面
        filterMap.put("/add","perms[user:add]");
        filterMap.put("/update","perms[user:update]");
        //指定拦截某些方法
        //默认拦截 所有 /*
        filterMap.put("/admin/*","authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        //修改默认登录的访问地址
        shiroFilterFactoryBean.setLoginUrl("/login");
        //修改 未授权提示地址
        shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");

        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        return shiroFilterFactoryBean;
    }

    /**
     * 创建DefaultWebSecurityManager
     * 加入 Bean 注解 将DefaultWebSecurityManager类放入 spring 容器中
     * 便于 主体器（Subject ） 关联 securityManager（管理器）
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建 realm
     * 加入 Bean 注解 将UserRealm类放入 spring 容器中
     * 便于 安全管理器（securityManager） 关联 规则
     */
    @Bean(name = "userRealm")
    public UserRealm getRealm(){
        return new UserRealm();
    }

}

~~~







