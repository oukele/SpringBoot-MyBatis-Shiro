package com.example.demo.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

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
